package net.sf.jaceko.mock.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.ws.rs.core.Response;

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
	public void shouldPassSetUpOf1stResponseToServiceLayer() {
		// by default 1st response is setup
		int responseInOrder = 1;

		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		String customResponse = "<dummyResponse></dummyResponse>";

		resource.setUpResponse(serviceName, operationId, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				customResponse);

		serviceName = "mptu";
		operationId = "prepayRequest";

		resource.setUpResponse(serviceName, operationId, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				customResponse);

	}

	@Test
	public void setUpResponseShouldReturnResponseWithStatusOK() {
		Response response = resource.setUpResponse("", "",1, "");
		assertThat(response.getStatus(), is(HttpStatus.SC_OK));
	}

	@Test
	public void shouldPassSetUpConsecutiveResponseToServiceLayer() {
		String serviceName = "ticketing";
		String operationId = "reserveRequest";
		String customResponse = "<dummyResponse></dummyResponse>";
		int responseInOrder = 2;

		resource.setUpResponse(serviceName, operationId, responseInOrder, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				customResponse);

		serviceName = "mptu";
		operationId = "prepayRequest";
		responseInOrder = 1;

		resource.setUpResponse(serviceName, operationId, responseInOrder, customResponse);
		verify(service).setCustomResponse(serviceName, operationId, responseInOrder,
				customResponse);
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
