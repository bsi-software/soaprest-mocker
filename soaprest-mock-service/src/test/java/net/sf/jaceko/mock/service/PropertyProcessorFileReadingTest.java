package net.sf.jaceko.mock.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.application.enums.ServiceType;
import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.matcher.OperationHavingNameEqualTo;
import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PropertyProcessorFileReadingTest {
	private PropertyProcessor propertyProcessor = new PropertyProcessor();

	@Test
	public void shouldReadWsdlContentsFromFile() throws IOException, ParserConfigurationException, SAXException {
		String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].WSDL=hello-for-unit-tests.wsdl\r\n"
				+ "SERVICE[0].TYPE=SOAP\r\n" + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=someRequest\r\n";

		Reader reader = new StringReader(propertyString);
		MockConfigurationHolder configuration = propertyProcessor.process(reader);
		Collection<WebService> services = configuration.getWebServices();
		WebService soapService = services.iterator().next();
		String wsdlText = soapService.getWsdlText();
		Document wsdlDoc = new DocumentImpl(wsdlText);
		assertThat(wsdlDoc, hasXPath("/definitions/message/@name", equalTo("SayHelloRequest")));

	}

	@Test
	public void shouldReadDefaultResponseContentsFromFile() throws IOException, ParserConfigurationException, SAXException {
		String propertyString = "SERVICE[0].NAME=dummysoapservice\r\n" + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=dummyRequest\r\n"
				+ "SERVICE[0].OPERATION[0].DEFAULT_RESPONSE=dummy_soap_response.xml\r\n";

		Reader reader = new StringReader(propertyString);
		MockConfigurationHolder configuration = propertyProcessor.process(reader);

		Collection<WebService> services = configuration.getWebServices();
		WebService soapService = services.iterator().next();
		WebserviceOperation operation = soapService.getOperation(0);

		String responseXML = operation.getDefaultResponseText();
		Document responseDoc = new DocumentImpl(responseXML);

		assertThat(responseDoc, hasXPath("/dummyResponse/reqId", equalTo("789789")));
		assertThat(responseDoc, hasXPath("/dummyResponse/status", equalTo("OK")));

	}

	@Test
	public void shouldContinueIfResponseFileNotFound() throws IOException, ParserConfigurationException, SAXException {
		String propertyString = "SERVICE[0].NAME=ticketing\r\n" + "SERVICE[0].OPERATION[0].INPUT_MESSAGE=reserveRequest\r\n"
				+ "SERVICE[0].OPERATION[1].INPUT_MESSAGE=confirmRequest\r\n";

		Reader reader = new StringReader(propertyString);
		MockConfigurationHolder configuration = propertyProcessor.process(reader);

		Collection<WebService> services = configuration.getWebServices();
		WebService soapService = services.iterator().next();
		WebserviceOperation operation = soapService.getOperation(0);
		assertThat(operation, notNullValue());

	}

	@Test
	public void shouldReadPropertiesFromFile() throws ParserConfigurationException, SAXException, IOException {

		String mockPropertiesFileName = "ws-mock-for-unit-tests.properties";
		MockConfigurationHolder configuration = propertyProcessor.process(mockPropertiesFileName);

		Collection<WebService> services = configuration.getWebServices();
		assertThat(services.size(), is(2));
		WebService soapService = configuration.getWebService("dummy_soap");
		assertThat(soapService.getName(), is("dummy_soap"));
		assertThat(soapService.getServiceType(), is(ServiceType.SOAP));
		assertThat(soapService.getOperation(0).getOperationName(), is("dummySoapRequest"));

		String wsdlText = soapService.getWsdlText();
		Document wsdlDoc = new DocumentImpl(wsdlText);
		assertThat(wsdlDoc, hasXPath("/definitions/service/documentation", equalTo("Dummy wsdl file")));

		WebService restService = configuration.getWebService("dummy_rest_get");
		assertThat(restService.getServiceType(), is(ServiceType.REST));
		assertThat(restService.getOperation(0).getOperationName(), is(HttpMethod.GET.toString()));
		assertThat(restService.getOperation(0).getDefaultResponseCode(), is(200));

	}

	@Test(expected = FileNotFoundException.class)
	public void shouldThrowExceptionIfPropertyFileNotFound() throws ParserConfigurationException, SAXException, IOException {

		String mockPropertiesFileName = "not_existing.properties";
		propertyProcessor.process(mockPropertiesFileName);
	}

	@Test
	public void shouldReadOperationFromWsdl() throws IOException {
		String wsdlName = "hello-for-unit-tests.wsdl";

		String propertyString = "SERVICE[0].NAME=hello\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations.size(), is(1));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("sayHello")));
	}

	@Test
	public void shouldReadOperationFromWsdl2() throws IOException, ParserConfigurationException, SAXException {
		String wsdlName = "bookReservation.wsdl";

		String propertyString = "SERVICE[0].NAME=someService\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations.size(), is(1));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("Reservation")));
		WebserviceOperation operation = operations.iterator().next();
		DocumentImpl defaultResponseDoc = new DocumentImpl(operation.getDefaultResponseText());
		assertThat(defaultResponseDoc, hasXPath("//Envelope/Body/ReservationResponse"));

	}

	@Test
	public void shouldReadMultipleOperationsFromWsdl() throws IOException {
		String wsdlName = "ebi-mafft.wsdl";

		String propertyString = "SERVICE[0].NAME=multipleOperationsService\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations, hasSize(6));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("getParameterDetails")));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("getParameters")));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("getResult")));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("getResultTypes")));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("getStatus")));
		assertThat(operations, hasItem(new OperationHavingNameEqualTo("run")));

	}

	@Test
	public void shouldGenerateDafaultResponseFromWsdl() throws IOException, ParserConfigurationException, SAXException {
		String wsdlName = "hello-for-unit-tests.wsdl";

		String propertyString = "SERVICE[0].NAME=hello\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		WebserviceOperation operation = operations.iterator().next();
		DocumentImpl defaultResponseDoc = new DocumentImpl(operation.getDefaultResponseText());
		assertThat(defaultResponseDoc, hasXPath("//Envelope/Body/sayHelloResponse/greeting"));

	}
	
	@Test
	public void shouldGenerateDafaultResponseFromWsdl2() throws IOException, ParserConfigurationException, SAXException {
		String wsdlName = "bookReservation.wsdl";

		String propertyString = "SERVICE[0].NAME=someService\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations.size(), is(1));
		WebserviceOperation operation = operations.iterator().next();
		DocumentImpl defaultResponseDoc = new DocumentImpl(operation.getDefaultResponseText());
		assertThat(defaultResponseDoc, hasXPath("//Envelope/Body/ReservationResponse"));

	}

	
	@Test
	public void shouldGenerateDafaultResponseFromWsdl3() throws IOException, ParserConfigurationException, SAXException {
		String wsdlName = "webservicex-rate-convertor.wsdl";

		String propertyString = "SERVICE[0].NAME=someOtherService\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations.size(), is(1));
		WebserviceOperation operation = operations.iterator().next();
		DocumentImpl defaultResponseDoc = new DocumentImpl(operation.getDefaultResponseText());
		assertThat(defaultResponseDoc, hasXPath("//Envelope/Body/ConversionRateResponse/ConversionRateResult"));

	}


	@Test
	public void shouldProcessWsdlWithNoBindings() throws IOException {
		String wsdlName = "nobindings.wsdl";

		String propertyString = "SERVICE[0].NAME=emptyService\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations, hasSize(0));

	}

	@Test
	public void shouldProcessNotValidWsdl() throws IOException {
		String wsdlName = "notvalid.wsdl";

		String propertyString = "SERVICE[0].NAME=NotValid\r\n" + "SERVICE[0].WSDL=" + wsdlName;
		Reader reader = new StringReader(propertyString);

		Collection<WebService> webServices = propertyProcessor.process(reader).getWebServices();
		assertThat(webServices.size(), is(1));
		WebService webService = webServices.iterator().next();
		Collection<WebserviceOperation> operations = webService.getOperations();
		assertThat(operations, hasSize(0));

	}

}
