package net.sf.jaceko.mock.resource;


import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;



@Path("/{serviceName}/{operationId}/recorded")
public class RecordedRequestsResource {
	
	
	private WebserviceMockSvcLayer service;
	@GET
	@Path("/requests")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedRequests(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {

		Collection<String> recordedRequests = service.getRecordedRequestBodies(serviceName, operationId);
		return buildRequestsXml(recordedRequests);
	}
	
	@GET
	@Path("/url-request-params")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedUrlParams(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
		Collection<String> recordedUrlParams = service.getRecordedUrlParams(serviceName, operationId);
		return buildRequestParamsXml(recordedUrlParams);
	}


	private String buildRequestsXml(Collection<String> recordedRequests) {
		StringBuilder builder = new StringBuilder();
		builder.append("<requests>\n");
		for (String request : recordedRequests) {
			builder.append(request);
		}
		builder.append("</requests>");
		return builder.toString();
	}
	

	private String buildRequestParamsXml(Collection<String> recordedUrlParams) {
		StringBuilder builder = new StringBuilder();
		builder.append("<urlRequestParams>\n");
		for (String urlQueryString : recordedUrlParams) {
			builder.append("<queryString>");
			builder.append("<![CDATA[");
			builder.append(urlQueryString);
			builder.append("]]>");
			builder.append("</queryString>\n");
		}
		builder.append("</urlRequestParams>");
		return builder.toString();
	}
	
	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}

}
