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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Integration tests of REST mock, GET method
 * 
 * @author Jacek Obarymski
 * 
 */
public class RestMockGETMethodIntegrationTest {

	// mocked endpoints configured in ws-mock.properties
	private static final String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/services/REST/dummy-rest/endpoint";
	private static final String REST_MOCK_ENDPOINT_FORBIDDEN_RESPONSE_CODE = "http://localhost:8080/mock/services/REST/dummy-rest-notauthorized/endpoint";

	private static final String REST_MOCK_GET_INIT = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/init";
	private static final String REST_MOCK_GET_RESPONSES = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/responses";
	private static final String REST_MOCK_GET_RECORDED_RESOURCE_IDS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-resource-ids";
	private static final String REST_MOCK_GET_RECORDED_REQUEST_PARAMS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-request-params";

    private static final String REST_MOCK_GET_RECORDED_REQUESTS_HEADERS = "http://localhost:8080/mock/services/REST/dummy-rest/operations/GET/recorded-request-headers";

	HttpRequestSender requestSender = new HttpRequestSender();

	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		// initalizing mock, clearing history of previous requests
		requestSender.sendPostRequest(REST_MOCK_GET_INIT, "", MediaType.TEXT_XML);
	}

	// default response defined in ws-mock.properties
	@Test
	public void shouldReturnDefaultRESTGetResponse() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		assertThat(response.getContentType(), is("application/vnd.specific+xml"));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//get_response_data", equalTo("default REST GET response text")));

	}

	@Test
	public void shouldReturnDefaultRESTGetResponse2() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT_FORBIDDEN_RESPONSE_CODE);
		assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));

	}

	@Test
	public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode() throws UnsupportedEncodingException,
			ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		// setting up response body on mock
		// not setting custom response code
		String customResponseXML = "<custom_get_response>custom REST GET response text</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES, customResponseXML, MediaType.TEXT_XML);

		// sending REST GET request
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);

		assertThat(response.getContentType(), is(MediaType.TEXT_XML_TYPE.toString()));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_get_response", equalTo("custom REST GET response text")));

		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldReturnCustomRESTGetResponseBodyAndDefaultResponseCode_WhilePassingResourceId()
			throws UnsupportedEncodingException, ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		// setting up response body on mock
		// not setting custom response code
		String customResponseXML = "<custom_get_response>custom REST GET response text</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES, customResponseXML, MediaType.TEXT_XML);

		// sending REST GET request
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "/someResourceId");
		assertThat(response.getContentType(), is(MediaType.TEXT_XML_TYPE.toString()));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc,
				hasXPath("//custom_get_response", equalTo("custom REST GET response text")));

		assertThat("default response code", response.getCode(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldReturnCustomRESTGetResponseBodyAndCode() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		String customResponseXML = "<custom_get_response>not authorized</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?code=403", customResponseXML, MediaType.TEXT_XML);

		// sending REST GET request
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);

		Document serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat("custom response body", serviceResponseDoc, hasXPath("//custom_get_response", equalTo("not authorized")));

		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_FORBIDDEN));

	}

	@Test
	public void shouldReturnCustomRESTGetResponseCode() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?code=401", null, MediaType.APPLICATION_JSON);

		// sending REST GET request
		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);

		assertThat("custom response code", response.getCode(), is(HttpStatus.SC_UNAUTHORIZED));

	}

	@Test
	public void shouldReturnConsecutiveCustomRESTGetResponses() throws UnsupportedEncodingException, ClientProtocolException,
			IOException, ParserConfigurationException, SAXException {
		// setting up consecutive responses on mock
		String customResponseXML1 = "<custom_get_response>custom REST GET response text 1</custom_get_response>";
		requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/1" + "?code=200", customResponseXML1, MediaType.TEXT_XML);

		String customResponseXML2 = "<custom_get_response>custom REST GET response text 2</custom_get_response>";
		requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/2" + "?code=403", customResponseXML2, MediaType.TEXT_XML);

		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_OK));
		Document serviceResponseDoc = new DocumentImpl(response.getBody());

		assertThat(serviceResponseDoc, hasXPath("//custom_get_response", equalTo("custom REST GET response text 1")));

		response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_FORBIDDEN));
		serviceResponseDoc = new DocumentImpl(response.getBody());
		assertThat(serviceResponseDoc, hasXPath("//custom_get_response", equalTo("custom REST GET response text 2")));
	}

	@Test
	public void shouldReturnDefaultResponseCode() throws UnsupportedEncodingException, ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		// setting up 1st response on mock, without response code
		requestSender.sendPutRequest(REST_MOCK_GET_RESPONSES + "/1", "", MediaType.TEXT_XML);

		MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);
		assertThat(response.getCode(), is(HttpStatus.SC_OK)); // default
																// response code
																// defined in
																// ws-mock.properties

	}

	@Test
	public void shouldVerifyRequestParameters() throws ClientProtocolException, IOException, ParserConfigurationException,
			SAXException {
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "?param=paramValue1");
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "?param=paramValue2");

		MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_REQUEST_PARAMS);
		Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

		assertThat(verifyResponseDoc,
				hasXPath("//recorded-request-params/recorded-request-param[1]", equalTo("param=paramValue1")));
		assertThat(verifyResponseDoc,
				hasXPath("//recorded-request-params/recorded-request-param[2]", equalTo("param=paramValue2")));

	}

	@Test
	public void shouldVerifyResourceIds() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "/id123");
		requestSender.sendGetRequest(REST_MOCK_ENDPOINT + "/id567");

		MockResponse verifyResponse = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_RESOURCE_IDS);
		Document verifyResponseDoc = new DocumentImpl(verifyResponse.getBody());

		assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[1]", equalTo("id123")));
		assertThat(verifyResponseDoc, hasXPath("//recorded-resource-ids/recorded-resource-id[2]", equalTo("id567")));

	}

    @Test
    public void shouldReturnCustomResponseWithHeader() throws Exception {
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?headers=X-Signature::signatureValue", "<body/>", MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);

        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
    }

    @Test
    public void shouldReturnCustomResponseWithMultipleHeaders() throws Exception {
        requestSender.sendPostRequest(REST_MOCK_GET_RESPONSES + "?headers=X-Signature::signatureValue,,X-Date::tomorrow", "<body/>", MediaType.TEXT_XML);

        // sending REST GET request
        MockResponse response = requestSender.sendGetRequest(REST_MOCK_ENDPOINT);

        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Signature"), equalTo("signatureValue"));
        assertThat("Expected X-Date header to be returned from mock", response.getHeader("X-Date"), equalTo("tomorrow"));
    }

    @Test
    public void shouldVerifyRecordedRequestsWithHeaders() throws Exception {
        // Given we've sent a get request with headers
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("aHeader","aValue");
        requestSender.sendGetRequest(REST_MOCK_ENDPOINT, headers);

        //When we get the recorded headers
        MockResponse recordedRequestsHeaders = requestSender.sendGetRequest(REST_MOCK_GET_RECORDED_REQUESTS_HEADERS);

        //Then the header sent in the Get request is returned
        System.out.println(recordedRequestsHeaders.getBody());
        assertThat("Expected a response body", recordedRequestsHeaders.getBody(), notNullValue());
        Document requestUrlParamsDoc = new DocumentImpl(recordedRequestsHeaders.getBody());

        assertThat(recordedRequestsHeaders.getCode(), equalTo(200));
        assertThat(requestUrlParamsDoc, hasXPath("/recorded-request-headers/single-request-recorded-headers[1]/header/name[text()='aHeader']//..//value", equalTo("aValue")));
    }

}
