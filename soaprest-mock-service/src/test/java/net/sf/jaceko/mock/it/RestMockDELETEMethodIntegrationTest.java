package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.MockResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Integration tests of REST mock, DELETE method
 * 
 * @author Jacek Obarymski
 *
 */
public class RestMockDELETEMethodIntegrationTest {

	//mocked endpoints configured in ws-mock.properties
	private static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest/endpoint";
	
	
	
	private static final String REST_MOCK_DELETE_SETUP_INIT 				 	= "http://localhost:8080/mock/services/REST/dummy-rest/setup/DELETE/init";
	private static final String REST_MOCK_DELETE_SETUP_RESPONSE 			 	= "http://localhost:8080/mock/services/REST/dummy-rest/setup/DELETE/response";
	private static final String REST_MOCK_DELETE_SETUP_CONSECUTIVE_RESPONSE 	= "http://localhost:8080/mock/services/REST/dummy-rest/setup/DELETE/responses/";
	
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		//initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_INIT, "", MediaType.TEXT_XML);
	}

	@Test
	public void shouldReturnDefaultRESTPostResponse()
			throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		
		MockResponse response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//delete_response_data",
						equalTo("default REST DELETE response text")));
	}
	
	@Test
	public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up response body on mock
		//not setting custom response code
		String customResponseXML = "<custom_response>custom REST DELETE response text</custom_response>";
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_RESPONSE, customResponseXML, MediaType.TEXT_XML);
		
		//sending REST DELETE request 
		MockResponse response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT);
		
		
		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_response",
						equalTo("custom REST DELETE response text")));

		
	}

	
	@Test
	public void shouldReturnCustomRESTDeleteResponseBodyAndCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String customResponseXML = "<custom_response>conflict</custom_response>";
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_RESPONSE + "?code=409", customResponseXML, MediaType.TEXT_XML);
		
		//sending REST DELETE request 
		MockResponse response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT);
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_response",
						equalTo("conflict")));

		
		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_CONFLICT));
		
	}

	
	@Test
	public void shouldReturnConsecutiveCustomRESTDeleteResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_delete_response>custom REST DELETE response text 1</custom_delete_response>";
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_CONSECUTIVE_RESPONSE + "1", customResponseXML1, MediaType.TEXT_XML);

		String customResponseXML2 = "<custom_delete_response>custom REST DELETE response text 2</custom_delete_response>";
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_CONSECUTIVE_RESPONSE + "2", customResponseXML2, MediaType.TEXT_XML);
		
		MockResponse response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT);
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_delete_response",
						equalTo("custom REST DELETE response text 1")));

		response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT);
		serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_delete_response",
						equalTo("custom REST DELETE response text 2")));
	}
	
	@Test
	public void shouldReturnCustomRESTDeleteResponseBodyAndDefaultResponseCode_WhilePassingResourceId() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up response body on mock
		//not setting custom response code
		String customResponseXML = "<custom_delete_response>custom REST DELETE response text</custom_delete_response>";
		requestSender.sendPostRequest(REST_MOCK_DELETE_SETUP_RESPONSE, customResponseXML, MediaType.TEXT_XML);
		
		//sending REST DELETE request 
		MockResponse response = requestSender.sendDeleteRequest(REST_MOCK_ENDPOINT + "/someResourceId");
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_delete_response",
						equalTo("custom REST DELETE response text")));

		
		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
	}



}
