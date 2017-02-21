package com.personal.nfx.githubproxy.client;

import javax.ws.rs.core.Response;

public interface IProxyClient {

	/*
	 * Returns Response from Base URL
	 */
	Response getBaseResource();

	/*
	 * Returns details of all Members belonging to the Organization.This
	 * response is flat and not paginated.
	 */
	Response getAllMembers();

	/*
	 * Returns details about the Organization.
	 */
	Response getOrganization();

	/*
	 * Returns details about all responses in the Organization. This response is
	 * flat and not paginated.
	 */
	Response getAllOrgRepos();

	/*
	 * Returns the Top N Forks. The Response contains the Repo name and the fork
	 * count.
	 */
	Response getTopNForks(int n);

	/*
	 * Returns the Top N Open issues. The Response contains the Repo name and
	 * the open issues count.
	 */
	Response getTopNOpenIssues(int n);

	/*
	 * Returns the Top N Forks. The Response contains the Repo name and the
	 * watchers count.
	 */
	Response getTopNWatchers(int n);

	/*
	 * Returns the Top N Stars. The Response contains the Repo name and the
	 * stars count.
	 */
	Response getTopNStars(int n);

	/*
	 * Returns the Top N Last updated. The Response contains the Repo name and
	 * the last updated timestamp.
	 */
	Response getTopNLastUpdated(int n);

	/*
	 * Proxies the call to the downstream and returns the exact response.
	 */
	Response proxy(String path);

}
