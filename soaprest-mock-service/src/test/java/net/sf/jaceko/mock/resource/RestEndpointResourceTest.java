package net.sf.jaceko.mock.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.http.HttpServletRequest;

import net.sf.jaceko.mock.resource.RestEndpointResource;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class RestEndpointResourceTest {

	private static final String NOT_USED_SERVICE_NAME = "";

	private RestEndpointResource resource = new RestEndpointResource();

	@Mock
	private WebserviceMockSvcLayer service;
	
	@Mock
	private HttpServletRequest servletContext; 

	@Before
	public void before() {
		initMocks(this);
		resource.setRestserviceMockSvcLayer(service);
	}

	@Test
	public void shouldPerformGetRequest() {
		String serviceName = "bms_refdata";
		String urlParams = "msg=abc";
		when(servletContext.getQueryString()).thenReturn(urlParams);
		resource.performGetRequest(serviceName, servletContext);
		verify(service).performRequest(serviceName, "GET", "", urlParams);
	}

	
	@Test
	public void shouldReturnResponseReturnedByGetRequest() {
		String responseReturnedByServuceLayer = "someResponse";
		when(service.performRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(
				responseReturnedByServuceLayer);
		assertThat(resource.performGetRequest(NOT_USED_SERVICE_NAME, servletContext), is(responseReturnedByServuceLayer));

	}
	
	@Test
	public void shouldPerformPostRequest() {
		String serviceName = "billdesk";
		String urlParams = "msg=abc";
		String request = "<dummyRequest>abc</dummyRequest>";
		when(servletContext.getQueryString()).thenReturn(urlParams);
		resource.performPostRequest(serviceName, servletContext, request);
		verify(service).performRequest(serviceName, "POST", request, urlParams);
	}
	
	@Test
	public void shouldReturnResponseReturnedByPostRequest() {
		String responseReturnedByServuceLayer = "someResponse";
		when(service.performRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(
				responseReturnedByServuceLayer);
		assertThat(resource.performPostRequest(NOT_USED_SERVICE_NAME, servletContext, null), is(responseReturnedByServuceLayer));

	}
	
	@Test
	public void shouldPerformPutRequest() {
		String serviceName = "dummyService";
		String request = "<dummyRequest>def</dummyRequest>";
		resource.performPutRequest(serviceName, request);
		verify(service).performRequest(serviceName, "PUT", request, null);
	}

	@Test
	public void shouldReturnResponseReturnedByPutRequest() {
		String responseReturnedByServuceLayer = "someResponse123";
		when(service.performRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(
				responseReturnedByServuceLayer);
		assertThat(resource.performPutRequest(NOT_USED_SERVICE_NAME, null), is(responseReturnedByServuceLayer));

	}

}
