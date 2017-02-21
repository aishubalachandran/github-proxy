package com.personal.nfx.githubproxy;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Configuration for the service. 
 */
public class ServiceConfiguration extends Configuration {

	private String githubApiToken;

	private String githubApiBaseURL;

	private int port;

	@NotNull
	@JsonProperty
	private JedisFactory redis;

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

	public JedisFactory getRedis() {
		return redis;
	}

	public void setRedis(JedisFactory redis) {
		this.redis = redis;
	}

	public String getGithubApiBaseURL() {
		return githubApiBaseURL;
	}

	public void setGithubApiBaseURL(String githubApiBaseURL) {
		this.githubApiBaseURL = githubApiBaseURL;
	}
}
