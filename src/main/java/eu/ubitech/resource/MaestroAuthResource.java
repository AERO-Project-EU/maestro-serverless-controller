package eu.ubitech.resource;

import eu.ubitech.constants.Constants;
import eu.ubitech.service.MaestroAuthService;
import eu.ubitech.transfer.MaestroAuthDto;
import eu.ubitech.transfer.MaestroAuthTokenDto;
import eu.ubitech.utils.GenericMessageDto;
import io.quarkus.security.UnauthorizedException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Log
@Path(Constants.REST_API + "/maestro")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MaestroAuthResource {


    @Inject
    MaestroAuthService maestroAuthService;


    // Authenticate with Maestro
    // TODO remove Request Body
    @POST
    @Path("/auth")
    @Operation(summary = "Authenticate with maestro", description = "Request an access token to maestro service")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successful Authentication",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = MaestroAuthTokenDto.class)
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
    public Response maestroAuthenticate(@RequestBody MaestroAuthDto maestroAuthDto, @Context UriInfo uriInfo) {

        try {
            String authToken = maestroAuthService.maestroAuthenticate();
            return Response.ok().entity(new MaestroAuthTokenDto(authToken)).build();
        } catch (ForbiddenException fe) {
            log.warning(fe.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity(new GenericMessageDto(fe.getMessage())).build();
        } catch (UnauthorizedException ue) {
            log.warning(ue.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(new GenericMessageDto(ue.getMessage())).build();
        } catch (Exception ex) {
            log.severe(ex.getMessage());
            return Response.serverError().entity(new GenericMessageDto(ex.getMessage())).build();
        }
    }

}
