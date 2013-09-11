/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Service.
 *
 *     SOAP/REST Mock Service is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Service is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Service; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/services/REST/{serviceName}/operations/{operationId}")
public class RestServiceMockVerificatonResource extends BasicVerifictationResource {

	@GET
	@Path("/recorded-resource-ids")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedResourceIds(@PathParam("serviceName") String serviceName, @PathParam("operationId")  String operationId) {
		Collection<String> recordedResourceIds = recordedRequestsHolder
				.getRecordedResourceIds(serviceName, operationId);
		String rootElementName = "recorded-resource-ids";
		String elementName = "recorded-resource-id";
		boolean surroundElementTextWithCdata = false;
		return buildListXml(recordedResourceIds, rootElementName, elementName,
				surroundElementTextWithCdata);
	}
	
	@GET
	@Path("/recorded-request-params")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedUrlParams(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
		Collection<String> recordedUrlParams = recordedRequestsHolder.getRecordedUrlParams(
				serviceName, operationId);
		return buildRequestParamsXml(recordedUrlParams);
	}

	private String buildRequestParamsXml(Collection<String> recordedUrlParams) {
		String rootElementName = "recorded-request-params";
		String elementName = "recorded-request-param";
		boolean surroundElementTextWithCdata = true;
		return buildListXml(recordedUrlParams, rootElementName, elementName,
				surroundElementTextWithCdata);
	}

}
