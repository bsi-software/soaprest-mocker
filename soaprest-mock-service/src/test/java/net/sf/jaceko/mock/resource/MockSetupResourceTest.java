package net.sf.jaceko.mock.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.ws.rs.core.Response;

import net.sf.jaceko.mock.model.MockResponse;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class MockSetupResourceTest {

	private MockSetupResource resource = new MockSetupResource();

	@Mock
	private WebserviceMockSvcLayer service;

	@Before
	public void before() {
		initMocks(this);
		resource.setWebserviceMockService(service);
	}
	
	@Test
	public void shouldPassClearRecordedRequestsToServiceLayer() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";

		resource.initMock(serviceName, operationId);
		verify(service).initMock(serviceName, operationId);

		serviceName = "mptu";
		operationId = "prepayRequest";

		resource.initMock(serviceName, operationId);
		verify(service).initMock(serviceName, operationId);

	}
	
	@Test
	public void initMockShouldReturnResponseWithStatusOK() {
		Response response = resource.initMock("", "");
		assertThat(response.getStatus(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldSetUpCustomResponse() {

		String serviceName = "ticketing";
		String operationId = "POST";
		String customResponseBody = "<dummyResponse>respTExt</dummyResponse>";
		int customResponseCode = 201;

		resource.setUpResponse(serviceName, operationId, customResponseCode, customResponseBody);
		int expectedResponseInOrder = 1;
		MockResponse expectedResponse = new MockResponse(customResponseBody, customResponseCode);

		
		verify(service).setCustomResponse(serviceName, operationId, expectedResponseInOrder,
				expectedResponse);

	}
	
	@Test
	public void shouldSetUpCustomResponse2() {

		String serviceName = "ticketing";
		String operationId = "GET";
		String customResponseBody = "<dummyResponse>respTExt2</dummyResponse>";
		int customResponseCode = 200;

		resource.setUpResponse(serviceName, operationId, customResponseCode, customResponseBody);
		int expectedResponseInOrder = 1;
		MockResponse expectedResponse = new MockResponse(customResponseBody, customResponseCode);

		verify(service).setCustomResponse(serviceName, operationId, expectedResponseInOrder,
				expectedResponse);

	}
	@Test
	public void setUpResponseShouldReturnResponseWithStatusOK() {
		Response response = resource.setUpResponse("", "",1, 0, "");
		assertThat(response.getStatus(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldPassSetUpConsecutiveResponseToServiceLayer() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		String customResponse = "<dummyResponse></dummyResponse>";
		int responseInOrder = 2;

		resource.setUpResponse(serviceName, operationId, responseInOrder, 0, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				new MockResponse(customResponse));

		serviceName = "mptu";
		operationId = "prepayRequest";
		responseInOrder = 1;

		resource.setUpResponse(serviceName, operationId, responseInOrder, 0, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				new MockResponse(customResponse));
	}

	@Test
	public void shouldPassSetUpDelayToServiceLayer() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		int delaySec = 5;

		resource.setUpRequestDelay(serviceName, operationId, delaySec);
		verify(service).setRequestDelay(serviceName, operationId, delaySec);

		serviceName = "mptu";
		operationId = "prepayRequest";

		delaySec = 10;

		resource.setUpRequestDelay(serviceName, operationId, delaySec);
		verify(service).setRequestDelay(serviceName, operationId, delaySec);

	}

	
}
