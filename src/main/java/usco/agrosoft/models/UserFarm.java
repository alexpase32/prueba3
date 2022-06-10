package usco.agrosoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_farm", schema = "public")
public class UserFarm {

    @Id
    @Getter
    @Setter
    @Column(name = "id_user_farm")
    private String idUserFarm;

    @Getter
    @Setter
    @Column(name = "id_user")
    private String idUser;

    @Getter
    @Setter
    @Column(name = "id_role")
    private String idRole;

    @Getter
    @Setter
    @Column(name = "id_farm")
    private String idFarm;

    @Getter
    @Setter
    @Column(name = "is_active_user_farm", columnDefinition = "boolean default true")
    private boolean isActiveUserFarm;
}
