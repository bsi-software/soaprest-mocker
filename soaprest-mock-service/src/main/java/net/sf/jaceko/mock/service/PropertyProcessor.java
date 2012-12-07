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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.apache.log4j.Logger;

/**
 * Parses property file
 * <p/>
 * 
 * example property file:
 * 
 * <pre>
 * SERVICE[0].NAME=ticketing 
 * SERVICE[0].WSDL=ticketing.wsdl
 * SERVICE[0].OPERATION[0].INPUT_MESSAGE=reserveRequest
 * SERVICE[0].OPERATION[0].DEFAULT_RESPONSE=reserve_response.xml
 * SERVICE[0].OPERATION[1].INPUT_MESSAGE=confirmRequest
 * SERVICE[0].OPERATION[1].DEFAULT_RESPONSE=confirm_response.xml
 * 
 * SERVICE[1].NAME=mptu 
 * SERVICE[1].WSDL=mptu.wsdl
 * SERVICE[1].OPERATION[0].INPUT_MESSAGE=prepayRequest
 * SERVICE[1].OPERATION[0].DEFAULT_RESPONSE=prepay_response.xml
 * 
 * </pre>
 * 
 */
public class PropertyProcessor {
	private static final Logger LOG = Logger.getLogger(PropertyProcessor.class);

	private static final String INPUT_MESSAGE = "INPUT_MESSAGE";

	private static final String HTTP_METHOD = "HTTP_METHOD";

	private static final String DEFAULT_RESPONSE = "DEFAULT_RESPONSE";

	private static final String DEFAULT_RESPONSE_CODE = "DEFAULT_RESPONSE_CODE";

	private static final String SERVICE_TYPE = "TYPE";

	private static final String SERVICE_NAME = "NAME";

	private static final String SERVICE_WSDL = "WSDL";

	private static final Pattern SERVICE_PATTERN = Pattern.compile("^SERVICE\\[([0-9]+)\\]$");
	private static final Pattern OPERATION_PATTERN = Pattern.compile("^OPERATION\\[([0-9]+)\\]$");

	private final WsdlProcessor wsdlProcessor = new WsdlProcessor();

	/**
	 * @param reader
	 *            - reader pointing to configuration file of the mock service
	 * @return
	 * @throws IOException
	 */
	public MockConfigurationHolder process(final Reader reader) throws IOException {
		final Properties properties = new Properties();
		properties.load(reader);
		final Set<Object> keySet = properties.keySet();
		final Map<Integer, WebService> services = new HashMap<Integer, WebService>();

		for (final Iterator<Object> iterator = keySet.iterator(); iterator.hasNext();) {
			final String propertyKey = (String) iterator.next();
			final String propertyValue = ((String) properties.get(propertyKey)).trim();

			final String[] propertyKeyParts = propertyKey.split("\\.");
			if (propertyKeyParts.length >= 2) {

				final int serviceIndex = getServiceIndex(propertyKeyParts[0]);
				if (serviceIndex >= 0) {
					final WebService service = getService(services, serviceIndex);

					final String serviceVariable = propertyKeyParts[1];
					final int operationIndex = getOperationIndex(serviceVariable);
					if (operationIndex >= 0) {
						// operation part
						final WebserviceOperation operation = getOperationFromService(service, operationIndex);
						final String operationProperty = propertyKeyParts[2];
						setOperationProperties(operation, operationProperty, propertyValue);
					} else {
						setServiceProperties(service, serviceVariable, propertyValue);
					}
				}

			}
		}

		final MockConfigurationHolder configuration = new MockConfigurationHolder();
		configuration.setWebServices(services.values());

		return configuration;

	}

	private void setServiceProperties(final WebService service, final String serviceProperty, final String propertyValue) {
		final String wsdlFileName = propertyValue;
		if (serviceProperty.equals(SERVICE_WSDL)) {
			final String fileText = readFileContents(wsdlFileName);

			if (fileText != null) {
				service.setWsdlText(fileText);
				service.addOperations(wsdlProcessor.getOperationsFromWsdl(wsdlFileName, fileText));

			}

		} else if (serviceProperty.equals(SERVICE_NAME)) {
			service.setName(wsdlFileName);
		} else if (serviceProperty.equals(SERVICE_TYPE)) {
			service.setServiceType(ServiceType.valueOf(wsdlFileName));
		}
	}

	private WebService getService(final Map<Integer, WebService> services, final int serviceIndex) {
		WebService service = services.get(serviceIndex);
		if (service == null) {
			service = new WebService();
			services.put(serviceIndex, service);
		}
		return service;
	}

	private void setOperationProperties(final WebserviceOperation operation, final String operationProperty, final String propertyValue) {
		if (operationProperty.equals(DEFAULT_RESPONSE)) {
			operation.setDefaultResponseFile(propertyValue);
			setDefaultResponseText(operation);
		} else if (operationProperty.equals(DEFAULT_RESPONSE_CODE)) {
			operation.setDefaultResponseCode(Integer.valueOf(propertyValue));
		} else if (operationProperty.equals(INPUT_MESSAGE)) {
			operation.setOperationName(propertyValue);
		} else if (operationProperty.equals(HTTP_METHOD)) {
			try {
				operation.setOperationName(HttpMethod.valueOf(propertyValue).toString());
			} catch (final IllegalArgumentException e) {
				throw new ServiceNotConfiguredException("Http method not recognized: " + propertyValue);

			}
		}
	}

	private void setDefaultResponseText(final WebserviceOperation operation) {

		final String fileText = readFileContents(operation.getDefaultResponseFile());
		if (fileText != null) {
			operation.setDefaultResponseText(fileText);
		}

	}

	protected String readFileContents(final String fileName) {
		final StringBuilder text = new StringBuilder();
		final String newLine = System.getProperty("line.separator");
		Scanner scanner = null;
		try {
			final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
			if (resourceAsStream == null) {
				LOG.error("File not found: " + fileName);
				return null;
			} else {
				LOG.info(fileName + " found in classpath");
			}
			scanner = new Scanner(resourceAsStream);
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + newLine);
			}
		} catch (final Exception e) {
			LOG.error("Problem reading file : " + fileName, e);
			return null;
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		return text.toString();
	}

	private WebserviceOperation getOperationFromService(final WebService service, final int operationIndex) {
		WebserviceOperation operation = service.getOperation(operationIndex);
		if (operation == null) {
			operation = new WebserviceOperation();
			service.addOperation(operationIndex, operation);
		}
		return operation;
	}

	int getOperationIndex(final String keyPart) {
		final Pattern pattern = OPERATION_PATTERN;
		return extractIndex(keyPart, pattern);
	}

	int getServiceIndex(final String keyPart) {
		final Pattern pattern = SERVICE_PATTERN;
		return extractIndex(keyPart, pattern);
	}

	private int extractIndex(final String keyPart, final Pattern pattern) {
		final Matcher matcher = pattern.matcher(keyPart);
		if (matcher.find()) {
			final String indxNumberStr = matcher.group(1);
			return Integer.parseInt(indxNumberStr);
		}

		return -1;
	}

	public MockConfigurationHolder process(final String fileName) throws IOException {
		final String fileContents = readFileContents(fileName);
		if (fileContents == null) {
			throw new FileNotFoundException("Property file not found in the classpath: " + fileName);
		}
		final Reader reader = new StringReader(fileContents);
		return process(reader);
	}

}
