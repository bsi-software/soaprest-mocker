package net.sf.jaceko.mock.resource;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Collection;

import net.sf.jaceko.mock.model.webservice.WebService;
import net.sf.jaceko.mock.model.webservice.WebServices;
import net.sf.jaceko.mock.service.MockConfigurationHolder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public class ServicesResourceTest {
	private ServicesResource resource = new ServicesResource();

	@Mock
	private MockConfigurationHolder mockConfigurationService;
	
	@Before
	public void before() {
		initMocks(this);
		resource.setMockConfigurationService(mockConfigurationService);
	}
	
	@Test
	public void shouldReturnAllWebServices() {
		WebService service1 = new WebService("ticketing", "<dummyWSDL/>");
		WebService service2 = new WebService("mptu", "<dummyWSDL2/>");
		
		Collection<WebService> servicesCollection = asList(service1, service2);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
		assertThat(resource.getWebServices(), is(new WebServices(servicesCollection)));
	}
	
	@Test
	public void shouldReturnAllWebServices2() {
		WebService service1 = new WebService("billpayment", "<dummyWSDL/>");
		
		Collection<WebService> servicesCollection = asList(service1);
		when(mockConfigurationService.getWebServices()).thenReturn(servicesCollection);
		assertThat(resource.getWebServices(), is(new WebServices(servicesCollection)));
		
	}


}
