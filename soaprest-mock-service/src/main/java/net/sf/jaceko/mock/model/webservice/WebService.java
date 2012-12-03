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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.jaceko.mock.application.enums.ServiceType;

import com.google.common.base.Objects;

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

	public void setName(String name) {
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

	public void setWsdlText(String wsdlText) {
		this.wsdlText = wsdlText;
	}

	public ServiceType getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name, wsdlName, indxToOperationMap, wsdlText, serviceType);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WebService) {
			WebService that = (WebService) object;
			return Objects.equal(this.name, that.name)
				&& Objects.equal(this.wsdlName, that.wsdlName)
				&& Objects.equal(this.indxToOperationMap, that.indxToOperationMap)
				&& Objects.equal(this.wsdlText, that.wsdlText)
				&& Objects.equal(this.serviceType, that.serviceType);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("name", name)
			.add("wsdlName", wsdlName)
			.add("indxToOperationMap", indxToOperationMap)
			.add("wsdlText", wsdlText)
			.add("serviceType", serviceType)
			.toString();
	}
	
}
