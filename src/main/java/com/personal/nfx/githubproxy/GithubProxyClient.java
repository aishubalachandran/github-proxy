package com.personal.nfx.githubproxy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;

public class GithubProxyClient {

	private static final int PER_PAGE = 30;

	private final String apiToken;

	private final String baseURL;

	private Client client;

	private Jedis jedis;

	private DateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public GithubProxyClient(String apiToken, String baseURL, Client client,
			Jedis jedis) {
		this.apiToken = apiToken;
		this.baseURL = baseURL;
		this.client = client;
		this.jedis = jedis;
	}

	public void fetchDataFromGithub() throws ParseException {

		getOrganization();

		getAllRepositories();

		getAllMembers();

	}

	private void getAllMembers() {
		WebTarget target = client.target(this.baseURL).path(
				"/orgs/Netflix/members");
		boolean hasNext = true;
		int page = 1;
		JSONArray result = new JSONArray();

		while (hasNext) {
			Response response = target.queryParam("page", page)
					.queryParam("per_page", PER_PAGE)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "token " + this.apiToken).get();

			Link link = response.getLink("last");
			if (link != null) {
				page++;
			} else {
				hasNext = false;
			}

			JSONArray resultArray = new JSONArray(
					response.readEntity(String.class));
			for (int i = 0; i < resultArray.length(); i++) {
				result.put(resultArray.get(i));
			}
		}

		jedis.set("all:members", result.toString());
	}

	// TODO : Optimize the logic to write to redis only once after iterating
	// over all pages.
	private void getAllRepositories() throws ParseException {
		WebTarget target = client.target(this.baseURL).path(
				"/orgs/Netflix/repos");
		boolean hasNext = true;
		int page = 1;
		JSONArray consolidatedResult = new JSONArray();

		while (hasNext) {
			Response response = target.queryParam("page", page)
					.queryParam("per_page", PER_PAGE)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", "token " + this.apiToken).get();

			Link link = response.getLink("last");
			if (link != null) {
				page++;
			} else {
				hasNext = false;
			}

			JSONArray resultArray = new JSONArray(
					response.readEntity(String.class));
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) resultArray.get(i);
				int repoId = jsonObject.getInt("id");
				int forksCount = jsonObject.getInt("forks_count");
				int openIssuesCount = jsonObject.getInt("open_issues_count");
				int starGazersCount = jsonObject.getInt("stargazers_count");
				int watchersCount = jsonObject.getInt("watchers_count");
				String updatedAt = jsonObject.getString("updated_at");
				String repoFullName = jsonObject.getString("full_name");

				Map<String, String> repoProperties = new HashMap<String, String>();
				repoProperties.put("forksCount", String.valueOf(forksCount));
				repoProperties.put("openIssuesCount",
						String.valueOf(openIssuesCount));
				repoProperties.put("starGazersCount",
						String.valueOf(starGazersCount));
				repoProperties.put("watchersCount",
						String.valueOf(watchersCount));
				repoProperties.put("updatedAt", String.valueOf(dateFormatter
						.parse(updatedAt).getTime()));
				repoProperties.put("fullName", repoFullName);
				repoProperties.put("data", resultArray.get(i).toString());

				consolidatedResult.put(resultArray.get(i));

				jedis.sadd("all:repos", String.valueOf(repoId));
				jedis.hmset("repo:" + repoId, repoProperties);
			}

		}

		jedis.set("all:repos:data", consolidatedResult.toString());
	}

	private void getOrganization() {
		WebTarget target = client.target(this.baseURL).path("/orgs/Netflix");

		Response response = target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "token " + this.apiToken).get();

		jedis.set("organization", response.readEntity(String.class));
	}

}
