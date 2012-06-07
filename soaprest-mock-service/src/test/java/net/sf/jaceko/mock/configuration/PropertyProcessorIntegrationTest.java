package net.sf.jaceko.mock.configuration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.PropertyProcessor;
import net.sf.jaceko.mock.configuration.WebService;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.helper.XmlParser;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class PropertyProcessorIntegrationTest {
	private PropertyProcessor propertyProcessor = new PropertyProcessor();

	@Test
	public void shouldReadWsdlContentsFromFile() throws IOException, ParserConfigurationException,
			SAXException {
		String propertyString = "SERVICE[0].NAME=ticketing\r\n"
				+ "SERVICE[0].WSDL=dummy.wsdl\r\n"
				+ "SERVICE[0].TYPE=SOAP\r\n"
				+ "SERVICE[0].OPERATION[0].INPUT_MESSAGE=reserveRequest\r\n"
				+ "SERVICE[0].OPERATION[1].INPUT_MESSAGE=confirmRequest\r\n";

		Reader reader = new StringReader(propertyString);
		MockserviceConfiguration configuration = propertyProcessor.process(reader);
		Collection<WebService> services = configuration.getSoapServices();
		WebService soapService = services.iterator().next();
		String wsdlText = soapService.getWsdlText();
		Document wsdlDoc = XmlParser.parse(wsdlText, false);
		assertThat(wsdlDoc, hasXPath("/wsdl", equalTo("dummyContent123")));

	}

	@Test
	public void shouldReadDefaultResponseContentsFromFile() throws IOException,
			ParserConfigurationException, SAXException {
		String propertyString = "SERVICE[0].NAME=ticketing\r\n"
				+ "SERVICE[0].OPERATION[0].INPUT_MESSAGE=reserveRequest\r\n"
				+ "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE=reserve_response.xml\r\n";

		Reader reader = new StringReader(propertyString);
		MockserviceConfiguration configuration = propertyProcessor.process(reader);

		Collection<WebService> services = configuration.getSoapServices();
		WebService soapService = services.iterator().next();
		WebserviceOperation operation = soapService.getOperation(0);

		String responseXML = operation.getDefaultResponseText();
		Document responseDoc = XmlParser.parse(responseXML, false);

		assertThat(responseDoc, hasXPath("/dummyResponse/reqId", equalTo("1123")));
		assertThat(responseDoc, hasXPath("/dummyResponse/status", equalTo("PENDING")));

	}

	@Test
	public void shouldContinueIfResponseFileNotFound() throws IOException,
			ParserConfigurationException, SAXException {
		String propertyString = "SERVICE[0].NAME=ticketing\r\n"
				+ "SERVICE[0].OPERATION[0].INPUT_MESSAGE=reserveRequest\r\n"
				+ "SERVICE[0].OPERATION[1].INPUT_MESSAGE=confirmRequest\r\n";

		Reader reader = new StringReader(propertyString);
		MockserviceConfiguration configuration = propertyProcessor.process(reader);

		Collection<WebService> services = configuration.getSoapServices();
		WebService soapService = services.iterator().next();
		WebserviceOperation operation = soapService.getOperation(0);
		assertThat(operation, notNullValue());

	}

	@Test
	public void shouldReadPropertiesFromFile() throws ParserConfigurationException, SAXException, IOException {

		String mockPropertiesFileName = "test-ws-mock.properties";
		MockserviceConfiguration configuration = propertyProcessor.process(mockPropertiesFileName);
		
		Collection<WebService> services = configuration.getSoapServices();
		assertThat(services.size(), is(2));
		WebService soapService = configuration.getSoapService("mptu");
		assertThat(soapService.getName(), is("mptu"));
		assertThat(soapService.getServiceType(), is(ServiceType.SOAP));
		assertThat(soapService.getOperation(0).getOperationName(), is("prepayRequest"));

		String wsdlText = soapService.getWsdlText();
		Document wsdlDoc = XmlParser.parse(wsdlText, false);
		assertThat(wsdlDoc, hasXPath("/wsdl", equalTo("dummyContent123")));
		
		WebService restService = configuration.getSoapService("ticketing_refdata");
		assertThat(restService.getServiceType(), is(ServiceType.REST));
		assertThat(restService.getOperation(0).getOperationName(), is(HttpMethod.GET.toString()));
		

	}
	
	@Test(expected=FileNotFoundException.class)
	public void shouldThrowExceptionIfPropertyFileNotFound() throws ParserConfigurationException, SAXException, IOException {

		String mockPropertiesFileName = "not_existing.properties";
		propertyProcessor.process(mockPropertiesFileName);
	}


}
