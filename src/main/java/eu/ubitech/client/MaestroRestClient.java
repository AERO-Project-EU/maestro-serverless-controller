package eu.ubitech.client;

import eu.ubitech.transfer.MaestroAuthDto;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/v1")
@RegisterClientHeaders
@RegisterRestClient(configKey = "maestro-rest-api")
public interface MaestroRestClient {

    @POST
    @Path("/auth/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Response maestroAuthenticate(@RequestBody MaestroAuthDto maestroAuthDto);

    @POST
    @Path("/applicationinstance/{applicationInstanceID}/request/deployment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response requestDeployment(@CookieParam("auth_token") String authToken, @PathParam("applicationInstanceID") Long applicationInstanceID);

    @POST
    @Path("/applicationinstance/{applicationInstanceID}/request/undeployment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response requestUndeployment(@CookieParam("auth_token") String authToken, @PathParam("applicationInstanceID") Long applicationInstanceID);

    @GET
    @Path("status")
    @Produces(MediaType.TEXT_PLAIN)
    String getDummyMaestroStatus();

}