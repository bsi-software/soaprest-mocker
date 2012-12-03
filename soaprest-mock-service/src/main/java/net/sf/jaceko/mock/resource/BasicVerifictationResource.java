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

import static java.text.MessageFormat.format;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.service.RecordedRequestsHolder;

public class BasicVerifictationResource {

	protected RecordedRequestsHolder recordedRequestsHolder;

	public BasicVerifictationResource() {
		super();
	}

	@GET
	@Path("/recorded-requests")
	@Produces(MediaType.TEXT_XML)
	public String getRecordedRequests(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
	
		Collection<String> recordedRequests = recordedRequestsHolder.getRecordedRequestBodies(
				serviceName, operationId);
		return buildRequestsXml(recordedRequests);
	}


	private String buildRequestsXml(Collection<String> recordedRequests) {
		return buildListXml(recordedRequests, "recorded-requests", null, false);
	}

	protected String buildListXml(Collection<String> elementValuesList, String rootElementName, String elementName, boolean surroundElementTextWithCdata) {
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

	public void setRecordedRequestsHolder(RecordedRequestsHolder recordedRequestsHolder) {
		this.recordedRequestsHolder = recordedRequestsHolder;
	}

}