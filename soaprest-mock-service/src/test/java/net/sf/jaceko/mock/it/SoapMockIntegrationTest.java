package net.sf.jaceko.mock.it;

import net.sf.jaceko.mock.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

public class SoapMockIntegrationTest {

	// mocked endpoints configured in ws-mock.properties
	private static final String SOAP_MOCK_ENDPOINT = "http://localhost:8088/mock/services/SOAP/hello-soap/endpoint";

	private static final String SOAP_MOCK_INIT = "http://localhost:8088/mock/services/SOAP/hello-soap/operations/sayHello/init";
	private static final String SOAP_MOCK_RESPONSES = "http://localhost:8088/mock/services/SOAP/hello-soap/operations/sayHello/responses";
	private static final String SOAP_MOCK_RECORDED_REQUESTS = "http://localhost:8088/mock/services/SOAP/hello-soap/operations/sayHello/recorded-requests";

	private static final String REQUEST = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:examples:helloservice\">\r\n"
			+ "   <soapenv:Header/>\r\n"
			+ "   <soapenv:Body>\r\n"
			+ "      <urn:sayHello soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
			+ "         <firstName xsi:type=\"xsd:string\">{0}</firstName>\r\n"
			+ "      </urn:sayHello>\r\n"
			+ "   </soapenv:Body>\r\n" + "</soapenv:Envelope>";

	private static final String RESPONSE_TEMPLATE = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:urn=\"urn:examples:helloservice\" xmlns:soapenv=\"soapenv\">\r\n"
			+ "   <soap:Body>\r\n"
			+ "      <urn:sayHello soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"
			+ "         <greeting xsi:type=\"xsd:string\">{0}</greeting>\r\n"
			+ "      </urn:sayHello>\r\n"
			+ "   </soap:Body>\r\n" + "</soap:Envelope>";

	private HttpRequestSender requestSender = new HttpRequestSender();

	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		// initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(SOAP_MOCK_INIT, "", MediaType.TEXT_XML);
	}

	@Test
	public void shouldReturnDefaultResponse() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {

		MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hello!!")));
	}

	@Test
	public void shouldReturnCustomResponseAndDefaultCode() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		// setting up xml response body on mock
		String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Hi!");
		requestSender.sendPostRequest(SOAP_MOCK_RESPONSES, customResponseXML, MediaType.TEXT_XML);

		// sending SOAP request
		MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hi!")));
		assertThat("default response code", response.getCode(), is(200));

	}

	@Test
	public void shouldReturnCustomResponseAndCustomCode() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		// setting up xml response body on mock
		String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Hola!");
		requestSender.sendPostRequest(SOAP_MOCK_RESPONSES + "?code=500", customResponseXML, MediaType.TEXT_XML);

		// sending SOAP request
		MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Hola!")));

		assertThat("custom response code", response.getCode(), is(500));
	}

	@Test
	public void shouldReturnCustomSecondResponse() throws UnsupportedEncodingException, ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		// setting up xml response body on mock
		String customResponseXML = MessageFormat.format(RESPONSE_TEMPLATE, "Aloha!");
		requestSender.sendPutRequest(SOAP_MOCK_RESPONSES + "/2", customResponseXML, MediaType.TEXT_XML);

		// sending 1st SOAP request
		requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);
		// sending 2nd SOAP request
		MockResponse response = requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, REQUEST, MediaType.TEXT_XML);

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//Envelope/Body/sayHello/greeting", equalTo("Aloha!")));
	}

	@Test
	public void shoulVerifyRecordedRequests() throws UnsupportedEncodingException, ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Jacek"), MediaType.TEXT_XML);
		requestSender.sendPostRequest(SOAP_MOCK_ENDPOINT, MessageFormat.format(REQUEST, "Peter"), MediaType.TEXT_XML);

		MockResponse recordedRequests = requestSender.sendGetRequest(SOAP_MOCK_RECORDED_REQUESTS);
        String body = recordedRequests.getBody().replace("<![CDATA[","").replace("]]>", "");
        Document requestUrlParamsDoc = new DocumentImpl(body);

		assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/Envelope[1]/Body/sayHello/firstName", equalTo("Jacek")));

		assertThat(requestUrlParamsDoc, hasXPath("//recorded-requests/Envelope[2]/Body/sayHello/firstName", equalTo("Peter")));

	}

}
