package net.sf.jaceko.mock.it;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jaceko.mock.it.helper.dom.DocumentImpl;
import net.sf.jaceko.mock.it.helper.request.HttpRequestSender;
import net.sf.jaceko.mock.model.request.MockResponse;

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
	private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest/endpoint";
	
	
	private static final String REST_MOCK_POST_INIT 						= "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/init";
	private static final String REST_MOCK_POST_RESPONSES 					= "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/responses";
	private static final String REST_MOCK_POST_RECORDED_REQUESTS	 		= "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-requests";
	private static final String REST_MOCK_POST_RECORDED_REQUEST_PARAMS 		= "http://localhost:8080/mock/services/REST/dummy-rest/operations/POST/recorded-request-params";
	
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		//initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_POST_INIT, "", MediaType.TEXT_XML);
	}

	@Test
	public void shouldReturnDefaultResponse()
			throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT,"", MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_CREATED));

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//post_response_data",
						equalTo("default REST POST response text")));
	}
		
	@Test
	public void shouldReturnCustomXmlResponseBodyAndDefaultResponseCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up xml response body on mock
		//not setting custom response code
		String customResponseXML = "<custom_post_response>custom REST POST response text</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES, customResponseXML, MediaType.TEXT_XML);
		
		//sending REST POST request 
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body xml", serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text")));

		
		assertThat("default response code", response.getCode(), is(HttpStatus.SC_CREATED));
	}
	
	@Test
	public void shouldReturnCustomJsonResponseBody() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up json response body on mock
		//not setting custom response code
		String customResponseJson = "{\"json\": \"obj\"}";
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES, customResponseJson, MediaType.APPLICATION_JSON);
		
		//sending REST POST request 
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat("custom response body xml", response.getBody(), is(customResponseJson));

	}	

	@Test
	public void shouldReturnXmlResponseBodyAndCode() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String customResponseXML = "<custom_post_response>not authorized</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=403", customResponseXML, MediaType.TEXT_XML);
		
		//sending REST POST request 
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("not authorized")));

		
		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		
	}


	@Test
	public void shouldReturnConsecutiveCustomXmlResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=403", customResponseXML1, MediaType.TEXT_XML);

		String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?code=200", customResponseXML2, MediaType.TEXT_XML);
		
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 1")));

		response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));

		serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 2")));
	}

	
	@Test
	public void shouldReturnConsecutiveCustomXmlResponses2() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseXML1 = "<custom_post_response>custom REST POST response text 1</custom_post_response>";
		requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/1" + "?code=403", customResponseXML1, MediaType.TEXT_XML);

		String customResponseXML2 = "<custom_post_response>custom REST POST response text 2</custom_post_response>";
		requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/2" + "?code=200", customResponseXML2, MediaType.TEXT_XML);
		
		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 1")));

		response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));

		serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(
				serviceResponseDoc,
				hasXPath("//custom_post_response",
						equalTo("custom REST POST response text 2")));
	}
	
	@Test
	public void shouldReturnConsecutiveCustomJsonResponses() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		//setting up consecutive responses on mock		
		String customResponseJson1 = "{\"custom_post_response\": \"custom REST POST response text 1\"}";
		requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/1" + "?code=403", customResponseJson1, MediaType.APPLICATION_JSON);

		String customResponseJson2 = "{\"custom_post_response\": \"custom REST POST response text 2\"}";
		requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/2" + "?code=200", customResponseJson2, MediaType.APPLICATION_JSON);

		MockResponse response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getBody(), is(customResponseJson1));

		response = requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		assertThat(response.getBody(), is(customResponseJson2));

	}
	
	@Test
	public void shouldDelayResponseFor1sec() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		requestSender.sendPostRequest(REST_MOCK_POST_RESPONSES + "?delay=1", "", MediaType.TEXT_XML);
		
		Calendar before = Calendar.getInstance();
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		Calendar after  = Calendar.getInstance();
		long oneSecInMilis = 1000l;
		assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(greaterThanOrEqualTo(oneSecInMilis)));
	}
	
	@Test
	public void shouldDelaySecondResponseFor1Sec() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		requestSender.sendPutRequest(REST_MOCK_POST_RESPONSES + "/2/?delay=1", "", MediaType.TEXT_XML);
		long oneSecInMilis = 1000l;


		//first request is not delayed
		Calendar before = Calendar.getInstance();
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		Calendar after  = Calendar.getInstance();
		assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(not(greaterThanOrEqualTo(oneSecInMilis))));

		//second request
		before = Calendar.getInstance();
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "", MediaType.TEXT_XML);
		after  = Calendar.getInstance();
		assertThat(after.getTimeInMillis() - before.getTimeInMillis(), is(greaterThanOrEqualTo(oneSecInMilis)));

	}
	@Test
	public void shoulVerifyRecordedRequests() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText1</dummyReq>", MediaType.TEXT_XML);
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText2</dummyReq>", MediaType.TEXT_XML);

		MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS);
		Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

		assertThat(
				requestUrlParamsDoc,
				hasXPath("//recorded-requests/dummyReq[1]", equalTo("dummyReqText1")));
		assertThat(
				requestUrlParamsDoc,
				hasXPath("//recorded-requests/dummyReq[2]", equalTo("dummyReqText2")));
	}

	@Test
	public void shoulVerifyRecordedJsonRequest() throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		String requestBody = "{\"dummyReq\": \"dummyReqText1\"}";
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT, requestBody, MediaType.APPLICATION_JSON);

		MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUESTS);
		Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

		assertThat(
				requestUrlParamsDoc,
				hasXPath("//recorded-requests", containsString(requestBody)));
	}

	
	@Test
	public void shoulVerifyRequestParameters() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT + "?param=paramValue1", "", MediaType.TEXT_XML);
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT + "?param=paramValue2", "", MediaType.TEXT_XML);
		
		MockResponse requestUrlParams = requestSender.sendGetRequest(REST_MOCK_POST_RECORDED_REQUEST_PARAMS);
		Document requestUrlParamsDoc = new DocumentImpl(requestUrlParams.getBody());

		assertThat(
				requestUrlParamsDoc,
				hasXPath("//recorded-request-params/recorded-request-param[1]",
						equalTo("param=paramValue1")));
		assertThat(
				requestUrlParamsDoc,
				hasXPath("//recorded-request-params/recorded-request-param[2]",
						equalTo("param=paramValue2")));
	}
	
}
