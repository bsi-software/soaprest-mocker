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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebServices;
import net.sf.jaceko.mock.service.MockConfigurationHolder;

@Path("/services")
public class ServicesResource {

	private MockConfigurationHolder mockConfigurationService;

	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public WebServices getWebServices() {
		Collection<WebService> servicesCollection = mockConfigurationService.getWebServices();
		return new WebServices(servicesCollection);
	}

	public void setMockConfigurationService(MockConfigurationHolder mockConfigurationService) {
		this.mockConfigurationService = mockConfigurationService;
	}

}
