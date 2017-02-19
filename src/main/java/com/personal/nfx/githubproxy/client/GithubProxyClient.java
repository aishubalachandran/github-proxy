package com.personal.nfx.githubproxy.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;

import com.google.common.collect.Lists;

public class GithubProxyClient implements IProxyClient {

	private final String apiToken;

	private final String baseURL;

	private final Client client;

	private final Jedis jedis;

	private final DateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public GithubProxyClient(String apiToken, String baseURL, Client client,
			Jedis jedis) {
		this.apiToken = apiToken;
		this.baseURL = baseURL;
		this.client = client;
		this.jedis = jedis;
	}

	@Override
	public Response proxy(String path) {
		WebTarget target = client.target(this.baseURL).path(path);

		return target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "token " + this.apiToken).get();

	}

	@Override
	public Response getBaseResource() {
		checkKeyExistence("base:data");
		return Response.ok(jedis.get("base:data")).build();
	}

	@Override
	public Response getAllMembers() {
		checkKeyExistence("all:members");
		return Response.ok(jedis.get("all:members")).build();
	}

	@Override
	public Response getOrganization() {
		checkKeyExistence("organization");
		return Response.ok(jedis.get("organization")).build();
	}

	@Override
	public Response getAllOrgRepos() {
		checkKeyExistence("all:repos:data");
		return Response.ok(jedis.get("all:repos:data")).build();
	}

	@Override
	public Response getTopNForks(int n) {
		checkKeyExistence("all:repos");
		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->forksCount")
						.get("repo:*->fullName", "repo:*->forksCount")
						.limit(0, n).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Override
	public Response getTopNOpenIssues(int n) {
		checkKeyExistence("all:repos");
		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->openIssuesCount")
						.get("repo:*->fullName", "repo:*->openIssuesCount")
						.limit(0, n).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Override
	public Response getTopNWatchers(int n) {
		checkKeyExistence("all:repos");
		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->watchersCount")
						.get("repo:*->fullName", "repo:*->watchersCount")
						.limit(0, n).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Override
	public Response getTopNStars(int n) {
		checkKeyExistence("all:repos");
		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->starGazersCount")
						.get("repo:*->fullName", "repo:*->starGazersCount")
						.limit(0, n).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	@Override
	public Response getTopNLastUpdated(int n) {
		checkKeyExistence("all:repos");
		List<String> result = jedis.sort(
				"all:repos",
				new SortingParams().by("repo:*->updatedAt")
						.get("repo:*->fullName", "repo:*->updatedAt")
						.limit(0, n).desc());

		JSONArray responseArray = getJsonArray(result);

		return Response.ok(responseArray.toString()).build();
	}

	private void checkKeyExistence(String key) {
		if (!jedis.exists(key)) {
			throw new RuntimeException(
					"PROBLEM WITH CACHING. RE-TRIGGER CACHING.");
		}
	}

	private JSONArray getJsonArray(List<String> result) {

		List<List<String>> partitions = Lists.partition(result, 2);
		JSONArray finalArray = new JSONArray();
		for (List<String> partition : partitions) {
			JSONArray subArray = new JSONArray();
			subArray.put(partition.get(0));

			try {
				int parseInt = Integer.parseInt(partition.get(1));
				subArray.put(parseInt);
			} catch (NumberFormatException e) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(Long.parseLong(partition.get(1)));
				subArray.put(dateFormatter.format(calendar.getTime()));
			}
			finalArray.put(subArray);
		}

		return finalArray;
	}
}
