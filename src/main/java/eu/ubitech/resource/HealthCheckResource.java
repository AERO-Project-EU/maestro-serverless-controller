package eu.ubitech.resource;

import eu.ubitech.constants.Constants;
import eu.ubitech.utils.GenericMessageDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Log
@Path(Constants.REST_API + "/healthcheck")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

    @POST
    @Operation(summary = "Controller healthcheck Endpoint", description = "Check that Controller is running")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Request successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)
            ),
            @APIResponse(responseCode = "401", description = "Request Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)
            ),
            @APIResponse(responseCode = "403", description = "Request Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)
            ),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON)
            )
    })
    public Response hello() {
        try {
            String response = "Hello from knative-serverless-controller";
            return Response.ok().entity(new GenericMessageDto(response)).build();
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            return Response.serverError().entity(new GenericMessageDto(ex.getMessage())).build();
        }
    }

}
