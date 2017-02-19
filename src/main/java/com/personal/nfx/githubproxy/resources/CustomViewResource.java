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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

import com.google.common.collect.Lists;

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

		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->forksCount")
						.get("repo:*->fullName", "repo:*->forksCount")
						.limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Path("/{result_count}/last_updated")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopNLastUpdated(
			@PathParam("result_count") @Min(0) int resultCount)
			throws IOException {

		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->updatedAt")
						.get("repo:*->fullName", "repo:*->updatedAt")
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
						.get("repo:*->fullName", "repo:*->openIssuesCount")
						.limit(0, resultCount).desc());

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
						.get("repo:*->fullName", "repo:*->starGazersCount")
						.limit(0, resultCount).desc());

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
				new SortingParams().by("repo:*->watchersCount")
						.get("repo:*->fullName", "repo:*->watchersCount")
						.limit(0, resultCount).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	private JSONArray getJsonArray(List<String> result) {

		List<List<String>> partitions = Lists.partition(result, 2);
		JSONArray finalArray = new JSONArray();
		for (List<String> partition : partitions) {
			JSONArray subArray = new JSONArray();
			subArray.put(partition.get(0));
			subArray.put(partition.get(1));

			finalArray.put(subArray);
		}

		return finalArray;
	}
}
