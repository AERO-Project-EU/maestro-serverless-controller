package eu.ubitech.resource;

import eu.ubitech.constants.Constants;
import eu.ubitech.service.kubernetes.NamespaceService;
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
@Path(Constants.REST_API + "/kubernetes/namespaces")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NamespaceResource {

        @ConfigProperty(name = "k8s.aero.masterUrl")
        String masterUrl;

        @ConfigProperty(name = "k8s.aero.caData")
        String caData;

        @ConfigProperty(name = "k8s.aero.ccData")
        String ccData;

        @ConfigProperty(name = "k8s.aero.ckData")
        String ckData;

        @Inject
        NamespaceService namespaceService;

        @GET
        public List<String> getNamespaces() {
                Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
                return namespaceService.getNamespaces(aeroConfig);
        }

        @POST
        @Path("/{namespace}")
        public Response createNamespace(@PathParam("namespace") String namespace) {
                Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
                Boolean status = namespaceService.createNamespace(namespace, aeroConfig);
                return Response.ok().entity(new GenericMessageDto("Created namespace: " + namespace)).build();
        }

        @DELETE
        @Path("/{namespace}")
        public Response deleteNamespace(@PathParam("namespace") String namespace) {
                Config aeroConfig = KubernetesUtil.configConnection(masterUrl, caData, ccData, ckData);
                Boolean status = namespaceService.deleteNamespace(namespace, aeroConfig);
                return Response.ok().entity(new GenericMessageDto("Deleted namespace: " + namespace)).build();
        }

}
