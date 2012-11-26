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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.apache.commons.httpclient.HttpStatus;

public abstract class BasicSetupResource {

	private WebserviceMockSvcLayer service;

	public BasicSetupResource() {
		super();
	}

	@POST
	@Path("/{operationId}/responses")
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response addResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @QueryParam("code") int customResponseCode, @QueryParam("delay") int delaySec, String customResponseBody) {
		service.addCustomResponse(serviceName, operationId, new MockResponse(customResponseBody, customResponseCode, delaySec));
		return Response.status(HttpStatus.SC_OK).build();
	}

	@PUT
	@Path("/{operationId}/responses/{requestInOrder}")
	@Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response setResponse(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId, @PathParam("requestInOrder") int requestInOrder, @QueryParam("code") int customResponseCode, @QueryParam("delay") int delaySec, String customResponseBody) {
		service.setCustomResponse(serviceName, operationId, requestInOrder, new MockResponse(customResponseBody, customResponseCode, delaySec));
		return Response.status(HttpStatus.SC_OK).build();
	}

	@POST
	@Path("/{operationId}/init")
	public Response initMock(@PathParam("serviceName") String serviceName, @PathParam("operationId") String operationId) {
		service.initMock(serviceName, operationId);
		return Response.status(HttpStatus.SC_OK).build();
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}

}