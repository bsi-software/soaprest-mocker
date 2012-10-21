package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Integration tests of REST mock, GET method
 * 
 * @author Jacek Obarymski
 *
 */
public class RestMockGETMethodIntegrationTest {

	//mocked endpoints configured in ws-mock.properties
	public static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/rest/dummy-rest";
	
	
	public static String REST_MOCK_GET_SETUP_INIT 						= "http://localhost:8080/mock/dummy-rest/GET/setup/init";
	public static String REST_MOCK_GET_SETUP_RESPONSE 					= "http://localhost:8080/mock/dummy-rest/GET/setup/response";
	public static String REST_MOCK_GET_SETUP_CONSECUTIVE_RESPONSE 		= "http://localhost:8080/mock/dummy-rest/GET/setup/consecutive-response/";
	public static String REST_MOCK_GET_VERIFY_RECORDED_RESOURCE_IDS 	= "http://localhost:8080/mock/dummy-rest/GET/recorded/resource-ids";
	public static String REST_MOCK_GET_VERIFY_RECORDED_REQUEST_PARAMS 	= "http://localhost:8080/mock/dummy-rest/GET/recorded/url-request-params";
	
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		//initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_GET_SETUP_INIT, "");
	}
	
	@Test
	public void shouldReturnDefaultRESTGetResponse()
			throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		Document serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//get_response_data",
						equalTo("default REST GET response text")));

	}
	
	@Test
	public void shouldReturnCustomRESTGetResponse() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up response on mock
		String customResponseXML = "<custom_get_response>custom REST GET response text</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_SETUP_RESPONSE, customResponseXML);
		
		//sending REST GET request 
		String response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		Document serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_get_response",
						equalTo("custom REST GET response text")));

		
	}
	
	@Test
	public void shouldReturnConsecutiveCustomRESTGetResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_get_response>custom REST GET response text 1</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_SETUP_CONSECUTIVE_RESPONSE + "1", customResponseXML1);

		String customResponseXML2 = "<custom_get_response>custom REST GET response text 2</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_SETUP_CONSECUTIVE_RESPONSE + "2", customResponseXML2);
		
		String response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		Document serviceResponseDoc = new DocumentImpl(response);
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_get_response",
						equalTo("custom REST GET response text 1")));

		response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_get_response",
						equalTo("custom REST GET response text 2")));
	}

	@Test
	public void shoulVerifyRequestParameters() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "?param=paramValue1");
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "?param=paramValue2");
		
		String verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_VERIFY_RECORDED_REQUEST_PARAMS);
		Document verifyResponseDoc = new DocumentImpl(verifyResponse);

		assertThat(
				verifyResponseDoc,
				hasXPath("//urlRequestParams/queryString[1]",
						equalTo("param=paramValue1")));
		assertThat(
				verifyResponseDoc,
				hasXPath("//urlRequestParams/queryString[2]",
						equalTo("param=paramValue2")));

		
	}
	
	@Test
	public void shoulVerifyResourceIds() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "/id123");
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "/id567");
		
		String verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_VERIFY_RECORDED_RESOURCE_IDS);
		Document verifyResponseDoc = new DocumentImpl(verifyResponse);

		assertThat(
				verifyResponseDoc,
				hasXPath("//resourceIds/resourceId[1]",
						equalTo("id123")));
		assertThat(
				verifyResponseDoc,
				hasXPath("//resourceIds/resourceId[2]",
						equalTo("id567")));

	}

}
