package com.personal.nfx.githubproxy;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.bendb.dropwizard.redis.JedisBundle;
import com.bendb.dropwizard.redis.JedisFactory;
import com.personal.nfx.githubproxy.resources.CustomViewResource;
import com.personal.nfx.githubproxy.resources.GithubOrganizationResource;

public class GithubProxyApplication extends Application<ServiceConfiguration> {

	@Override
	public void run(ServiceConfiguration configuration, Environment env)
			throws Exception {
		env.jersey().register(new GithubOrganizationResource());
		env.jersey().register(new CustomViewResource());
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
			public JedisFactory getJedisFactory(ServiceConfiguration serviceConfiguration) {
				return serviceConfiguration.getRedis();
			}
		});
	}

	public static void main(String... args) throws Exception {
		new GithubProxyApplication().run(args);
	}
}
