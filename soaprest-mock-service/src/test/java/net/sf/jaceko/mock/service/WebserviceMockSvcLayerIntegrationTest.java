package net.sf.jaceko.mock.service;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;



public class WebserviceMockSvcLayerIntegrationTest {
	@Mock
	private MockserviceConfiguration configuration;
	


	private WebserviceMockSvcLayer serviceLayer = new WebserviceMockSvcLayer();

	@Before
	public void init() {
		initMocks(this);

		serviceLayer.setMockserviceConfiguration(configuration);
	}

	@Test
	public void shouldDelayRequest() {
		String serviceName = "mptu";
		String inputMessageName = "prepayRequest";
		WebserviceOperation operation = new WebserviceOperation(null, null, "aa", 0);
		operation.setCustomDelaySec(1);

		when(configuration.getWebServiceOperation(serviceName, inputMessageName))
				.thenReturn(operation);
		
	}
}
