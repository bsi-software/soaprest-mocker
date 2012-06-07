package net.sf.jaceko.mock.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.apache.http.HttpStatus;


@Path("/{serviceName}/{operationId}/setup")
public class MockSetupResource {


	private WebserviceMockSvcLayer service;

	
    @POST
	@Path("/response")
    @Consumes(MediaType.TEXT_XML)
	public Response setUpResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, String customResponse) {
		service.setCustomResponse(serviceName, operationId, 1, customResponse);
		return Response.status(HttpStatus.SC_OK).build();
	}
    
    @POST
	@Path("/consecutive-response/{requestInOrder}")
    @Consumes(MediaType.TEXT_XML)
    public Response setUpResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @PathParam("requestInOrder") int requestInOrder,
			String customResponse) {
		service.setCustomResponse(serviceName, operationId, requestInOrder, customResponse);
		return Response.status(HttpStatus.SC_OK).build();
	}

    @POST
	@Path("/delay")
    public Response setUpRequestDelay(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, int delaySec) {
		service.setRequestDelay(serviceName, operationId, delaySec);
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
