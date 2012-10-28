package net.sf.jaceko.mock.configuration;

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

	/**
	 * @param reader
	 *            - reader pointing to configuration file of the mock service
	 * @return
	 * @throws IOException
	 */
	public MockserviceConfiguration process(Reader reader) throws IOException {
		Properties properties = new Properties();
		properties.load(reader);
		Set<Object> keySet = properties.keySet();
		Map<Integer, WebService> services = new HashMap<Integer, WebService>();

		for (Iterator<Object> iterator = keySet.iterator(); iterator.hasNext();) {
			String propertyKey = (String) iterator.next();
			String propertyValue = ((String) properties.get(propertyKey)).trim();

			String[] propertyKeyParts = propertyKey.split("\\.");
			if (propertyKeyParts.length >= 2) {

				int serviceIndex = getServiceIndex(propertyKeyParts[0]);
				if (serviceIndex >= 0) {
					WebService service = getService(services, serviceIndex);

					String serviceVariable = propertyKeyParts[1];
					int operationIndex = getOperationIndex(serviceVariable);
					if (operationIndex >= 0) {
						// operation part
						WebserviceOperation operation = getOperationFromService(service, operationIndex);
						String operationProperty = propertyKeyParts[2];
						setOperationProperties(operation, operationProperty, propertyValue);
					} else {
						setServiceProperties(service, serviceVariable, propertyValue);
					}
				}

			}
		}

		MockserviceConfiguration configuration = new MockserviceConfiguration();
		configuration.setSoapServices(services.values());

		return configuration;

	}

	private void setServiceProperties(WebService service, String serviceProperty,
			String propertyValue) {
		if (serviceProperty.equals(SERVICE_WSDL)) {
			service.setWsdlName(propertyValue);
			setWsdlText(service, propertyValue);
		} else if (serviceProperty.equals(SERVICE_NAME)) {
			service.setName(propertyValue);
		} else if (serviceProperty.equals(SERVICE_TYPE)) {
			service.setServiceType(ServiceType.valueOf(propertyValue));
		}
	}

	private WebService getService(Map<Integer, WebService> services, int serviceIndex) {
		WebService service = services.get(serviceIndex);
		if (service == null) {
			service = new WebService();
			services.put(serviceIndex, service);
		}
		return service;
	}

	private void setOperationProperties(WebserviceOperation operation, String operationProperty,
			String propertyValue) {
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
			} catch (IllegalArgumentException e) {
				throw new ServiceNotConfiguredException("Http method not recognized: "+propertyValue);

			}
		}
	}

	private void setDefaultResponseText(WebserviceOperation operation) {

		String fileText = readFileContents(operation.getDefaultResponseFile());
		if (fileText != null) {
			operation.setDefaultResponseText(fileText);
		}

	}

	private void setWsdlText(WebService soapService, String fileName) {
		String fileText = readFileContents(fileName);
		if (fileText != null) {
			soapService.setWsdlText(fileText);
		}
	}

	protected String readFileContents(String fileName) {
		StringBuilder text = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		Scanner scanner = null;
		try {
			InputStream resourceAsStream = this.getClass().getClassLoader()
					.getResourceAsStream(fileName);
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
		} catch (Exception e) {
			LOG.error("Problem reading file : " + fileName, e);
			return null;
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		return text.toString();
	}

	private WebserviceOperation getOperationFromService(WebService service, int operationIndex) {
		WebserviceOperation operation = service.getOperation(operationIndex);
		if (operation == null) {
			operation = new WebserviceOperation();
			service.addOperation(operationIndex, operation);
		}
		return operation;
	}

	int getOperationIndex(String keyPart) {
		Pattern pattern = OPERATION_PATTERN;
		return extractIndex(keyPart, pattern);
	}

	int getServiceIndex(String keyPart) {
		Pattern pattern = SERVICE_PATTERN;
		return extractIndex(keyPart, pattern);
	}

	private int extractIndex(String keyPart, Pattern pattern) {
		Matcher matcher = pattern.matcher(keyPart);
		if (matcher.find()) {
			String indxNumberStr = matcher.group(1);
			return Integer.parseInt(indxNumberStr);
		}

		return -1;
	}

	public MockserviceConfiguration process(String fileName) throws IOException {
		String fileContents = readFileContents(fileName);
		if (fileContents == null) {
			throw new FileNotFoundException("Property file not found in the classpath: " + fileName);
		}
		Reader reader = new StringReader(fileContents);
		return process(reader);
	}

}
