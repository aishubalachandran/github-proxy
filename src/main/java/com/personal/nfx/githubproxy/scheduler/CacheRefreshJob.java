package com.personal.nfx.githubproxy.scheduler;

import java.text.ParseException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.personal.nfx.githubproxy.GithubProxyApplication;
import com.personal.nfx.githubproxy.client.ICacheClient;

public class CacheRefreshJob implements Job {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GithubProxyApplication.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		ICacheClient cacheClient = (ICacheClient) context.getJobDetail()
				.getJobDataMap().get("cacheClient");

		try {
			LOGGER.debug("---- Cache Refresh Begin ----");

			cacheClient.refreshCache();

			LOGGER.debug("---- Cache Refresh End ----");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
