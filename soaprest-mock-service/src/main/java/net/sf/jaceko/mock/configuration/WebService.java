package net.sf.jaceko.mock.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jaceko.mock.application.enums.ServiceType;



public class WebService {

	private String name;
	private String wsdlName;
	private Map<Integer, WebserviceOperation> indxToOperationMap = new HashMap<Integer, WebserviceOperation>();
	private String wsdlText;
	private ServiceType serviceType;

	public WebService() {
		super();
	}
	
	public WebService(String name, String wsdlText) {
		super();
		this.name = name;
		this.wsdlText = wsdlText;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}


	public String getWsdlName() {
		return wsdlName;
	}

	public void setWsdlName(String wsdlName) {
		this.wsdlName = wsdlName;
	}

	public Collection<WebserviceOperation> getOperations() {
		return indxToOperationMap.values();
	}


	public void addOperation(int operationIndex, WebserviceOperation operation) {
		indxToOperationMap.put(operationIndex, operation);
	}

	public WebserviceOperation getOperation(int indx) {
		return indxToOperationMap.get(indx);
	}

	public String getWsdlText() {
		return wsdlText;
	}

	void setWsdlText(String wsdlText) {
		this.wsdlText = wsdlText;
	}

	public ServiceType getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public String toString() {
		return "WebService [name=" + name + ", wsdlName=" + wsdlName + ", indxToOperationMap="
				+ indxToOperationMap + ", wsdlText=" + wsdlText + ", serviceType=" + serviceType
				+ "]";
	}

}
