package net.sf.jaceko.mock.resource;

import net.sf.jaceko.mock.application.enums.HttpMethod;
import net.sf.jaceko.mock.model.request.MockResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/services/REST/{serviceName}")
public class RestServiceResourceWithoutEndPoint extends RestEndpointResource{
}
