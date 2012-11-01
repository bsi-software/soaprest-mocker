package net.sf.jaceko.mock.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

@Path("/endpoint/rest/{serviceName}")
public class RestEndpointResource {
	private static final Logger LOG = Logger.getLogger(RestEndpointResource.class);

	private WebserviceMockSvcLayer svcLayer;

	@GET
	@Produces(MediaType.TEXT_XML)
	public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request) {
		return performGetRequest(serviceName, request, null);

	}

	@GET
	@Path("/{resourceId}")
	@Produces(MediaType.TEXT_XML)
	public Response performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request,
			@PathParam("resourceId") String resourceId) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.GET.toString(), "", request.getQueryString(),
				resourceId);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);
	}

	@POST
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public Response performPostRequest(@PathParam("serviceName") String serviceName,
			@Context HttpServletRequest httpServletRequest, String request) {
		MockResponse mockResponse = svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), request,
				httpServletRequest.getQueryString(), null);
		LOG.debug("serviceName: " + serviceName + ", response:" + mockResponse);
		return buildWebserviceResponse(mockResponse);

	}

	@PUT
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String performPutRequest(@PathParam("serviceName") String serviceName, String request) {
		String response = svcLayer.performRequest(serviceName, HttpMethod.PUT.toString(), request, null, null).getBody();
		return response;

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
