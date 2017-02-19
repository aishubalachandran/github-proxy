package com.personal.nfx.githubproxy.client;

import java.text.ParseException;

public interface ICacheClient {

	void refreshCache() throws ParseException;
}
