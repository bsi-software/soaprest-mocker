package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
 * Integration tests of REST mock, POST method
 * 
 * @author Jacek Obarymski
 *
 */
public class RestMockPOSTMethodIntegrationTest {

	//mocked endpoints configured in ws-mock.properties
	public static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/REST/dummy-rest";
	
	
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
		
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT,"");
		assertThat(response.getCode(), is(HttpStatus.SC_CREATED));

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//post_response_data",
						equalTo("default REST POST response text")));
	}
		
	@Test
	public void shouldReturnCustomRESTPostResponseBodyAndDefaultResponseCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up response body on mock
		//not setting custom response code
		String customResponseXML = "<custom_post_response>custom REST POST response text</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_RESPONSE, customResponseXML);
		
		//sending REST POST request 
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text")));

		
		assertThat("default response code", response.getCode(), is(HttpStatus.SC_CREATED));
	}

	
	@Test
	public void shouldReturnCustomRESTPostResponseBodyAndCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String customResponseXML = "<custom_post_response>not authorized</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_RESPONSE + "?code=403", customResponseXML);
		
		//sending REST POST request 
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("not authorized")));

		
		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		
	}

	
	@Test
	public void shouldReturnConsecutiveCustomRESTPostResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_CONSECUTIVE_RESPONSE + "1" + "?code=403", customResponseXML1);

		String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_SETUP_CONSECUTIVE_RESPONSE + "2" + "?code=200", customResponseXML2);
		
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 1")));

		response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "");
		assertThat(response.getCode(), is(HttpStatus.SC_OK));

		serviceResponseDoc = new DocumentImpl(response.getBody());
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
