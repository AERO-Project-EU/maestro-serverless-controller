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

    public Response requestDeployment(Long applicationInstanceID, String authToken) {
        try {
            authToken = maestroAuthService.maestroAuthenticateWithCredentials("admin", "!1q2w3e!");
            return maestroRestClient.requestDeployment(authToken, applicationInstanceID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response requestUndeployment(Long applicationInstanceID, String authToken) {
        try {
            authToken = maestroAuthService.maestroAuthenticateWithCredentials("admin", "!1q2w3e!");
            return maestroRestClient.requestUndeployment(authToken, applicationInstanceID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDummyMaestroStatus() {
        try {
            return maestroRestClient.getDummyMaestroStatus();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
