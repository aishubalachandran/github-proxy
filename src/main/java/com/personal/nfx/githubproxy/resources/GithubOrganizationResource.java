package com.personal.nfx.githubproxy.resources;

import java.io.IOException;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;

@Path("/orgs")
public class GithubOrganizationResource {

	public GithubOrganizationResource() {
	}

	@Path("/{org_name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrg(
			@PathParam("org_name") @NotNull @NotEmpty String orgName)
			throws IOException {
		return Response.ok().build();
	}

	@Path("/{org_name}/members")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgMembers(
			@PathParam("org_name") @NotNull @NotEmpty String orgName) {
		return Response.ok().build();
	}

	@Path("/{org_name}/repos")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgRepos(
			@PathParam("org_name") @NotNull @NotEmpty String orgName) {
		return Response.ok().build();
	}
}
