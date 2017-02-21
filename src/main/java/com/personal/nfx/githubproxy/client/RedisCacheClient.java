package com.personal.nfx.githubproxy.client;

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

/*
 * An implementation of the ICacheClient as a Cache client for Redis.
 */
public class RedisCacheClient implements ICacheClient {

	private static final int PER_PAGE = 30;
	private static final String QUERY_PARAM_PER_PAGE = "per_page";
	private static final String QUERY_PARAM_PAGE = "page";
	private static final String REL_LINK_LAST = "last";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String AUTHORIZATION_HEADER_VAL_TOKEN = "token ";
	private static final String BASE_PATH = "/";
	private static final String ALL_MEMBERS_PATH = "/orgs/Netflix/members";
	private static final String ALL_REPOS_PATH = "/orgs/Netflix/repos";
	private static final String ORG_PATH = "/orgs/Netflix";

	private final String apiToken;
	private final String baseURL;
	private final Client client;
	private final Jedis jedis;

	private final DateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	public RedisCacheClient(String apiToken, String baseURL, Client client,
			Jedis jedis) {
		this.apiToken = apiToken;
		this.baseURL = baseURL;
		this.client = client;
		this.jedis = jedis;
	}

	@Override
	public void refreshCache() throws ParseException {
		fetchAndUpdateBaseURL();
		fetchAndUpdateOrganization();
		fetchAndUpdateAllRespositories();
		fetchAndUpdateAllMembers();
	}

	private void fetchAndUpdateAllMembers() {
		WebTarget target = client.target(this.baseURL).path(ALL_MEMBERS_PATH);
		boolean hasNext = true;
		int page = 1;
		JSONArray result = new JSONArray();

		while (hasNext) {
			Response response = getPaginatedResponse(target, page);

			Link link = response.getLink(REL_LINK_LAST);
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

	private void fetchAndUpdateAllRespositories() throws ParseException {
		WebTarget target = client.target(this.baseURL).path(ALL_REPOS_PATH);
		boolean hasNext = true;
		int page = 1;
		JSONArray consolidatedResult = new JSONArray();

		while (hasNext) {
			Response response = getPaginatedResponse(target, page);

			Link link = response.getLink(REL_LINK_LAST);
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

	private void fetchAndUpdateOrganization() {
		WebTarget target = client.target(this.baseURL).path(ORG_PATH);

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER,
						AUTHORIZATION_HEADER_VAL_TOKEN + this.apiToken).get();

		jedis.set("organization", response.readEntity(String.class));

	}

	private void fetchAndUpdateBaseURL() {
		WebTarget target = client.target(this.baseURL).path(BASE_PATH);

		Response response = target
				.request(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER,
						AUTHORIZATION_HEADER_VAL_TOKEN + this.apiToken).get();

		jedis.set("base:data", response.readEntity(String.class));
	}

	private Response getPaginatedResponse(WebTarget target, int page) {
		return target
				.queryParam(QUERY_PARAM_PAGE, page)
				.queryParam(QUERY_PARAM_PER_PAGE, PER_PAGE)
				.request(MediaType.APPLICATION_JSON)
				.header(AUTHORIZATION_HEADER,
						AUTHORIZATION_HEADER_VAL_TOKEN + this.apiToken).get();
	}
}
