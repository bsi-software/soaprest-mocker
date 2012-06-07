package net.sf.jaceko.mock.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;


@Path("/endpoint/rest/{serviceName}")
public class RestEndpointResource {

	private WebserviceMockSvcLayer svcLayer;

	@GET
	@Produces(MediaType.TEXT_XML)
	public String performGetRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request) {
		return svcLayer.performRequest(serviceName, HttpMethod.GET.toString(), "", request.getQueryString());
	}
	
	@POST
	@Produces(MediaType.TEXT_XML)
	public String performPostRequest(@PathParam("serviceName") String serviceName, @Context HttpServletRequest request) {
		return svcLayer.performRequest(serviceName, HttpMethod.POST.toString(), "", request.getQueryString());
		
	}

	public void setRestserviceMockSvcLayer(WebserviceMockSvcLayer service) {
		this.svcLayer = service;
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer svcLayer) {
		this.svcLayer = svcLayer;
	}

}
