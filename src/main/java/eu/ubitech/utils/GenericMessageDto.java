package eu.ubitech.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GenericMessageDto implements Serializable {

    private String message;

    public static final String RESPONSE_MESSAGE = "Fill a proper response";
    public static final String DEPLOYMENT_REQUEST_SUCCESSFUL = "Successfully proceeded to deployment";
    public static final String UNDEPLOYMENT_REQUEST_SUCCESSFUL = "Successfully proceeded to undeployment";

}