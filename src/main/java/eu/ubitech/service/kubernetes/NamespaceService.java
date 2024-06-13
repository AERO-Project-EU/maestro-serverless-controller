package eu.ubitech.service.kubernetes;


import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;

import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
public class NamespaceService {

    public List<String> getNamespaces(Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            NamespaceList namespaceList = client.namespaces().list();
            return namespaceList.getItems().stream()
                    .map(namespace -> namespace.getMetadata().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.severe("Get Namespaces error: {} " + e.getMessage());
            return null;
        }
    }

    public boolean createNamespace(String name, Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            Namespace ns = new NamespaceBuilder().withNewMetadata().withName(name).endMetadata().build();
            client.namespaces().create(ns);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.severe("Create Namespace error:" + e.getMessage());
            return Boolean.FALSE;
        }

    }

    public boolean doesNamespaceExist(String name, Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            // Indirectly check whether namespace exists, as the supplied config might not have access to retrieve namespaces.
            client.pods().inNamespace(name).list();
            return true;
        } catch (Exception exception) {
            log.severe("Failed to retrieve (pods within) namespace: " + name + "." + exception);
            return false;
        }
    }

    public boolean deleteNamespace(String name, Config config) {
        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            List<StatusDetails> deleteNsDetails = client.namespaces().withName(name).delete();
            return  Boolean.TRUE;
        } catch (Exception e) {
            log.severe("Delete Namespace error: {}" + e.getMessage());
            return Boolean.FALSE;
        }
    }



}
