package eu.ubitech.service.kubernetes;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;

import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class PodService {

    public List<String> getPodsInNamespace(String namespace, Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            PodList podList = client.pods().inNamespace(namespace).list();
            return podList.getItems().stream()
                    .map(pod -> pod.getMetadata().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.severe("Get Pods error: " + e.getMessage());
            return null;
        }
    }

    public Boolean createPodInNamespace(String podName, String namespace, Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            Pod pod = new PodBuilder()
                    .withNewMetadata()
                    .withName(podName)
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("nginx")
                    .withImage("nginx:latest")
                    .addNewPort()
                    .withContainerPort(80)
                    .endPort()
                    .endContainer()
                    .endSpec()
                    .build();

            client.pods().inNamespace(namespace).create(pod);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.severe("Create Pod error: " + e.getMessage());
            return Boolean.FALSE;
        }
    }

}

