package com.personal.nfx.githubproxy.resources;

import java.io.IOException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;

import com.personal.nfx.githubproxy.client.IProxyClient;

@Path("/")
public class GithubProxyResource {

	private final IProxyClient proxyClient;

	public GithubProxyResource(IProxyClient proxyClient) {
		this.proxyClient = proxyClient;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBase() throws IOException {
		return proxyClient.getBaseResource();
	}

	@Path("orgs/{org_name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrg(
			@PathParam("org_name") @NotNull @NotEmpty String orgName)
			throws IOException {
		return proxyClient.getOrganization();
	}

	@Path("orgs/{org_name}/members")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgMembers(
			@PathParam("org_name") @NotNull @NotEmpty String orgName) {
		return proxyClient.getAllMembers();
	}

	@Path("orgs/{org_name}/repos")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgRepos(
			@PathParam("org_name") @NotNull @NotEmpty String orgName)
			throws IOException {

		return proxyClient.getAllOrgRepos();
	}

	@Path("view/top/{result_count}/forks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNForks(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {
		return proxyClient.getTopNForks(resultCount);
	}

	@Path("view/top/{result_count}/last_updated")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNLastUpdated(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		return proxyClient.getTopNLastUpdated(resultCount);
	}

	@Path("view/top/{result_count}/open_issues")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNOpenIssues(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		return proxyClient.getTopNOpenIssues(resultCount);
	}

	@Path("view/top/{result_count}/stars")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNStars(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		return proxyClient.getTopNStars(resultCount);
	}

	@Path("view/top/{result_count}/watchers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNWatchers(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		return proxyClient.getTopNWatchers(resultCount);
	}

	@Path("{other: [a-zA-Z0-9_/]+}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxy(@PathParam("other") String other) throws IOException {
		return proxyClient.proxy(other);
	}
}
