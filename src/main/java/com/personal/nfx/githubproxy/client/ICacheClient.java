package com.personal.nfx.githubproxy.client;

import java.text.ParseException;

public interface ICacheClient {

	/*
	 * Trigger the set of actions to refresh cache.
	 */
	void refreshCache() throws ParseException;
}
