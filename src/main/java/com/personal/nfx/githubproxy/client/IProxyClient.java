package com.personal.nfx.githubproxy.client;

import javax.ws.rs.core.Response;

public interface IProxyClient {

	Response getBaseResource();

	Response getAllMembers();

	Response getOrganization();

	Response getAllOrgRepos();

	Response getTopNForks(int n);

	Response getTopNOpenIssues(int n);

	Response getTopNWatchers(int n);

	Response getTopNStars(int n);

	Response getTopNLastUpdated(int n);

	Response proxy(String path);

}
