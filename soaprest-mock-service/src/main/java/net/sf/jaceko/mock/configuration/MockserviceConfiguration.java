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
