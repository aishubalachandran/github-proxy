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

import redis.clients.jedis.Jedis;

@Path("/")
public class GithubOrganizationResource {

	private final Jedis jedis;

	public GithubOrganizationResource(Jedis jedis) {
		this.jedis = jedis;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBase() throws IOException {
		return Response.ok(jedis.get("base:data")).build();
	}

	@Path("orgs/{org_name}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrg(
			@PathParam("org_name") @NotNull @NotEmpty String orgName)
			throws IOException {
		return Response.ok(jedis.get("organization")).build();
	}

	@Path("orgs/{org_name}/members")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgMembers(
			@PathParam("org_name") @NotNull @NotEmpty String orgName) {
		return Response.ok(jedis.get("all:members")).build();
	}

	@Path("orgs/{org_name}/repos")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrgRepos(
			@PathParam("org_name") @NotNull @NotEmpty String orgName)
			throws IOException {

		return Response.ok(jedis.get("all:repos:data")).build();
	}
}
