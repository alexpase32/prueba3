package usco.agrosoft.models;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sub_role", schema = "public")
public class SubRole {

    @Id
    @Getter
    @Setter
    @Column(name = "id_sub_role")
    private String idSubRole;

    @Getter
    @Setter
    @Column(name = "id_farm")
    private String idFarm;

    @Getter
    @Setter
    @Column(name = "name_sub_role")
    private String nameSubRole;

    @Getter
    @Setter
    @Column(name = "description_sub_role")
    private String descriptionSubRole;

    @Getter
    @Setter
    @Column(name = "created_sub_role")
    private LocalDateTime createdSubRole;

    @Getter
    @Setter
    @Column(name = "is_active_sub_role", columnDefinition = "boolean default true")
    private boolean isActiveSubRole;

}
