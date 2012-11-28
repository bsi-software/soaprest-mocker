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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.jaceko.mock.application.enums.ServiceType;


@XmlRootElement(name = "service")
public class WebService {

	@XmlElement
	private String name;
	private String wsdlName;
	private Map<Integer, WebserviceOperation> indxToOperationMap = new HashMap<Integer, WebserviceOperation>();
	private String wsdlText;
	
	@XmlElement(name = "type")
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
	public String toString() {
		return "WebService [name=" + name + ", wsdlName=" + wsdlName + ", indxToOperationMap="
				+ indxToOperationMap + ", wsdlText=" + wsdlText + ", serviceType=" + serviceType
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((indxToOperationMap == null) ? 0 : indxToOperationMap.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((serviceType == null) ? 0 : serviceType.hashCode());
		result = prime * result + ((wsdlName == null) ? 0 : wsdlName.hashCode());
		result = prime * result + ((wsdlText == null) ? 0 : wsdlText.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebService other = (WebService) obj;
		if (indxToOperationMap == null) {
			if (other.indxToOperationMap != null)
				return false;
		} else if (!indxToOperationMap.equals(other.indxToOperationMap))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serviceType != other.serviceType)
			return false;
		if (wsdlName == null) {
			if (other.wsdlName != null)
				return false;
		} else if (!wsdlName.equals(other.wsdlName))
			return false;
		if (wsdlText == null) {
			if (other.wsdlText != null)
				return false;
		} else if (!wsdlText.equals(other.wsdlText))
			return false;
		return true;
	}

	

}
