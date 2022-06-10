package usco.agrosoft.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "role", schema = "public")
public class Role {
    
    @Id
    @Getter
    @Setter
    @Column(name = "id_role")
    private String idRole;

    @Getter
    @Setter
    @Column(name = "name_role")
    private String nameRole;

    @Getter
    @Setter
    @Column(name = "description_role")
    private String descriptionRole;

    @Getter
    @Setter
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Getter
    @Setter
    @Column(name = "is_active_role", columnDefinition = "boolean default true")
    private boolean isActiveRole;
}
