package net.sf.jaceko.mock.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import net.sf.jaceko.mock.matcher.OperationHavingNameEqualTo;
import net.sf.jaceko.mock.model.webservice.WebserviceOperation;
import net.sf.jaceko.mock.util.FileReader;

import org.junit.Test;

public class WsdlProcessorTest {

	private WsdlProcessor wsdlProcessor = new WsdlProcessor();

	private FileReader fileReader = new FileReader();

	@Test
	public void shouldFetchOperationsFromWsdlFile_DocumetStyle() {

		List<WebserviceOperation> operationsFromWsdl = wsdlProcessor.getOperationsFromWsdl("dataserviceJRN.wsdl",
				fileReader.readFileContents("dataserviceJRN.wsdl"));
		assertThat(operationsFromWsdl.size(), is(4));
		assertThat(operationsFromWsdl, hasItem(new OperationHavingNameEqualTo("extendWindowRequestElement")));
		assertThat(operationsFromWsdl, hasItem(new OperationHavingNameEqualTo("lockRequestElement")));
		assertThat(operationsFromWsdl, hasItem(new OperationHavingNameEqualTo("unlockRequestElement")));
		assertThat(operationsFromWsdl, hasItem(new OperationHavingNameEqualTo("purgeRequestElement")));

	}
	
	@Test
	public void shouldFetchOperationsFromWsdlFile_RPCStyle() {
		List<WebserviceOperation> operationsFromWsdl = wsdlProcessor.getOperationsFromWsdl("hello-for-unit-tests.wsdl",
				fileReader.readFileContents("hello-for-unit-tests.wsdl"));
		assertThat(operationsFromWsdl.size(), is(1));
		assertThat(operationsFromWsdl, hasItem(new OperationHavingNameEqualTo("sayHello")));
		
	}


}
