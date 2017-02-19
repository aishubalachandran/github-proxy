package com.personal.nfx.githubproxy;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.jersey.jackson.JacksonMessageBodyProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.personal.nfx.githubproxy.client.GithubProxyClient;
import com.personal.nfx.githubproxy.client.RedisCacheClient;
import com.personal.nfx.githubproxy.exception.UnhandledExceptionMapper;
import com.personal.nfx.githubproxy.resources.GithubProxyResource;
import com.personal.nfx.githubproxy.resources.ServiceHealthCheck;
import com.personal.nfx.githubproxy.scheduler.CacheRefreshJob;

public class GithubProxyApplication extends Application<ServiceConfiguration> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GithubProxyApplication.class);

	private static final int CACHE_REFRESH_IN_MINUTES = 15;
	private static JedisPool pool;
	private Jedis jedis;
	private Client client;
	private GithubProxyClient githubProxyClient;
	private RedisCacheClient redisCacheClient;

	@Override
	public void run(ServiceConfiguration configuration, Environment env)
			throws Exception {
		LOGGER.debug("---- Starting Run ----");

		client = ClientBuilder.newBuilder().build();

		githubProxyClient = new GithubProxyClient(
				configuration.getGithubApiToken(),
				configuration.getGithubApiBaseURL(), client, jedis);

		redisCacheClient = new RedisCacheClient(
				configuration.getGithubApiToken(),
				configuration.getGithubApiBaseURL(), client, jedis);

		scheduleCacheUpdate();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		env.jersey().register(new JacksonMessageBodyProvider(mapper));
		env.jersey().register(new GithubProxyResource(githubProxyClient));
		env.healthChecks()
				.register("healthcheck", new ServiceHealthCheck(pool));
		env.jersey().register(new UnhandledExceptionMapper());

		LOGGER.debug("---- Run Complete -- Ready ----");
	}

	private void scheduleCacheUpdate() throws SchedulerException {

		LOGGER.debug("---- Scheduling Cache ----");

		JobDetail job = JobBuilder.newJob(CacheRefreshJob.class)
				.withIdentity("cacheJob").build();
		job.getJobDataMap().put("cacheClient", redisCacheClient);

		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withSchedule(
						SimpleScheduleBuilder
								.simpleSchedule()
								.withIntervalInMinutes(CACHE_REFRESH_IN_MINUTES)
								.repeatForever()).build();

		// schedule the job
		SchedulerFactory schFactory = new StdSchedulerFactory();
		Scheduler sch = schFactory.getScheduler();
		sch.start();
		sch.scheduleJob(job, trigger);

		LOGGER.debug("---- Cache Scheduled ----");
	}

	@Override
	public String getName() {
		return "github-proxy";
	}

	@Override
	public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
		LOGGER.debug("---- Init Start ----");

		bootstrap
				.setConfigurationSourceProvider(new SubstitutingSourceProvider(
						bootstrap.getConfigurationSourceProvider(),
						new EnvironmentVariableSubstitutor(false)));

		bootstrap.addBundle(new JedisBundle<ServiceConfiguration>() {
			public JedisFactory getJedisFactory(
					ServiceConfiguration serviceConfiguration) {
				return serviceConfiguration.getRedis();
			}
		});

		pool = new JedisPool(new JedisPoolConfig(), "localhost");

		jedis = pool.getResource();

		LOGGER.debug("---- Init Done ----");
	}

	public static void main(String... args) throws Exception {
		new GithubProxyApplication().run(args);
	}
}
