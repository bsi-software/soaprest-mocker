package net.sf.jaceko.mock.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.WebService;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;
import net.sf.jaceko.mock.model.Request;


public class WebserviceMockSvcLayer {
	private MockserviceConfiguration configuration;

	private Map<String, Map<String, Collection<Request>>> recordedRequestsMap = new HashMap<String, Map<String, Collection<Request>>>();

	private DelayService delayService;

	public String performRequest(String serviceName, String operationId, String request, String queryString, String resourceId) {
		System.out.println(serviceName+", "+operationId+", "+request);
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		int invocationNumber = serviceOperation.getNextInvocationNumber();
		String response = serviceOperation.getResponseText(invocationNumber);
		int delaySec = serviceOperation.getCustomDelaySec();
		recordRequest(serviceName, operationId, request, queryString, resourceId);
		delayService.delaySec(delaySec);
		return response;

	}

	public void setCustomResponse(String serviceName, String operationId, int requestInOrder,
			String customResponse) {
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		serviceOperation.setCustomResponseText(customResponse, requestInOrder);
		serviceOperation.resetInvocationNumber();

	}

	public void setRequestDelay(String serviceName, String operationId, int delaySec) {
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		serviceOperation.setCustomDelaySec(delaySec);
		serviceOperation.resetInvocationNumber();
	}

	public String getWsdl(String serviceName) {
		WebService soapService = configuration.getWebService(serviceName);
		if (soapService == null) {
			throw new ServiceNotConfiguredException("Undefined service: " + serviceName);
		}
		return soapService.getWsdlText();
	}

	public void setMockserviceConfiguration(MockserviceConfiguration configuration) {
		this.configuration = configuration;
	}

	private WebserviceOperation getWebserviceOperation(String serviceName, String operationId) {
		WebserviceOperation serviceOperation = configuration.getWebServiceOperation(serviceName, operationId);
		if (serviceOperation == null) {
			throw new ServiceNotConfiguredException("Undefined webservice operation: operationId:"
					+ operationId + " of service: " + serviceName);
		}
		return serviceOperation;
	}

	private void recordRequest(String serviceName, String operationId, String requestBody, String queryString, String resourceId) {
		Map<String, Collection<Request>> requestsPerOperationMap = recordedRequestsMap.get(serviceName);
		if (requestsPerOperationMap == null) {
			requestsPerOperationMap = new HashMap<String, Collection<Request>>();
			recordedRequestsMap.put(serviceName, requestsPerOperationMap);
		}

		Collection<Request> recordedRequests = requestsPerOperationMap.get(operationId);
		if (recordedRequests == null) {
			recordedRequests = new ArrayList<Request>();
			requestsPerOperationMap.put(operationId, recordedRequests);
		}

		Request request = new Request(requestBody, queryString, resourceId);
		recordedRequests.add(request);
	}

	public Collection<String> getRecordedRequestBodies(String serviceName, String operationId) {
		Collection<Request> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedRequestBodies = new ArrayList<String>();
		for (Request request : recordedRequests) {
			recordedRequestBodies.add(request.getBody());
		}
		return recordedRequestBodies;

	}

	public Collection<String> getRecordedUrlParams(String serviceName, String operationId) {
		Collection<Request> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedRequestParams = new ArrayList<String>();
		for (Request request : recordedRequests) {
			recordedRequestParams.add(request.getQueryString());
		}
		return recordedRequestParams;
	}

	private Collection<Request> getRecordedRequests(String serviceName, String operationId) {
		getWebserviceOperation(serviceName, operationId);
		Map<String, Collection<Request>> requestsPerOperationMap = recordedRequestsMap.get(serviceName);
		if (requestsPerOperationMap == null) {
			return Collections.emptyList();
		}
		Collection<Request> recordedRequests = requestsPerOperationMap.get(operationId);
		if (recordedRequests == null) {
			return Collections.emptyList();
		}
		return recordedRequests;
	}
	
	public Collection<String> getRecordedResourceIds(String serviceName, String operationId) {
		Collection<Request> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedResourceIds = new ArrayList<String>();
		for (Request request : recordedRequests) {
			recordedResourceIds.add(request.getResourceId());
		}
		return recordedResourceIds;
	}


	public void setDelayService(DelayService delayService) {
		this.delayService = delayService;
	}

	public void initMock(String serviceName, String operationId) {
		clearOperationSetup(serviceName, operationId);
		clearRecordedRequests(serviceName, operationId);
	}

	private void clearOperationSetup(String serviceName, String operationId) {
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		serviceOperation.init();
	}

	private void clearRecordedRequests(String serviceName, String operationId) {
		getRecordedRequests(serviceName, operationId).clear();
	}


}
