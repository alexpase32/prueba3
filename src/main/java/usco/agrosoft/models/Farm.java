package usco.agrosoft.models;

import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.*;


@Entity
@Table(name = "farm", schema = "public")
public class Farm {

    @Id
    @Getter
    @Setter
    @Column(name = "id_farm")
    private String idFarm;

    @Getter
    @Setter
    @Column(name = "id_country")
    private String idCountry;

    @Getter
    @Setter
    @Column(name = "name_farm")
    private String nameFarm;

    @Getter
    @Setter
    @Column(name = "description_farm")
    private String descriptionFarm;

    @Getter
    @Setter
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Getter
    @Setter
    @Column(name = "is_active_farm")
    private boolean isActiveFarm;

}