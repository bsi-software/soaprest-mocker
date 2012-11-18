/**
 *
 *     Copyright (C) 2012 Jacek Obarymski
 *
 *     This file is part of SOAP/REST Mock Servce.
 *
 *     SOAP/REST Mock Servce is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License, version 3
 *     as published by the Free Software Foundation.
 *
 *     SOAP/REST Mock Servce is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with SOAP/REST Mock Servce; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jaceko.mock.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;


public class MockserviceConfiguration {

	private Map<String, WebService> servicesMap = new HashMap<String, WebService>();

	public void setSoapServices(Collection<WebService> services) {
		servicesMap = new HashMap<String, WebService>();
		for (WebService soapService : services) {
			servicesMap.put(soapService.getName(), soapService);
		}
	}

	public Collection<WebService> getSoapServices() {
		return servicesMap.values();
	}

	public WebserviceOperation getWebServiceOperation(String serviceName, String operationId) {
		WebService service = getWebService(serviceName);
		if (service == null) {
			throw new ServiceNotConfiguredException("Undefined webservice:" + serviceName);
		}
		Collection<WebserviceOperation> operations = service.getOperations();
		for (WebserviceOperation operation : operations) {
			if (operation.getOperationName().equals(operationId)) {
				return operation;
			}
		}

		return null;
	}

	public WebService getWebService(String serviceName) {
		return servicesMap.get(serviceName);
	}

}
