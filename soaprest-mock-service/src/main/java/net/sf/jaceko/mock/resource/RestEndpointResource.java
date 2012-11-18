/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Servce.
 *
 *     SOAP/REST Mock Servce is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Servce is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Servce; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

@Path("/endpoint/REST/{serviceName}")
public class RestEndpointResource {
	private static final Logger LOG = Logger.getLogger(RestEndpointResource.class);

	private WebserviceMockSvcLayer svcLayer;

	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request) {
		return performGetRequest(serviceName, request, null);

	}

	@GET
	@Path("/{resourceId}")
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request,
			@PathParam("resourceId") String resourceId) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.GET.toString(), "", request.getQueryString(),
				resourceId);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);
	}

	@POST
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performPostRequest(@PathParam("serviceName") String serviceName,
			@Context HttpServletRequest httpServletRequest, String request) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), request,
				httpServletRequest.getQueryString(), null);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);

	}

	@PUT
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performPutRequest(@PathParam("serviceName") String serviceName, String request) {
		return performPutRequest(serviceName, null, request);
	}

	@PUT
	@Path("/{resourceId}")
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performPutRequest(@PathParam("serviceName") String serviceName, @PathParam("resourceId") String resourceId,
			String request) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.PUT.toString(), request, null, resourceId);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);
	}

	@DELETE
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performDeleteRequest(@PathParam("serviceName") String serviceName) {
		return performDeleteRequest(serviceName, null);

	}

	@DELETE
	@Path("/{resourceId}")
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response performDeleteRequest(@PathParam("serviceName") String serviceName, @PathParam("resourceId") String resourceId) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.DELETE.toString(), "", null, resourceId);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);

	}

	private Response buildWebserviceResponse(MockResponse response) {
		return Response.status(response.getCode()).entity(response.getBody()).build();
	}

	public void setRestserviceMockSvcLayer(WebserviceMockSvcLayer service) {
		this.svcLayer = service;
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer svcLayer) {
		this.svcLayer = svcLayer;
	}

}
