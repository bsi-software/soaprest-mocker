package net.sf.jaceko.mock.configuration;

import java.util.List;

import org.apache.commons.collections.list.GrowthList;

/**
 * Class representing an operation or "method" of a webservice. SOAP operations
 * are identified by a root node name of an xml request eg. prepayRequest. 
 * REST operations are identified by a HTTP method name eg. GET, POST, PUT
 * 
 */
public class WebserviceOperation {

	private String operationName;
	private String defaultResponseFile;
	private String defaultResponseText;
	private int invocationNumber = 0;

	@SuppressWarnings("unchecked")
	private List<String> customResponses = new GrowthList();;

	private int customDelaySec;

	public WebserviceOperation() {
		super();

	}

	public WebserviceOperation(String operationName,
			String defaultResponseFile, String defaultResponseText) {
		super();
		this.operationName = operationName;
		this.defaultResponseFile = defaultResponseFile;
		this.defaultResponseText = defaultResponseText;
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

	public String getResponseText(int requestNumber) {
		int index = requestNumber - 1;
		if (customResponses.size() < requestNumber
				|| customResponses.get(index) == null) {
			return defaultResponseText;
		}

		return customResponses.get(index);
	}

	public void setCustomResponseText(String customResponseText,
			int requestNumber) {

		customResponses.set(requestNumber - 1, customResponseText);
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

}
