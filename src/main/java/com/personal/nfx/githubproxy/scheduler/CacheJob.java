package com.personal.nfx.githubproxy.scheduler;

import java.text.ParseException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.personal.nfx.githubproxy.GithubProxyClient;

public class CacheJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		GithubProxyClient githubProxyClient = (GithubProxyClient) context
				.getJobDetail().getJobDataMap().get("githubProxyClient");

		try {
			System.out.println("Refreshing cache - start");
			githubProxyClient.fetchDataFromGithub();
			System.out.println("Refreshing cache - finish");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
