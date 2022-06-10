package usco.agrosoft.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "request", schema = "public")
public class Request {
    @Id
    @Getter
    @Setter
    @Column(name = "id_request")
    private String idRequest;

    @Getter
    @Setter
    @Column(name = "id_user_emmiter")
    private String idUserEmmiter;

    @Getter
    @Setter
    @Column(name = "id_farm")
    private String idFarm;

    @Getter
    @Setter
    @Column(name = "id_user_receiver")
    private String idUserReceiver;

    @Getter
    @Setter
    @Column(name = "id_state")
    private String idState;

    @Getter
    @Setter
    @Column(name = "id_role")
    private String idRole;

    @Getter
    @Setter
    @Column(name = "is_admin")
    private boolean isAdmin;

    @Getter
    @Setter
    @Column(name = "is_active_request", columnDefinition = "boolean default true")
    private boolean isActiveRequest;

    @Getter
    @Setter
    @Column(name = "created_date")
    private LocalDateTime createdDate;
}
