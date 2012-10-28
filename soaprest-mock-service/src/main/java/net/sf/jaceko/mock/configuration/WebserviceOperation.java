package net.sf.jaceko.mock.configuration;

import java.util.List;

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
	private int invocationNumber = 0;

	@SuppressWarnings("unchecked")
	private List<MockResponse> customResponses = new GrowthList();;

	private int customDelaySec;

	public WebserviceOperation() {
		super();

	}

	public WebserviceOperation(String operationName,
			String defaultResponseFile, String defaultResponseText,
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
		if (customResponses.size() < requestNumber
				|| customResponses.get(index) == null) {
			return new MockResponse(defaultResponseText, defaultResponseCode);
		}

		return customResponses.get(index);
	}

	public void setCustomResponse(MockResponse customResponse, int requestNumber) {
		if (customResponse.getCode() == 0) {
			customResponse.setCode(defaultResponseCode);
		}

		customResponses.set(requestNumber - 1, customResponse);
	}

	public void setCustomDelaySec(int customDelaySec) {
		this.customDelaySec = customDelaySec;
	}

	public int getCustomDelaySec() {
		return customDelaySec;
	}

	public void init() {
		customDelaySec = 0;
		customResponses.clear();
		invocationNumber = 0;
	}

	/**
	 * increments and returns number of consecutive service invocations
	 */
	public int getNextInvocationNumber() {
		return ++invocationNumber;
	}

	public void resetInvocationNumber() {
		invocationNumber = 0;

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
				.format("WebserviceOperation [operationName=%s, defaultResponseFile=%s, defaultResponseText=%s, defaultResponseCode=%s, invocationNumber=%s, customResponses=%s, customDelaySec=%s]",
						operationName,
						defaultResponseFile,
						defaultResponseText,
						defaultResponseCode,
						invocationNumber,
						customResponses != null ? customResponses.subList(0,
								Math.min(customResponses.size(), maxLen))
								: null, customDelaySec);
	}

}
