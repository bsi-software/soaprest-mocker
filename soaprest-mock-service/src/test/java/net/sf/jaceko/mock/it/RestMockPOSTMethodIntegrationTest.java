package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.MockResponse;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Integration tests of REST mock, POST method
 * 
 * @author Jacek Obarymski
 *
 */
public class RestMockPOSTMethodIntegrationTest {

	//mocked endpoints configured in ws-mock.properties
	public static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/rest/dummy-rest";
	
	
	public static String REST_MOCK_POST_SETUP_INIT 						= "http://localhost:8080/mock/dummy-rest/POST/setup/init";
	public static String REST_MOCK_POST_SETUP_RESPONSE 					= "http://localhost:8080/mock/dummy-rest/POST/setup/response";
	public static String REST_MOCK_POST_SETUP_CONSECUTIVE_RESPONSE 		= "http://localhost:8080/mock/dummy-rest/POST/setup/consecutive-response/";
	public static String REST_MOCK_POST_VERIFY_RECORDED_REQUESTS	 	= "http://localhost:8080/mock/dummy-rest/POST/recorded/requests";
	public static String REST_MOCK_POST_VERIFY_RECORDED_REQUEST_PARAMS 	= "http://localhost:8080/mock/dummy-rest/POST/recorded/url-request-params";
	
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		//initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_INIT, "");
	}

	@Test
	public void shouldReturnDefaultRESTPostResponse()
			throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		
		String response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT,"");
		Document serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//post_response_data",
						equalTo("default REST POST response text")));
	}
	
	@Test
	public void shouldReturnCustomRESTPostResponse() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up response on mock
		String customResponseXML = "<custom_post_response>custom REST POST response text</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_RESPONSE, customResponseXML);
		
		//sending REST POST request 
		String response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT,"");
		Document serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text")));
	}
	
	@Test
	public void shouldReturnConsecutiveCustomRESTPostResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_CONSECUTIVE_RESPONSE + "1", customResponseXML1);

		String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_CONSECUTIVE_RESPONSE + "2", customResponseXML2);
		
		String response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		Document serviceResponseDoc = new DocumentImpl(response);
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 1")));

		response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		serviceResponseDoc = new DocumentImpl(response);
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 2")));
	}

	@Test
	public void shoulVerifyRecordedRequests() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText1</dummyReq>");
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText2</dummyReq>");

		MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_VERIFY_RECORDED_REQUESTS);
		Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

		assertThat(
				requestUrlParamsDoc,
				hasXPath("//requests/dummyReq[1]", equalTo("dummyReqText1")));
		assertThat(
				requestUrlParamsDoc,
				hasXPath("//requests/dummyReq[2]", equalTo("dummyReqText2")));


	}
	
	@Test
	public void shoulVerifyRequestParameters() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT + "?param=paramValue1", "");
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT + "?param=paramValue2", "");
		
		MockResponse requestUrlParams = requestSender.sendGetRequest(REST_MOCK_POST_VERIFY_RECORDED_REQUEST_PARAMS);
		Document requestUrlParamsDoc = new DocumentImpl(requestUrlParams.getBody());

		assertThat(
				requestUrlParamsDoc,
				hasXPath("//urlRequestParams/queryString[1]",
						equalTo("param=paramValue1")));
		assertThat(
				requestUrlParamsDoc,
				hasXPath("//urlRequestParams/queryString[2]",
						equalTo("param=paramValue2")));
	}
	
	

}
