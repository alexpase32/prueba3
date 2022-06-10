package usco.agrosoft.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class ShowRequest {
    @Getter
    @Setter
    private String idRequest;

    @Getter
    @Setter
    private String stateRequest;

    @Getter
    @Setter
    private String typeRequest;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String idFarm;

    @Getter
    @Setter
    private String nameFarm;

    @Getter
    @Setter
    private String descriptionFarm;

    @Getter
    @Setter
    private LocalDateTime createdDate;
}
