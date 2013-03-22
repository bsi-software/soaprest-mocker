package net.sf.jaceko.mock.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.RequestExecutor;

import org.apache.commons.httpclient.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class RestEndpointResourceTest {

	private static final String NOT_USED_RESPONSE_BODY = "";

	private static final MockResponse NOT_USED_RESPONSE = new MockResponse();

	private static final String NOT_USED_RESOURCE_ID = null;

	private static final String NOT_USED_SERVICE_NAME = "";

	private RestEndpointResource resource = new RestEndpointResource();

	@Mock
	private RequestExecutor requestExecutor;

	@Mock
	private HttpServletRequest servletContext;

	@Before
	public void before() {
		initMocks(this);
		resource.setWebserviceMockService(requestExecutor);
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				NOT_USED_RESPONSE);
	}

	@Test
	public void shouldPerformGetRequest() {
		String serviceName = "bms_refdata";
		String urlParams = "msg=abc";
		when(servletContext.getQueryString()).thenReturn(urlParams);
		resource.performGetRequest(serviceName, servletContext);
		verify(requestExecutor).performRequest(serviceName, "GET", NOT_USED_RESPONSE_BODY, urlParams, null);
	}

	@Test
	public void shouldPerformGetRequestPassingResourceId() {
		String serviceName = "restServiceName";
		String resourceId = "resId12";
		String urlParams = "msg=def";
		when(servletContext.getQueryString()).thenReturn(urlParams);
		resource.performGetRequest(serviceName, servletContext, resourceId);
		verify(requestExecutor).performRequest(serviceName, "GET", NOT_USED_RESPONSE_BODY, urlParams, resourceId);
	}

	@Test
	public void shouldReturnGET_OKResponse() {
		String responseReturnedByServiceLayer = "someResponseText";
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_OK;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));
		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext);
		assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
	}

	@Test
	public void shouldReturnGET_FORBIDDEN_Response() {
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_FORBIDDEN;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(null, responseCodeReturnedByServiceLayer));
		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext);
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
	}

	@Test
	public void shouldReturnGET_OKResponseOnRequestWith_RESOURCE_ID() {
		String responseReturnedByServiceLayer = "someResponseText";
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_OK;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));

		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext, NOT_USED_RESOURCE_ID);
		assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));

	}

	@Test
	public void shouldReturnGET_FORBIDDEN_ResponseOnRequestWith_RESOURCE_ID() {
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_FORBIDDEN;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(null, responseCodeReturnedByServiceLayer));
		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext, NOT_USED_RESOURCE_ID);
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
	}

	@Test
	public void shouldReturnJsonContentType() {
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				MockResponse.body(NOT_USED_RESPONSE_BODY).contentType(APPLICATION_JSON_TYPE).build());

		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext);
		assertThat(getResponse.getMetadata().getFirst("Content-Type").toString(), is("application/json"));

	}

	@Test
	public void shouldReturnXMLContentType() {
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				MockResponse.body(NOT_USED_RESPONSE_BODY).contentType(MediaType.TEXT_XML_TYPE).build());

		Response getResponse = resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext);
		assertThat(getResponse.getMetadata().getFirst("Content-Type").toString(), is("text/xml"));

	}

	
	@Test
	public void shouldPerformPostRequest() {
		String serviceName = "billdesk";
		String urlParams = "msg=abc";
		String request = "<dummyRequest>abc</dummyRequest>";
		when(servletContext.getQueryString()).thenReturn(urlParams);
		resource.performPostRequest(serviceName, servletContext, request);
		verify(requestExecutor).performRequest(serviceName, "POST", request, urlParams, null);
	}

	@Test
	public void shouldReturnPOST_CREATEDResponse() {
		String responseReturnedByServiceLayer = "someResponse";
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_CREATED;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));
		Response response = resource.performPostRequest(NOT_USED_SERVICE_NAME, servletContext, null);
		assertThat((String) response.getEntity(), is(responseReturnedByServiceLayer));
		assertThat(response.getStatus(), is(responseCodeReturnedByServiceLayer));

	}

	@Test
	public void shouldPerformPutRequest() {
		String serviceName = "dummyService";
		String request = "<dummyRequest>def</dummyRequest>";
		resource.performPutRequest(serviceName, request);
		verify(requestExecutor).performRequest(serviceName, "PUT", request, null, null);
	}

	@Test
	public void shouldPerformPutRequestPassingResourceId() {
		String serviceName = "restServiceName";
		String resourceId = "resId12";
		String request = "<dummyRequest>abc</dummyRequest>";
		resource.performPutRequest(serviceName, resourceId, request);
		verify(requestExecutor).performRequest(serviceName, "PUT", request, null, resourceId);
	}

	@Test
	public void shouldReturnPUT_CONFLICTResponse() {
		String responseReturnedByServiceLayer = "someResponse123";
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, 409));
		Response response = resource.performPutRequest(NOT_USED_SERVICE_NAME, null);
		assertThat((String) response.getEntity(), is(responseReturnedByServiceLayer));

	}

	@Test
	public void shouldPerformDeleteRequest() {
		String serviceName = "dummyService";
		resource.performDeleteRequest(serviceName);
		verify(requestExecutor).performRequest(serviceName, "DELETE", NOT_USED_RESPONSE_BODY, null, null);
	}

	@Test
	public void shouldPerformDeleteRequestPassingResourceId() {
		String serviceName = "restServiceName";
		String resourceId = "resId12";
		resource.performDeleteRequest(serviceName, resourceId);
		verify(requestExecutor).performRequest(serviceName, "DELETE", NOT_USED_RESPONSE_BODY, null, resourceId);
	}

	@Test
	public void shouldReturnDELETE_NO_CONTENTResponse() {
		String responseReturnedByServiceLayer = "someResponseText";
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_NO_CONTENT;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));
		Response getResponse = resource.performDeleteRequest(NOT_USED_SERVICE_NAME);
		assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));
	}

	@Test
	public void shouldReturnDELETE_NO_CONTENTResponseOnRequestWith_RESOURCE_ID() {
		String responseReturnedByServiceLayer = "someResponseText";
		int responseCodeReturnedByServiceLayer = HttpStatus.SC_NO_CONTENT;
		when(requestExecutor.performRequest(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(
				new MockResponse(responseReturnedByServiceLayer, responseCodeReturnedByServiceLayer));

		Response getResponse = resource.performDeleteRequest(NOT_USED_SERVICE_NAME, NOT_USED_RESOURCE_ID);
		assertThat((String) getResponse.getEntity(), is(responseReturnedByServiceLayer));
		assertThat(getResponse.getStatus(), is(responseCodeReturnedByServiceLayer));

	}

}
