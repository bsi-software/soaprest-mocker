package net.sf.jaceko.mock.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;


@Path("/{serviceName}/wsdl")
public class WsdlExposingResource {

	private WebserviceMockSvcLayer service;

	@GET
	@Produces(MediaType.TEXT_XML)
	public String getWsdl(@PathParam("serviceName") String serviceName) {
		return service.getWsdl(serviceName);
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}


}
