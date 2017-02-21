package com.personal.nfx.githubproxy.exception;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/*
 * Exception Mapper to handle and create a response for Unhandled exceptions. 
 */
public class UnhandledExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {

		Map<String, Object> errorDetailsMap = new HashMap<String, Object>();
		errorDetailsMap.put("error", exception.getMessage());

		return Response.serverError()
				.status(Response.Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.APPLICATION_JSON).entity(errorDetailsMap)
				.build();
	}

}
