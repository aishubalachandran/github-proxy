package com.personal.nfx.githubproxy.resources;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.codahale.metrics.health.HealthCheck;

/*
 * HealthCheck resource to monitor the health of the service.
 */
public class ServiceHealthCheck extends HealthCheck {
	private final JedisPool pool;

	public ServiceHealthCheck(JedisPool pool) {
		this.pool = pool;
	}

	@Override
	protected Result check() throws Exception {
		Jedis jedis = pool.getResource();
		final String pong = jedis.ping();
		if ("PONG".equals(pong)) {
			return Result.healthy();
		}

		return Result.unhealthy("Could not ping redis");
	}
}
