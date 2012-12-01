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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dto.OperationRefDto;
import net.sf.jaceko.mock.dto.WebServiceDto;
import net.sf.jaceko.mock.dto.WebServicesDto;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.service.MockConfigurationHolder;

@Path("/services")
public class ServicesResource {

	private MockConfigurationHolder mockConfigurationService;

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public WebServicesDto getWebServices(@Context HttpServletRequest context) {
		Collection<WebService> servicesCollection = mockConfigurationService.getWebServices();
		WebServicesDto webServicesDto = new WebServicesDto();
		for (WebService webService : servicesCollection) {
			WebServiceDto webServiceDto = new WebServiceDto();
			webServiceDto.setName(webService.getName());
			webServiceDto.setType(webService.getServiceType());
			String serviceUri = getServiceUri(context, webService);
			webServiceDto.setEndpointUri(getEndpointUri(serviceUri));
			if (ServiceType.SOAP == webService.getServiceType()) {
				webServiceDto.setWsdlUri(getWsdlUri(serviceUri));
			}
			Collection<WebserviceOperation> operations = webService.getOperations();
			for (WebserviceOperation operation : operations) {
				OperationRefDto operationRef = new OperationRefDto();
				operationRef.setName(operation.getOperationName());
				operationRef.setUri(getOperationUri(serviceUri, operation.getOperationName()));
				webServiceDto.getOperationRefs().add(operationRef);
			}

			webServicesDto.getWebservicesList().add(webServiceDto);

		}

		return webServicesDto;
	}

	private String getWsdlUri(String serviceUri) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(serviceUri);
		stringBuilder.append("/wsdl");
		return stringBuilder.toString();
	}

	private String getEndpointUri(String serviceUri) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(serviceUri);
		stringBuilder.append("/endpoint");
		return stringBuilder.toString();
	}

	private String getOperationUri(String serviceUri, String operationName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(serviceUri);
		stringBuilder.append("/operations/");
		stringBuilder.append(operationName);
		return stringBuilder.toString();
	}

	private String getServiceUri(HttpServletRequest context, WebService webService) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("http://");
		stringBuilder.append(context.getServerName());
		stringBuilder.append(":");
		stringBuilder.append(context.getServerPort());
		stringBuilder.append("/mock/services/");
		stringBuilder.append(webService.getServiceType());
		stringBuilder.append("/");
		stringBuilder.append(webService.getName());
		return stringBuilder.toString();
	}

	public void setMockConfigurationService(MockConfigurationHolder mockConfigurationService) {
		this.mockConfigurationService = mockConfigurationService;
	}

}
