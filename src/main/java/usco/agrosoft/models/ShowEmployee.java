package usco.agrosoft.models;

import lombok.Getter;
import lombok.Setter;

public class ShowEmployee {
    @Getter
    @Setter
    private String idUser;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String phoneNumber;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String idSubRole;

    @Getter
    @Setter
    private String nameSubRole;
}
