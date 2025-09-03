package eu.ubitech.resource;

import eu.ubitech.constants.Constants;
import eu.ubitech.service.ServerlessDeploymentService;
import eu.ubitech.utils.GenericMessageDto;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.logging.Level;

@Log
@Path(Constants.REST_API + "/serverless")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServerlessDeploymentResource {

    @Inject
    ServerlessDeploymentService serverlessDeploymentService;


    @POST
    @Path("/applicationinstance/{applicationInstanceID}/request/deployment")
    @Operation(summary = "Request Serverless Application Deployment", description = "Request Deployment to Knative for the application with id applicationInstanceID")
    @Parameter(name = "applicationInstanceID", description = "The applicationInstanceID value of the application to be deployed to knative", required = true)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully proceeded to deployment",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Request Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "403", description = "Request Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            )
    })
    public Response requestDeployment(@PathParam("applicationInstanceID") Long applicationInstanceID, @CookieParam("auth_token") String authToken) {
        try {
            Response res = serverlessDeploymentService.requestDeployment(applicationInstanceID, authToken);
            return Response.ok().entity(new GenericMessageDto(GenericMessageDto.DEPLOYMENT_REQUEST_SUCCESSFUL)).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            return Response.serverError().entity(new GenericMessageDto(e.getMessage())).build();
        }
    }

    @POST
    @Path("/applicationinstance/{applicationInstanceID}/request/undeployment")
    @Operation(summary = "Request Serverless Application Undeployment", description = "Request Undeployment to Knative for the application with id applicationInstanceID")
    @Parameter(name = "applicationInstanceID", description = "The applicationInstanceID value of the application to be undeployed from knative", required = true)
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully proceeded to undeployment",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Request Forbidden",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "403", description = "Request Unauthorized",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            ),
            @APIResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = GenericMessageDto.class)
                    )
            )
    })
    public Response requestUndeployment(@PathParam("applicationInstanceID") Long applicationInstanceID, @CookieParam("auth_token") String authToken) {
        try {
            Response res = serverlessDeploymentService.requestUndeployment(applicationInstanceID, authToken);
            return Response.ok().entity(new GenericMessageDto(GenericMessageDto.UNDEPLOYMENT_REQUEST_SUCCESSFUL)).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            return Response.serverError().entity(new GenericMessageDto(e.getMessage())).build();
        }
    }

    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDummyMaestroStatus() {
        try {
            String response = serverlessDeploymentService.getDummyMaestroStatus();
            return Response.ok(response).build();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            return Response.serverError().entity("Error occurred.").build();
        }

    }

}
