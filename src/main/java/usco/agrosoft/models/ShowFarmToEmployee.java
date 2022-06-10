package usco.agrosoft.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class ShowFarmToEmployee {
    @Getter
    @Setter
    private String idFarm;

    @Getter
    @Setter
    private String idCountry;

    @Getter
    @Setter
    private String nameFarm;

    @Getter
    @Setter
    private String descriptionFarm;

    @Getter
    @Setter
    private LocalDateTime createdDate;

    @Getter
    @Setter
    private boolean isActiveFarm;

    @Getter
    @Setter
    private String nameAdmin;
}
