package eu.ubitech.resource;


import eu.ubitech.constants.Constants;
import eu.ubitech.service.knative.KnativeService;
import eu.ubitech.utils.GenericMessageDto;
import eu.ubitech.utils.KubernetesUtil;
import io.fabric8.kubernetes.client.Config;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Log
@Path(Constants.REST_API + "/knative")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class KnativeResource {
    @ConfigProperty(name = "k8s.aero.masterUrl")
    String masterUrl;

    @ConfigProperty(name = "k8s.aero.caData")
    String caData;

    @ConfigProperty(name = "k8s.aero.ccData")
    String ccData;

    @ConfigProperty(name = "k8s.aero.ckData")
    String ckData;

    @Inject
    KnativeService knativeService;

    @POST
    @Path("/createService/{serviceName}")
    public Response createKnativeService(@PathParam("serviceName") String serviceName) {
        Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
        return knativeService.createKnativeService(serviceName, aeroConfig);
    }

    @DELETE
    @Path("/createService/{serviceName}")
    public Response deleteKnativeService(@PathParam("serviceName") String serviceName) {
        Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
        Boolean status = knativeService.deleteKnativeService(serviceName, aeroConfig);
        return Response.ok().entity(new GenericMessageDto("Knative service " + serviceName + " deleted.")).build();
    }
}

