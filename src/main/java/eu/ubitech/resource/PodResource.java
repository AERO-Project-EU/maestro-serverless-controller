package eu.ubitech.resource;

import eu.ubitech.constants.Constants;
import eu.ubitech.service.kubernetes.PodService;
import eu.ubitech.utils.GenericMessageDto;
import eu.ubitech.utils.KubernetesUtil;

import io.fabric8.kubernetes.client.Config;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Log
@Path(Constants.REST_API + "/kubernetes/pods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PodResource {

    @ConfigProperty(name = "k8s.aero.masterUrl")
    String masterUrl;

    @ConfigProperty(name = "k8s.aero.caData")
    String caData;

    @ConfigProperty(name = "k8s.aero.ccData")
    String ccData;

    @ConfigProperty(name = "k8s.aero.ckData")
    String ckData;

    @Inject
    PodService podService;

    @GET
    @Path("/{namespace}")
    public List<String> getPodsInNamespace(@PathParam("namespace") String namespace) {
        Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
        return podService.getPodsInNamespace(namespace, aeroConfig);
    }

    @POST
    @Path("/{podName}")
    public Response createPodInNamespace(@PathParam("podName") String podName, @QueryParam("namespace") String namespace) {
        Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
        Boolean status = podService.createPodInNamespace(podName, namespace, aeroConfig);
        return Response.ok().entity(new GenericMessageDto("Pod created: " + podName)).build();
    }
}
