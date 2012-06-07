package net.sf.jaceko.mock.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.jaceko.mock.exception.ClientFaultException;
import net.sf.jaceko.mock.helper.XmlParser;
import net.sf.jaceko.mock.service.WebserviceMockSvcLayer;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


@Path("/endpoint/soap/{serviceName}")
public class SoapEndpointResource {
	private static final Logger LOG = Logger.getLogger(SoapEndpointResource.class);

	private static final String BODY = "Body";
	private static final String ENVELOPE = "Envelope";
	private static final String INVALID_SOAP_REQUEST = "Invalid SOAP request";
	private static final String MALFORMED_XML = "Malformed Xml";
	private WebserviceMockSvcLayer service;

	@POST
	@Consumes(MediaType.TEXT_XML)
	@Produces(MediaType.TEXT_XML)
	public String performRequest(@PathParam("serviceName") String serviceName, String request) {
		LOG.debug("serviceName: " + serviceName + ", request:" + request);
		String requestMessgageName = extractRequestMessageName(request);
		String response = service.performRequest(serviceName, requestMessgageName, request, null);
		LOG.debug("serviceName: " + serviceName + ", response:" + response);
		return response;
	}

	private String extractRequestMessageName(String request) {
		Document reqDocument = null;
		try {
			reqDocument = XmlParser.parse(request, true);
		} catch (Exception e) {
			throw new ClientFaultException(MALFORMED_XML, e);
		}
		reqDocument.normalize();
		Node envelope = getChildElement(reqDocument, ENVELOPE);
		Node body = getChildElement(envelope, BODY);
		Node requestMessage = getChildElement(body);

		if (requestMessage == null) {
			throw new ClientFaultException(INVALID_SOAP_REQUEST);
		}
		return requestMessage.getLocalName();

	}

	private Node getChildElement(Node parent) {
		NodeList childNodes = parent.getChildNodes();

		Node foundNode = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				foundNode = childNode;
				break;
			}
		}
		return foundNode;
	}

	private Node getChildElement(Node parent, String elementName) {
		NodeList childNodes = parent.getChildNodes();

		Node foundNode = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (elementName.equals(childNode.getLocalName())) {
				foundNode = childNode;
				break;
			}
		}
		if (foundNode == null) {
			throw new ClientFaultException(INVALID_SOAP_REQUEST);
		}
		return foundNode;
	}

	public void setWebserviceMockService(WebserviceMockSvcLayer service) {
		this.service = service;
	}

}