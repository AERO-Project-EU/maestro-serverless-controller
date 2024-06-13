package eu.ubitech.service;

import eu.ubitech.client.MaestroRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Log
@ApplicationScoped
public class ServerlessDeploymentService {

    @Inject
    @RestClient
    MaestroRestClient maestroRestClient;

    @Inject
    MaestroAuthService maestroAuthService;

    public Response requestDeployment(Long applicationInstanceID) {
        try {
            String authToken = maestroAuthService.maestroAuthenticate();
            return maestroRestClient.requestDeployment(authToken, applicationInstanceID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response requestUndeployment(Long applicationInstanceID) {
        try {
            String authToken = maestroAuthService.maestroAuthenticate();
            return maestroRestClient.requestUndeployment(authToken, applicationInstanceID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
