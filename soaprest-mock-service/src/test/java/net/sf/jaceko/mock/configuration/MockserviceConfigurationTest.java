package net.sf.jaceko.mock.configuration;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import net.sf.jaceko.mock.configuration.MockserviceConfiguration;
import net.sf.jaceko.mock.configuration.WebService;
import net.sf.jaceko.mock.configuration.WebserviceOperation;
import net.sf.jaceko.mock.exception.ServiceNotConfiguredException;

import org.junit.Test;


public class MockserviceConfigurationTest {
	private MockserviceConfiguration configuration = new MockserviceConfiguration();

	@Test
	public void shouldReturnServices() {
		String name1 = "ticketing";
		String wsdlText1 = "<dummyWSDL/>";
		String name2 = "mptu";
		String wsdlText2 = "<dummyWSDL2/>";

		WebService service1 = new WebService(name1, wsdlText1);
		WebService service2 = new WebService(name2, wsdlText2);

		configuration.setSoapServices(asList(service1, service2));
		
		assertThat(configuration.getSoapServices(), hasItems(service1, service2));
	}

	@Test
	public void shouldReturnServiceByName() {
		String name1 = "ticketing";
		String wsdlText1 = "<dummyWSDL/>";
		String name2 = "mptu";
		String wsdlText2 = "<dummyWSDL2/>";

		WebService service1 = new WebService(name1, wsdlText1);
		WebService service2 = new WebService(name2, wsdlText2);

		configuration.setSoapServices(asList(service1, service2));

		assertThat(configuration.getWebService(name2), is(service2));
		assertThat(configuration.getWebService(name1), is(service1));

	}

	@Test
	public void shouldReturnOperationByServiceNameAndRequestName() {
		String name1 = "ticketing";
		String wsdlText1 = "<dummyWSDL/>";
		String name2 = "mptu";
		String wsdlText2 = "<dummyWSDL2/>";

		WebService service1 = new WebService(name1, wsdlText1);
		WebService service2 = new WebService(name2, wsdlText2);

		String inputMessageName1 = "reserveRequest";
		String inputMessageName2 = "confirmRequest";
		WebserviceOperation operation1 = new WebserviceOperation(inputMessageName1, null, null, 0);
		WebserviceOperation operation2 = new WebserviceOperation(inputMessageName2, null, null, 0);
		
		String inputMessageName3 = "prepayRequest";
		WebserviceOperation operation3 = new WebserviceOperation(inputMessageName3, null, null, 0);
		service1.addOperation(0, operation1);
		service1.addOperation(1, operation2);
		service2.addOperation(0, operation3);
		
		configuration.setSoapServices(asList(service1, service2));
		
		assertThat(configuration.getWebServiceOperation(name1, inputMessageName1), is(operation1));
		assertThat(configuration.getWebServiceOperation(name1, inputMessageName2), is(operation2));
		assertThat(configuration.getWebServiceOperation(name2, inputMessageName3), is(operation3));

	}
	
	@Test(expected=ServiceNotConfiguredException.class)
	public void shouldThrowExceptionIfServiceNotFound() {
		configuration.getWebServiceOperation("not_existing", "abc");
	}

}
