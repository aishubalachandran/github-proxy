package com.personal.nfx.githubproxy;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class GithubProxyApplication extends Application<ServiceConfiguration> {

	@Override
	public void run(ServiceConfiguration configuration, Environment env)
			throws Exception {
	}
	
	@Override
	public String getName() {
		return "github-proxy";
	}
	
	@Override
	public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
		
	}

	public static void main(String... args) throws Exception {
		new GithubProxyApplication().run(args);
	}
}
