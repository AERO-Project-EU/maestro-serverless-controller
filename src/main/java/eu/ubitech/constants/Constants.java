package eu.ubitech.constants;

import java.io.Serializable;

public final class Constants implements Serializable {

    // AERO REST API
    public static final String REST_API = "/aero/api/v1";

    // AUTH TOKEN FIELD KEY
    public static String AUTH_TOKEN_COOKIE = "auth_token";

    // AUTH TOKEN FIELD VALUE EXTRACTION
    public static String AUTH_TOKEN_REGEX = "(?<=auth_token=).*?(?=;)";

}