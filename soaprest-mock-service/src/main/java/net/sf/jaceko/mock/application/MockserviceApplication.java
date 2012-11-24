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
package net.sf.jaceko.mock.application;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.PropertyProcessor;
import net.sf.jaceko.mock.resource.MockSetupResource;
import net.sf.jaceko.mock.resource.RecordedRequestsResource;
import net.sf.jaceko.mock.resource.RestEndpointResource;
import net.sf.jaceko.mock.resource.SoapEndpointResource;
import net.sf.jaceko.mock.resource.WsdlExposingResource;
import net.sf.jaceko.mock.service.DelayService;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

public class MockserviceApplication extends Application {
	private static final String PROPERTY_FILE = "ws-mock.properties";

	public MockserviceApplication() {
		super();
		PropertyProcessor propertyProcessor = new PropertyProcessor();
		MockserviceConfiguration configuration = null;
		try {
			configuration = propertyProcessor.process(PROPERTY_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Problem reading property file", e);
		}
		DelayService delayService = new DelayService();

		WebserviceMockSvcLayer svcLayer = new WebserviceMockSvcLayer();
		svcLayer.setMockserviceConfiguration(configuration);
		svcLayer.setDelayService(delayService);
		MockSetupResource mockSetupResource = new MockSetupResource();
		mockSetupResource.setWebserviceMockService(svcLayer);
		RecordedRequestsResource recordedRequestsResource = new RecordedRequestsResource();
		recordedRequestsResource.setWebserviceMockService(svcLayer);

		SoapEndpointResource mockSoapEndpointResource = new SoapEndpointResource();
		mockSoapEndpointResource.setWebserviceMockService(svcLayer);

		RestEndpointResource mockRestEndpointResource = new RestEndpointResource();
		mockRestEndpointResource.setWebserviceMockService(svcLayer);

		WsdlExposingResource wsdlExposingResource = new WsdlExposingResource();
		wsdlExposingResource.setWebserviceMockService(svcLayer);

		singletons.add(mockSoapEndpointResource);
		singletons.add(mockRestEndpointResource);
		singletons.add(mockSetupResource);
		singletons.add(recordedRequestsResource);
		singletons.add(wsdlExposingResource);

	}

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
