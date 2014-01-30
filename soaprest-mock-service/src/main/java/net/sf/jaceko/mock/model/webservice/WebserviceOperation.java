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
package net.sf.jaceko.mock.model.webservice;

import static java.lang.String.format;
import static java.util.Collections.synchronizedList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.model.request.MockResponse;

import org.apache.commons.collections.list.GrowthList;

/**
 * Class representing an operation or "method" of a webservice. SOAP operations
 * are identified by a root node name of an xml request eg. prepayRequest. REST
 * operations are identified by a HTTP method name eg. GET, POST, PUT, DELETE
 * 
 */
public class WebserviceOperation {

	private String operationName;
	private String defaultResponseFile;
	private String defaultResponseText;
	private int defaultResponseCode = 200;
	private String defaultResponseContentType = MediaType.TEXT_XML_TYPE.toString();
	private final AtomicInteger invocationNumber = new AtomicInteger(0);

	private Map<Integer, WebserviceCustomResponse> indxToCustomResponseMap = new HashMap<Integer, WebserviceCustomResponse>();
	@SuppressWarnings("unchecked")
	private final List<MockResponse> customResponses = synchronizedList(new GrowthList());
	private boolean responseInSequences = false;

	public WebserviceOperation() {
		super();
	}

	public static WebserviceOperationBuilder name(String operationName) {
		WebserviceOperationBuilder builder = new WebserviceOperationBuilder();
		return builder.operationName(operationName);
	}

	public static WebserviceOperationBuilder defaultResponseText(String defaultResponseText) {
		WebserviceOperationBuilder builder = new WebserviceOperationBuilder();
		return builder.defaultResponseText(defaultResponseText);
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getDefaultResponseFile() {
		return defaultResponseFile;
	}

	public void setDefaultResponseFile(String defaultResponseFile) {
		this.defaultResponseFile = defaultResponseFile;
	}

	public String getDefaultResponseText() {
		return this.defaultResponseText;
	}

	public void setDefaultResponseText(String defaultResponseText) {
		this.defaultResponseText = defaultResponseText;
	}

	public Collection<WebserviceCustomResponse> getCustomResponses() {
		return indxToCustomResponseMap.values();
	}

	public void addCustomResponse(int operationIndex, WebserviceCustomResponse operation) {
		indxToCustomResponseMap.put(operationIndex, operation);
	}

	public WebserviceCustomResponse getCustomResponse(int indx) {
		return indxToCustomResponseMap.get(indx);
	}

	public MockResponse getResponse(int requestNumber) {
		return getResponse(requestNumber, null);
	}

	public MockResponse getResponse(int requestNumber, String request) {
		if(request == null || indxToCustomResponseMap.isEmpty()) {
			int index;
			if(responseInSequences) {
				index = requestNumber % customResponses.size();
			} else {
				index = requestNumber - 1;
			}
			MockResponse mockResponse = null;
			synchronized (customResponses) {
				if (customResponses.size() < requestNumber || (mockResponse = customResponses.get(index)) == null) {
					return MockResponse.body(defaultResponseText).code(defaultResponseCode).contentType(defaultResponseContentType)
							.build();
				}
				return mockResponse;
			}
		} else {
			for (WebserviceCustomResponse customResponse : indxToCustomResponseMap.values()) {
				if(request.contains(customResponse.getSearchString())) {
					return MockResponse.body(customResponse.getResponseText()).code(defaultResponseCode).contentType(defaultResponseContentType)
							.build();
				}
			}
			return MockResponse.body(defaultResponseText).code(defaultResponseCode).contentType(defaultResponseContentType)
					.build();
		}
	}

	public void setCustomResponse(MockResponse customResponse, int requestNumber) {
		customResponse.setZeroCodeTo(defaultResponseCode);
		customResponses.set(requestNumber - 1, customResponse);
	}

	public void addCustomResponse(MockResponse customResponse) {
		customResponse.setZeroCodeTo(defaultResponseCode);
		customResponses.add(customResponse);
	}
	
	public boolean isResponseInSequences() {
		return responseInSequences;
	}
	
	public void setResponseInSequences(boolean responseInSequences) {
		this.responseInSequences = responseInSequences;
	}
	
	public void init() {
		customResponses.clear();
		resetInvocationNumber();
	}

	/**
	 * increments and returns number of consecutive service invocations
	 */
	public int getNextInvocationNumber() {
		return invocationNumber.incrementAndGet();
	}

	public void resetInvocationNumber() {
		invocationNumber.set(0);

	}

	public int getDefaultResponseCode() {
		return defaultResponseCode;
	}

	public void setDefaultResponseCode(int defaultResponseCode) {
		this.defaultResponseCode = defaultResponseCode;
	}

	public String getDefaultResponseContentType() {
		return defaultResponseContentType;
	}

	public void setDefaultResponseContentType(String defaultResponseContentType) {
		this.defaultResponseContentType = defaultResponseContentType;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return format(
				"WebserviceOperation [operationName=%s, defaultResponseFile=%s, defaultResponseText=%s, defaultResponseCode=%s, defaultResponseContentType=%s, invocationNumber=%s, customResponses=%s]",
				operationName, defaultResponseFile, defaultResponseText, defaultResponseCode, defaultResponseContentType,
				invocationNumber, customResponses != null ? customResponses.subList(0, Math.min(customResponses.size(), maxLen))
						: null);
	}

	public static class WebserviceOperationBuilder {
		private String operationName;
		private String defaultResponseFile;
		private String defaultResponseText;
		private int defaultResponseCode;
		private String defaultResponseContentType;
		private Map<Integer, WebserviceCustomResponse> indxToCustomResponseMap;

		public WebserviceOperationBuilder operationName(String operationName) {
			this.operationName = operationName;
			return this;
		}

		public WebserviceOperationBuilder defaultResponseFile(String defaultResponseFile) {
			this.defaultResponseFile = defaultResponseFile;
			return this;
		}

		public WebserviceOperationBuilder defaultResponseText(String defaultResponseText) {
			this.defaultResponseText = defaultResponseText;
			return this;
		}

		public WebserviceOperationBuilder defaultResponseCode(int defaultResponseCode) {
			this.defaultResponseCode = defaultResponseCode;
			return this;
		}

		public WebserviceOperationBuilder defaultResponseContentType(String defaultResponseContentType) {
			this.defaultResponseContentType = defaultResponseContentType;
			return this;
		}

		public WebserviceOperationBuilder indxToCustomResponseMap(Map<Integer, WebserviceCustomResponse> indxToCustomResponseMap) {
			this.indxToCustomResponseMap = indxToCustomResponseMap;
			return this;
		}

		public WebserviceOperation build() {
			WebserviceOperation webserviceOperation = new WebserviceOperation();
			webserviceOperation.operationName = this.operationName;
			webserviceOperation.defaultResponseFile = this.defaultResponseFile;
			webserviceOperation.defaultResponseText = this.defaultResponseText;
			webserviceOperation.defaultResponseCode = this.defaultResponseCode;
			if (this.defaultResponseContentType != null) {
				webserviceOperation.defaultResponseContentType = this.defaultResponseContentType;
			}
			if (this.indxToCustomResponseMap != null) {
				webserviceOperation.indxToCustomResponseMap = this.indxToCustomResponseMap;
			}
			return webserviceOperation;
		}

	}

}
