package com.personal.nfx.githubproxy.resources;

import java.io.IOException;
import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

@Path("/view/top")
public class CustomViewResource {

	private final Jedis jedis;

	public CustomViewResource(Jedis jedis) {
		this.jedis = jedis;
	}

	@Path("/{result_count}/forks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNForks(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort("all:repos",
				new SortingParams().by("repo:*->forksCount")
						.get("repo:*->data").limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Path("/{result_count}/last_updated")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNLastUpdated(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort("all:repos",
				new SortingParams().by("repo:*->updatedAt").get("repo:*->data")
						.limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Path("/{result_count}/open_issues")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNOpenIssues(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->openIssuesCount")
						.get("repo:*->data").limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Path("/{result_count}/stars")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNStars(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->starGazersCount")
						.get("repo:*->data").limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Path("/{result_count}/watchers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNWatchers(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->watcherCount")
						.get("repo:*->data").limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	private JSONArray getJsonArray(List<String> result) {
		JSONArray amg1 = new JSONArray();

		for (String string : result) {
			JSONObject jsonObject = new JSONObject(string);
			amg1.put(jsonObject);
		}
		return amg1;
	}
}
