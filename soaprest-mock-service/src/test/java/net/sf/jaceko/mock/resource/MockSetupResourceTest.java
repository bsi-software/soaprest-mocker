package net.sf.jaceko.mock.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.model.request.MockResponse;
import net.sf.jaceko.mock.service.MockSetupExecutor;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class MockSetupResourceTest {

	private BasicSetupResource resource = new RestServiceMockSetupResource();

	@Mock
	private MockSetupExecutor mockSetupExecutor;

	@Before
	public void before() {
		initMocks(this);
		resource.setMockSetupExecutor(mockSetupExecutor);
	}

	@Test
	public void shouldPassClearRecordedRequestsToServiceLayer() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";

		resource.initMock(serviceName, operationId);
		verify(mockSetupExecutor).initMock(serviceName, operationId);

		serviceName = "prepayService";
		operationId = "prepayRequest";

		resource.initMock(serviceName, operationId);
		verify(mockSetupExecutor).initMock(serviceName, operationId);

	}

	@Test
	public void initMockShouldReturnResponseWithStatusOK() {
		Response response = resource.initMock("", "");
		assertThat(response.getStatus(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldAddCustomResponse() {

		String serviceName = "ticketing";
		String operationId = "POST";
		String customResponseBody = "[]";
		int customResponseCode = 201;
		int delaySec = 1;
		MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);

		resource.addResponse(httpHeaders, serviceName, operationId, customResponseCode, delaySec, customResponseBody);
		MockResponse expectedResponse = MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec)
				.contentType(mediaType).build();

		verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

	}

	@Test
	public void shouldAddCustomResponse2() {

		String serviceName = "ticketing";
		String operationId = "GET";
		String customResponseBody = "<dummyResponse>respTExt2</dummyResponse>";
		int customResponseCode = 200;
		int delaySec = 2;
		MediaType mediaType = MediaType.APPLICATION_XML_TYPE;

		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);

		resource.addResponse(httpHeaders, serviceName, operationId, customResponseCode, delaySec, customResponseBody);
		MockResponse expectedResponse = MockResponse.body(customResponseBody).code(customResponseCode).contentType(mediaType)
				.delaySec(delaySec).build();

		verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

	}

	@Test
	public void setResponseShouldReturnResponseWithStatusOK() {
		Response response = resource.setResponse(mock(HttpHeaders.class), "", "", 1, 0, 0, "");
		assertThat(response.getStatus(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldSetUpSecondResponse() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		int customResponseCode = 201;
		String customResponseBody = "<dummyResponse>aabb</dummyResponse>";
		int responseInOrder = 2;
		int delaySec = 2;
		MediaType mediaType = MediaType.APPLICATION_XML_TYPE;

		HttpHeaders httpHeaders = mock(HttpHeaders.class);
		when(httpHeaders.getMediaType()).thenReturn(mediaType);

		resource.setResponse(httpHeaders, serviceName, operationId, responseInOrder, customResponseCode, delaySec,
				customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec).contentType(mediaType).build());

		serviceName = "prepayService";
		operationId = "prepayRequest";
		responseInOrder = 1;
		customResponseCode = 200;
		delaySec = 5;
		mediaType = MediaType.APPLICATION_JSON_TYPE;

		when(httpHeaders.getMediaType()).thenReturn(mediaType);

		resource.setResponse(httpHeaders, serviceName, operationId, responseInOrder, customResponseCode, delaySec,
				customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				MockResponse.body(customResponseBody).code(customResponseCode).delaySec(delaySec).contentType(mediaType).build());
	}

	@Test
	public void shouldSetUpFifthResponse() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		int customResponseCode = 200;
		String customResponseBody = "<dummyResponse>abc123</dummyResponse>";
		int responseInOrder = 5;

		resource.setResponse(mock(HttpHeaders.class), serviceName, operationId, responseInOrder, customResponseCode, 0,
				customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				MockResponse.body(customResponseBody).code(customResponseCode).build());

	}

}
