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
package net.sf.jaceko.mock.configuration;

import static java.util.Collections.synchronizedList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jaceko.mock.model.MockResponse;

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
	private int defaultResponseCode;
	private AtomicInteger invocationNumber = new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	private List<MockResponse> customResponses = synchronizedList(new GrowthList());

	public WebserviceOperation() {
		super();

	}

	public WebserviceOperation(String operationName, String defaultResponseFile, String defaultResponseText,
			int defaultResponseCode) {
		super();
		this.operationName = operationName;
		this.defaultResponseFile = defaultResponseFile;
		this.defaultResponseText = defaultResponseText;
		this.defaultResponseCode = defaultResponseCode;
	}

	public String getOperationName() {
		return operationName;
	}

	void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getDefaultResponseFile() {
		return defaultResponseFile;
	}

	void setDefaultResponseFile(String defaultResponseFile) {
		this.defaultResponseFile = defaultResponseFile;
	}

	public String getDefaultResponseText() {
		return this.defaultResponseText;
	}

	void setDefaultResponseText(String defaultResponseText) {
		this.defaultResponseText = defaultResponseText;
	}

	public MockResponse getResponse(int requestNumber) {
		int index = requestNumber - 1;
		synchronized (customResponses) {
			if (customResponses.size() < requestNumber || customResponses.get(index) == null) {
				return new MockResponse(defaultResponseText, defaultResponseCode);
			}
			return customResponses.get(index);
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


	public synchronized void init() {
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

	@Override
	public String toString() {
		final int maxLen = 10;
		return String
				.format("WebserviceOperation [operationName=%s, defaultResponseFile=%s, defaultResponseText=%s, defaultResponseCode=%s, invocationNumber=%s, customResponses=%s]",
						operationName, defaultResponseFile, defaultResponseText, defaultResponseCode, invocationNumber,
						customResponses != null ? customResponses.subList(0, Math.min(customResponses.size(), maxLen)) : null);
	}


}
