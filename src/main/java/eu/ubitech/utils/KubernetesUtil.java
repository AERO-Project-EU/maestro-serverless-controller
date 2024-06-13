package eu.ubitech.utils;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;

public class KubernetesUtil {

    public static Config configConnection(String masterUrl, String caCertData, String clientCertData, String clientKeyData) {
        final String keyAlgorithm = Config.getKeyAlgorithm(null, clientKeyData);

        return new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withCaCertData(caCertData)
                .withClientCertData(clientCertData)
                .withClientKeyData(clientKeyData)
                .withClientKeyAlgo(keyAlgorithm)
                .build();
    }

}
