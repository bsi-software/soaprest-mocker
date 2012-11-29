package net.sf.jaceko.mock.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

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
		String customResponseBody = "<dummyResponse>respTExt</dummyResponse>";
		int customResponseCode = 201;
		int delaySec = 1;

		resource.addResponse(serviceName, operationId, customResponseCode, delaySec, customResponseBody);
		MockResponse expectedResponse = new MockResponse(customResponseBody, customResponseCode, delaySec);

		verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

	}

	@Test
	public void shouldAddCustomResponse2() {

		String serviceName = "ticketing";
		String operationId = "GET";
		String customResponseBody = "<dummyResponse>respTExt2</dummyResponse>";
		int customResponseCode = 200;
		int delaySec = 2;

		resource.addResponse(serviceName, operationId, customResponseCode, delaySec, customResponseBody);
		MockResponse expectedResponse = new MockResponse(customResponseBody, customResponseCode, delaySec);

		verify(mockSetupExecutor).addCustomResponse(serviceName, operationId, expectedResponse);

	}

	@Test
	public void setResponseShouldReturnResponseWithStatusOK() {
		Response response = resource.setResponse("", "", 1, 0, 0, "");
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

		resource.setResponse(serviceName, operationId, responseInOrder, customResponseCode, delaySec, customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				new MockResponse(customResponseBody, customResponseCode, delaySec));

		serviceName = "prepayService";
		operationId = "prepayRequest";
		responseInOrder = 1;
		customResponseCode = 200;
		delaySec = 5;
		resource.setResponse(serviceName, operationId, responseInOrder, customResponseCode, delaySec, customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				new MockResponse(customResponseBody, customResponseCode, delaySec));
	}

	@Test
	public void shouldSetUpFifthResponse() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		int customResponseCode = 200;
		String customResponseBody = "<dummyResponse>abc123</dummyResponse>";
		int responseInOrder = 5;
		int delaySec = 3;

		resource.setResponse(serviceName, operationId, responseInOrder, customResponseCode, delaySec, customResponseBody);
		verify(mockSetupExecutor).setCustomResponse(serviceName, operationId, responseInOrder,
				new MockResponse(customResponseBody, customResponseCode, delaySec));

	}

}
