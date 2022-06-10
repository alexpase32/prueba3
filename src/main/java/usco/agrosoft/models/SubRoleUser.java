package usco.agrosoft.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sub_role_user", schema = "public")
public class SubRoleUser {

    @Id
    @Getter
    @Setter
    @Column(name = "id_sub_role_user")
    private String idSubRoleUser;

    @Getter
    @Setter
    @Column(name = "id_sub_role")
    private String idSubRole;

    @Getter
    @Setter
    @Column(name = "id_user_farm")
    private String idUserFarm;

    @Getter
    @Setter
    @Column(name = "is_active_sub_role_user")
    private boolean isActiveSubRoleUser;
}
