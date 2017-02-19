package com.personal.nfx.githubproxy;

import java.util.Set;

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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Sets;
import com.personal.nfx.githubproxy.resources.CustomViewResource;
import com.personal.nfx.githubproxy.resources.GithubOrganizationResource;
import com.personal.nfx.githubproxy.resources.ServiceHealthCheck;
import com.personal.nfx.githubproxy.scheduler.CacheJob;

public class GithubProxyApplication extends Application<ServiceConfiguration> {

	private static JedisPool pool;
	private Jedis jedis;
	private Client client;
	private GithubProxyClient githubProxyClient;

	@Override
	public void run(ServiceConfiguration configuration, Environment env)
			throws Exception {
		client = ClientBuilder.newBuilder().build();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		env.jersey().register(new JacksonMessageBodyProvider(mapper));
		githubProxyClient = new GithubProxyClient(
				configuration.getGithubApiToken(),
				configuration.getGithubApiBaseURL(), client, jedis);

		scheduleCacheUpdate();

		env.jersey().register(new GithubOrganizationResource(jedis));
		env.jersey().register(new CustomViewResource(jedis));
		env.healthChecks()
				.register("healthcheck", new ServiceHealthCheck(pool));

	}

	private void scheduleCacheUpdate() throws SchedulerException {

		JobDetail job = JobBuilder.newJob(CacheJob.class)
				.withIdentity("cacheJob").build();
		job.getJobDataMap().put("githubProxyClient", githubProxyClient);

		// specify the running period of the job
		// TODO: Clean up and make minutes constant
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
								.withIntervalInMinutes(15).repeatForever())
				.build();

		// schedule the job
		SchedulerFactory schFactory = new StdSchedulerFactory();
		Scheduler sch = schFactory.getScheduler();
		sch.start();
		sch.scheduleJob(job, trigger);
	}

	@Override
	public String getName() {
		return "github-proxy";
	}

	@Override
	public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
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

	}

	public static void main(String... args) throws Exception {
		new GithubProxyApplication().run(args);
	}
}
