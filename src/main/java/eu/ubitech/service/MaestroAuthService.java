package eu.ubitech.service;

import eu.ubitech.client.MaestroRestClient;
import eu.ubitech.constants.Constants;
import eu.ubitech.transfer.MaestroAuthDto;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class MaestroAuthService {

    @Inject
    @RestClient
    MaestroRestClient maestroRestClient;

    @Inject
    JsonWebToken jwt;

    public String maestroAuthenticate() throws Exception {

        String maestroAdminUsername = jwt.containsClaim("maestro_username") ?
                jwt.getClaim("maestro_username") : null;
        String maestroAdminPassword = jwt.containsClaim("maestro_password") ?
                jwt.getClaim("maestro_password") : null;

        MaestroAuthDto maestroAuthDto = new MaestroAuthDto(maestroAdminUsername, maestroAdminPassword);

        String authToken;
        Pattern authTokenPattern = Pattern.compile(Constants.AUTH_TOKEN_REGEX);
        String authTokenCookie = maestroRestClient.maestroAuthenticate(maestroAuthDto)
                .getCookies()
                .get(Constants.AUTH_TOKEN_COOKIE).toString();
        Matcher m = authTokenPattern.matcher(authTokenCookie);

        if (m.find()) {
            authToken = m.group();
        } else {
            throw new Exception("Authentication Token missing from cookie");
        }
        return authToken;
    }

    public String maestroAuthenticateWithCredentials(String username, String password) throws Exception {

        String authToken;
        Pattern authTokenPattern = Pattern.compile(Constants.AUTH_TOKEN_REGEX);
        MaestroAuthDto maestroAuthDto = new MaestroAuthDto(username, password);
        String authTokenCookie = maestroRestClient.maestroAuthenticate(maestroAuthDto)
                .getCookies()
                .get(Constants.AUTH_TOKEN_COOKIE).toString();
        Matcher m = authTokenPattern.matcher(authTokenCookie);

        if (m.find()) {
            authToken = m.group();
        } else {
            throw new Exception("Authentication Token missing from cookie");
        }
        return authToken;

    }

}
