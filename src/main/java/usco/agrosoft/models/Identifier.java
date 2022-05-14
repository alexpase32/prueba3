package usco.agrosoft.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "identifier", schema = "public")
public class Identifier {
    @Id
    @Getter
    @Setter
    @Column(name = "id_identifier")
    private int idIdentifier;

    @Getter
    @Setter
    @Column(name = "country_name")
    private String countryName;

    @Getter
    @Setter
    @Column(name = "identifier")
    private String identifier;

}
