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
