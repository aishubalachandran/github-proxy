package com.personal.nfx.githubproxy;

import io.dropwizard.Configuration;

public class ServiceConfiguration extends Configuration {

	private String githubApiToken;

	private int port;

	public String getGithubApiToken() {
		return githubApiToken;
	}

	public void setGithubApiToken(String githubApiToken) {
		this.githubApiToken = githubApiToken;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
