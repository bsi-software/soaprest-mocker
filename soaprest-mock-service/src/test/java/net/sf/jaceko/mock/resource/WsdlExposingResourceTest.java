package net.sf.jaceko.mock.resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import net.sf.jaceko.mock.resource.WsdlExposingResource;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class WsdlExposingResourceTest {
	
	@Mock
	private WebserviceMockSvcLayer service;
	
	private WsdlExposingResource resource = new WsdlExposingResource();
	
	@Before
	public void before() {
		initMocks(this);
		resource.setWebserviceMockService(service);
	}
	
	@Test
	public void shouldExposeWsdl() {
		String serviceName = "ticketing";
		String expectedWsdlString = "<dummy/>";

		when(service.getWsdl(serviceName)).thenReturn(expectedWsdlString);
		String wsdlStringResp = resource.getWsdl(serviceName);
		assertThat(wsdlStringResp, is(expectedWsdlString));

		serviceName = "mptu";
		expectedWsdlString = "<dummy2/>";
		when(service.getWsdl(serviceName)).thenReturn(expectedWsdlString);
		wsdlStringResp = resource.getWsdl(serviceName);
		assertThat(wsdlStringResp, is(expectedWsdlString));
	}



}
