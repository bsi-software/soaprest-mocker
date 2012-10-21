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
		String queryString = "msg=abc";
		when(servletContext.getQueryString()).thenReturn(queryString);
		resource.performGetRequest(serviceName, servletContext);
		verify(service).performRequest(serviceName, "GET", "", queryString);
	}

	@Test
	public void shouldReturnResponseReturnedByGetRequest() {
		String responseReturnedByServuceLayer = "someResponse";
		when(service.performRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(
				responseReturnedByServuceLayer);
		assertThat(resource.performGetRequest("", servletContext), is(responseReturnedByServuceLayer));

	}
	
	@Test
	public void shouldPerformPostRequest() {
		String serviceName = "billdesk";
		String queryString = "msg=abc";
		String request = "<dummyRequest>abc</dummyRequest>";
		when(servletContext.getQueryString()).thenReturn(queryString);
		resource.performPostRequest(serviceName, servletContext, request);
		verify(service).performRequest(serviceName, "POST", request, queryString);
	}
	
	@Test
	public void shouldReturnResponseReturnedByPostRequest() {
		String responseReturnedByServuceLayer = "someResponse";
		when(service.performRequest(anyString(), anyString(), anyString(), anyString())).thenReturn(
				responseReturnedByServuceLayer);
		assertThat(resource.performPostRequest("", servletContext, null), is(responseReturnedByServuceLayer));

	}

}
