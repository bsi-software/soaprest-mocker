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

import static com.google.common.collect.Lists.newArrayList;
import static java.text.MessageFormat.format;

import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

public class WsdlProcessor {
	private static final Logger LOG = Logger.getLogger(WsdlProcessor.class);

	private WSDLFactory factory;

	public WsdlProcessor() {
		try {
			factory = WSDLFactory.newInstance();
		} catch (WSDLException e) {
			LOG.error("WSDL reader initialization error, ", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<WebserviceOperation> getOperationsFromWsdl(final String wsdlFileName, final String fileText) {
		final List<WebserviceOperation> mockOperations = newArrayList();

		WSDLReader wsdlReader = factory.newWSDLReader();
		try {
			Definition def = wsdlReader.readWSDL(null, new InputSource(new StringReader(fileText)));
			Map<QName, Binding> bindingsMap = def.getBindings();
			Collection<Binding> bindings = bindingsMap.values();
			Iterator<Binding> bindingsIterator = bindings.iterator();
			if (bindingsIterator.hasNext()) {
				Binding binding = bindingsIterator.next();
				List<BindingOperation> bindingOperations = binding.getBindingOperations();
				for (BindingOperation bindingOperation : bindingOperations) {
					final String operationName = bindingOperation.getName();
					final WebserviceOperation mockOperation = new WebserviceOperation();
					mockOperation.setOperationName(operationName);
					mockOperations.add(mockOperation);
				}
			}
		} catch (WSDLException e) {
			LOG.error(format("error processing WSDL file: {0}, {1}", wsdlFileName, e.getMessage()));
		}

		return mockOperations;
	}

}
