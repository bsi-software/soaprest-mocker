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
 * Integration tests of REST mock, PUT method
 * 
 * @author Jacek Obarymski
 * 
 */
public class RestMockPUTMethodIntegrationTest {

	// mocked endpoints configured in ws-mock.properties
	private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/REST/dummy-rest";

	private static final String REST_MOCK_PUT_SETUP_INIT 				 	= "http://localhost:8080/mock/dummy-rest/PUT/setup/init";
	private static final String REST_MOCK_PUT_SETUP_RESPONSE 			 	= "http://localhost:8080/mock/dummy-rest/PUT/setup/response";
	private static final String REST_MOCK_PUT_SETUP_CONSECUTIVE_RESPONSE 	= "http://localhost:8080/mock/dummy-rest/PUT/setup/consecutive-response/";
	private static final String REST_MOCK_PUT_VERIFY_RECORDED_REQUESTS 	 	= "http://localhost:8080/mock/dummy-rest/PUT/recorded/requests";
	private static final String REST_MOCK_PUT_VERIFY_RECORDED_RESOURCE_IDS 	= "http://localhost:8080/mock/dummy-rest/PUT/recorded/resource-ids";

	HttpRequestSender requestSender = new HttpRequestSender();

	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		// initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_INIT, "", MediaType.TEXT_XML);
	}

	@Test
	public void shouldReturnDefaultRESTPostResponse() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {

		MockResponse response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "");
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//put_response_data", equalTo("default REST PUT response text")));
	}

	@Test
	public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		// setting up response body on mock
		// not setting custom response code
		String customResponseXML = "<custom_response>custom REST PUT response text</custom_response>";
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_RESPONSE, customResponseXML, MediaType.TEXT_XML);

		// sending REST PUT request
		MockResponse response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "");

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_response", equalTo("custom REST PUT response text")));

		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldReturnCustomRESTPutResponseBodyAndDefaultResponseCode_WhilePassingResourceId()
			throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		// setting up response body on mock
		// not setting custom response code
		String customResponseXML = "<custom_put_response>custom REST PUT response text</custom_put_response>";
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_RESPONSE, customResponseXML, MediaType.TEXT_XML);

		// sending REST PUT request
		MockResponse response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT + "/someResourceId", "");

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_put_response", equalTo("custom REST PUT response text")));

		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shoulVerifyResourceIds() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendPutRequest(REST_MOCK_ENDPOINT + "/id1", "");
		requestSender.sendPutRequest(REST_MOCK_ENDPOINT + "/id2", "");

		MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_PUT_VERIFY_RECORDED_RESOURCE_IDS);
		Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

		assertThat(verifyResponseDoc, hasXPath("//resourceIds/resourceId[1]", equalTo("id1")));
		assertThat(verifyResponseDoc, hasXPath("//resourceIds/resourceId[2]", equalTo("id2")));

	}

	@Test
	public void shouldReturnCustomRESTPutResponseBodyAndCode() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		String customResponseXML = "<custom_response>conflict</custom_response>";
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_RESPONSE + "?code=409", customResponseXML, MediaType.TEXT_XML);

		// sending REST PUT request
		MockResponse response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "");

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc, hasXPath("//custom_response", equalTo("conflict")));

		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_CONFLICT));

	}

	@Test
	public void shouldReturnConsecutiveCustomRESTPostResponses() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		// setting up consecutive responses on mock
		String customResponseXML1 = "<custom_put_response>custom REST PUT response text 1</custom_put_response>";
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_CONSECUTIVE_RESPONSE + "1", customResponseXML1, MediaType.TEXT_XML);

		String customResponseXML2 = "<custom_put_response>custom REST PUT response text 2</custom_put_response>";
		requestSender.sendPostRequest(REST_MOCK_PUT_SETUP_CONSECUTIVE_RESPONSE + "2", customResponseXML2, MediaType.TEXT_XML);

		MockResponse response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "");
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//custom_put_response", equalTo("custom REST PUT response text 1")));

		response = requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "");
		serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//custom_put_response", equalTo("custom REST PUT response text 2")));
	}

	@Test
	public void shoulVerifyRecordedRequests() throws UnsupportedEncodingException, ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText1</dummyReq>");
		requestSender.sendPutRequest(REST_MOCK_ENDPOINT, "<dummyReq>dummyReqText2</dummyReq>");

		MockResponse recordedRequests = requestSender.sendGetRequest(REST_MOCK_PUT_VERIFY_RECORDED_REQUESTS);
		Document requestUrlParamsDoc = new DocumentImpl(recordedRequests.getBody());

		assertThat(requestUrlParamsDoc, hasXPath("//requests/dummyReq[1]", equalTo("dummyReqText1")));
		assertThat(requestUrlParamsDoc, hasXPath("//requests/dummyReq[2]", equalTo("dummyReqText2")));

	}

}
