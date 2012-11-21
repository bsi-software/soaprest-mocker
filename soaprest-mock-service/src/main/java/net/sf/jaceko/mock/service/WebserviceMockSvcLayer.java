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
package net.sf.jaceko.mock.service;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.WebService;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;
import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.model.MockRequest;

public class WebserviceMockSvcLayer {
	private MockserviceConfiguration configuration;

	private final Map<String, Map<String, Collection<MockRequest>>> recordedRequestsMap = synchronizedMap(new HashMap<String, Map<String, Collection<MockRequest>>>());

	private DelayService delayService;

	public MockResponse performRequest(String serviceName, String operationId, String request, String queryString,
			String resourceId) {
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		int invocationNumber = serviceOperation.getNextInvocationNumber();
		MockResponse response = serviceOperation.getResponse(invocationNumber);
		recordRequest(serviceName, operationId, request, queryString, resourceId);
		delayService.delaySec(response.getDelaySec());
		return response;

	}

	public void setCustomResponse(String serviceName, String operationId, int requestInOrder, MockResponse expectedResponse) {
		WebserviceOperation serviceOperation = getWebserviceOperation(serviceName, operationId);
		serviceOperation.setCustomResponse(expectedResponse, requestInOrder);
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
			throw new ServiceNotConfiguredException("Undefined webservice operation: operationId:" + operationId
					+ " of service: " + serviceName);
		}
		return serviceOperation;
	}

	private void recordRequest(String serviceName, String operationId, String requestBody, String queryString, String resourceId) {
		Map<String, Collection<MockRequest>> requestsPerOperationMap = fetchRequestsPerOperationMap(serviceName);

		Collection<MockRequest> recordedRequests = fetchRecordedRequests(operationId, requestsPerOperationMap);

		MockRequest request = new MockRequest(requestBody, queryString, resourceId);
		recordedRequests.add(request);
	}

	private Collection<MockRequest> fetchRecordedRequests(String operationId,
			Map<String, Collection<MockRequest>> requestsPerOperationMap) {

		synchronized (requestsPerOperationMap) {
			Collection<MockRequest> recordedRequests = requestsPerOperationMap.get(operationId);
			if (recordedRequests == null) {
				recordedRequests = synchronizedList(new ArrayList<MockRequest>());
				requestsPerOperationMap.put(operationId, recordedRequests);
			}
			return recordedRequests;
		}
	}

	private Map<String, Collection<MockRequest>> fetchRequestsPerOperationMap(String serviceName) {
		synchronized (recordedRequestsMap) {
			Map<String, Collection<MockRequest>> requestsPerOperationMap = recordedRequestsMap.get(serviceName);
			if (requestsPerOperationMap == null) {
				requestsPerOperationMap = synchronizedMap(new HashMap<String, Collection<MockRequest>>());
				recordedRequestsMap.put(serviceName, requestsPerOperationMap);
			}
			return requestsPerOperationMap;
		}
	}

	public Collection<String> getRecordedRequestBodies(String serviceName, String operationId) {
		Collection<MockRequest> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedRequestBodies = new ArrayList<String>();
		for (MockRequest request : recordedRequests) {
			recordedRequestBodies.add(request.getBody());
		}
		return recordedRequestBodies;

	}

	public Collection<String> getRecordedUrlParams(String serviceName, String operationId) {
		Collection<MockRequest> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedRequestParams = new ArrayList<String>();
		for (MockRequest request : recordedRequests) {
			recordedRequestParams.add(request.getQueryString());
		}
		return recordedRequestParams;
	}

	private Collection<MockRequest> getRecordedRequests(String serviceName, String operationId) {
		getWebserviceOperation(serviceName, operationId);
		Map<String, Collection<MockRequest>> requestsPerOperationMap = recordedRequestsMap.get(serviceName);
		if (requestsPerOperationMap == null) {
			return Collections.emptyList();
		}
		Collection<MockRequest> recordedRequests = requestsPerOperationMap.get(operationId);
		if (recordedRequests == null) {
			return Collections.emptyList();
		}
		return recordedRequests;
	}

	public Collection<String> getRecordedResourceIds(String serviceName, String operationId) {
		Collection<MockRequest> recordedRequests = getRecordedRequests(serviceName, operationId);

		Collection<String> recordedResourceIds = new ArrayList<String>();
		synchronized (recordedRequests) {
			for (MockRequest request : recordedRequests) {
				recordedResourceIds.add(request.getResourceId());
			}
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
