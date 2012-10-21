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

import org.apache.log4j.Logger;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;


@Path("/endpoint/rest/{serviceName}")
public class RestEndpointResource {
	private static final Logger LOG = Logger.getLogger(RestEndpointResource.class);

	private WebserviceMockSvcLayer svcLayer;

	@GET
	@Produces(MediaType.TEXT_XML)
	public String performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request) {
		String response =  svcLayer.performRequest(serviceName, HttpMethod.GET.toString(), "", request.getQueryString());
		LOG.debug("serviceName: " + serviceName + ", response:" + response);
		return response;

	}
	
	@POST
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String performPostRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest httpServletRequest, String request) {
		String response =  svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), request, httpServletRequest.getQueryString());
		LOG.debug("serviceName: " + serviceName + ", response:" + response);
		return response;

	}

	@PUT
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String performPutRequest(@PathParam("serviceName") String serviceName, String request) {
		String response =  svcLayer.performRequest(serviceName, HttpMethod.PUT.toString(), request, null);
		return response;
		
	}
	public void setRestserviceMockSvcLayer(WebserviceMockSvcLayer service) {
		this.svcLayer = service;
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer svcLayer) {
		this.svcLayer = svcLayer;
	}


}
