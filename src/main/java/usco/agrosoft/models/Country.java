package usco.agrosoft.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "country", schema = "public")
public class Country {

    @Id
    @Getter
    @Setter
    @Column(name = "id_country")
    private String idCountry;

    @Getter
    @Setter
    @Column(name = "name_country")
    private String nameCountry;    
}
