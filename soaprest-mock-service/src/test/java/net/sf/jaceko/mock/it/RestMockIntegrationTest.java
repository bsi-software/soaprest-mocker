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

public class RestMockIntegrationTest {

	//mocked endpoints configured in ws-mock.properties
	public static String REST_MOCK_ENDPOINT = "http://localhost:8080/mock/endpoint/rest/dummy-rest";
	
	public static String REST_MOCK_ENDPOINT_SETUP = "http://localhost:8080/mock/dummy-rest/GET/setup/";
	
	public static String REST_MOCK_ENDPOINT_SETUP_INIT =  REST_MOCK_ENDPOINT_SETUP + "init";
	
	public static String REST_MOCK_ENDPOINT_SETUP_RESPONSE = REST_MOCK_ENDPOINT_SETUP + "response";
	public static String REST_MOCK_ENDPOINT_SETUP_CONSECUTIVE_RESPONSE = REST_MOCK_ENDPOINT_SETUP + "consecutive-response/";
	
	HttpRequestSender requestSender = new HttpRequestSender();
	
	@Before
	public void initMock() throws UnsupportedEncodingException, ClientProtocolException, IOException {
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT_SETUP_INIT, "");
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
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT_SETUP_RESPONSE, customResponseXML);
		
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
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT_SETUP_CONSECUTIVE_RESPONSE + "1", customResponseXML1);

		String customResponseXML2 = "<custom_get_response>custom REST GET response text 2</custom_get_response>";
		requestSender.sendPostRequest(REST_MOCK_ENDPOINT_SETUP_CONSECUTIVE_RESPONSE + "2", customResponseXML2);
		
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


}
