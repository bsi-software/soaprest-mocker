package net.sf.jaceko.mock.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;
import org.apache.commons.httpclient.HttpStatus;


@Path("/{serviceName}/{operationId}/setup")
public class MockSetupResource {


	private WebserviceMockSvcLayer service;

	
    @POST
	@Path("/response")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response setUpResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @QueryParam("code") int customResponseCode, @QueryParam("delay") int delaySec, String customResponseBody) {
    	service.setCustomResponse(serviceName, operationId, 1, new MockResponse(customResponseBody, customResponseCode, delaySec));
		return Response.status(HttpStatus.SC_OK).build();
	}
    
    @POST
	@Path("/consecutive-response/{requestInOrder}")
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response setUpResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @PathParam("requestInOrder") int requestInOrder,
    		@QueryParam("code") int customResponseCode, @QueryParam("delay") int delaySec, String customResponseBody) {
		service.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse(customResponseBody, customResponseCode, delaySec));
		return Response.status(HttpStatus.SC_OK).build();
	}

    @POST
	@Path("/init")
	public Response initMock(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
		service.initMock(serviceName, operationId);
		return Response.status(HttpStatus.SC_OK).build();
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}

	

}
