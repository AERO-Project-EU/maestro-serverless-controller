package eu.ubitech.service.knative;

import eu.ubitech.utils.GenericMessageDto;
import io.fabric8.knative.client.DefaultKnativeClient;
import io.fabric8.knative.client.KnativeClient;
import io.fabric8.knative.serving.v1.Service;
import io.fabric8.knative.serving.v1.ServiceBuilder;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;

import java.util.Collections;
import java.util.List;

@Log
@ApplicationScoped
public class KnativeService {


    public Response createKnativeService(String serviceName, Config config) {
        try (final KnativeClient knativeClient = new DefaultKnativeClient(config)) {
            Service knativeService = new ServiceBuilder()
                    .withNewMetadata()
                    .withName(serviceName)
                    .endMetadata()
                    .withNewSpec()
                    .withNewTemplate()
                    .withNewSpec()
                    .addNewContainer()
                    .withImage("gcr.io/knative-samples/helloworld-go")
                    .addNewPort()
                    .withContainerPort(8080)
                    .endPort()
                    .addNewEnv()
                    .withName("TARGET")
                    .withValue("Go Sample v1")
                    .endEnv()
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();

            // Get the Knative service name
            String knativeServiceName = knativeService.getMetadata().getName();
//            String namespace = knativeService.getMetadata().getNamespace();
            String namespace = "aero";
            knativeClient.services().inNamespace("aero").create(knativeService);

            Service knativeService2 = new ServiceBuilder()
                    .withNewMetadata()
                    .withName("hello2")
                    .endMetadata()
                    .withNewSpec()
                    .withNewTemplate()
                    .withNewSpec()
                    .addNewContainer()
                    .withImage("gcr.io/knative-samples/helloworld-go")
                    .addNewPort()
                    .withContainerPort(8080)
                    .endPort()
                    .addNewEnv()
                    .withName("TARGET")
                    .withValue("Go Sample v1 from Service 2")
                    .endEnv()
                    // Add environment variable to point to Service 1 URL
                    .addNewEnv()
                    .withName("SERVICE1_URL")
                    .withValue("http://" + serviceName + ".default.svc.cluster.local")
                    .endEnv()
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();
            knativeClient.services().inNamespace("aero").create(knativeService2);
            // Create NodePort Service
            ServicePort servicePort = new ServicePortBuilder()
                    .withProtocol("TCP")
                    .withPort(80)
                    .withTargetPort(new IntOrString(8080)) // Match the container port
                    .withNodePort(30080) // Choose a specific NodePort
                    .build();

            ServiceSpec serviceSpec = new ServiceSpecBuilder()
                    .withSelector(Collections.singletonMap("serving.knative.dev/service", knativeServiceName))
                    .withPorts(servicePort)
                    .withType("NodePort")
                    .build();

            io.fabric8.kubernetes.api.model.Service nodePortService = new io.fabric8.kubernetes.api.model.ServiceBuilder()
                    .withNewMetadata()
                    .withName("knative-nodeport-service")
                    .withNamespace(namespace)
                    .endMetadata()
                    .withSpec(serviceSpec)
                    .build();

            KubernetesClient client = new KubernetesClientBuilder().withConfig(config).build();

            client.services().inNamespace(namespace).createOrReplace(nodePortService);


            return Response.ok().entity(new GenericMessageDto("Knative Service created: " + serviceName)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating Knative service: " + e.getMessage())
                    .build();
        }
    }


    public boolean deleteKnativeService(String serviceName, Config config) {
        try (final KnativeClient knativeClient = new DefaultKnativeClient(config)) {
            List<StatusDetails> deleteNsDetails = knativeClient.services().inNamespace("aero").withName(serviceName).delete();
            return  Boolean.TRUE;
        } catch (Exception e) {
            log.severe("Delete Namespace error: {}" + e.getMessage());
            return Boolean.FALSE;
        }
    }

}
