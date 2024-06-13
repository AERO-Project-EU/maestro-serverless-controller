package eu.ubitech.transfer;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaestroAuthDto implements Serializable {

    private String username;
    private String password;

}
