package net.sf.jaceko.mock.resource;

import static java.text.MessageFormat.format;

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
	public String getRecordedRequests(
			@PathParam("serviceName") String serviceName,
			@PathParam("operationId") String operationId) {

		Collection<String> recordedRequests = service.getRecordedRequestBodies(
				serviceName, operationId);
		return buildRequestsXml(recordedRequests);
	}

	@GET
	@Path("/url-request-params")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedUrlParams(
			@PathParam("serviceName") String serviceName,
			@PathParam("operationId") String operationId) {
		Collection<String> recordedUrlParams = service.getRecordedUrlParams(
				serviceName, operationId);
		return buildRequestParamsXml(recordedUrlParams);
	}

	@GET
	@Path("/resource-ids")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedResourceIds(@PathParam("serviceName") String serviceName, @PathParam("operationId")  String operationId) {
		Collection<String> recordedResourceIds = service
				.getRecordedResourceIds(serviceName, operationId);
		String rootElementName = "resourceIds";
		String elementName = "resourceId";
		boolean surroundElementTextWithCdata = false;
		return buildListXml(recordedResourceIds, rootElementName, elementName,
				surroundElementTextWithCdata);
	}

	private String buildRequestsXml(Collection<String> recordedRequests) {
		return buildListXml(recordedRequests, "requests", null, false);
	}

	private String buildRequestParamsXml(Collection<String> recordedUrlParams) {
		String rootElementName = "urlRequestParams";
		String elementName = "queryString";
		boolean surroundElementTextWithCdata = true;
		return buildListXml(recordedUrlParams, rootElementName, elementName,
				surroundElementTextWithCdata);
	}

	private String buildListXml(Collection<String> elementValuesList,
			String rootElementName, String elementName,
			boolean surroundElementTextWithCdata) {
		StringBuilder builder = new StringBuilder();
		builder.append(format("<{0}>\n", rootElementName));
		for (String urlQueryString : elementValuesList) {
			if (elementName != null) {
				builder.append(format("<{0}>", elementName));
			}
			if (surroundElementTextWithCdata) {
				builder.append("<![CDATA[");
			}
			builder.append(urlQueryString);
			if (surroundElementTextWithCdata) {
				builder.append("]]>");
			}
			if (elementName != null) {
				builder.append(format("</{0}>\n", elementName));
			}

		}
		builder.append(format("</{0}>", rootElementName));
		return builder.toString();
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}

}
