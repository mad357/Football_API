package org.league;

import org.util.validationgroups.Any;
import org.util.validationgroups.Create;
import org.util.validationgroups.Update;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Entity
@Table(name = "LEAGUE")
public class League implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LEAGUE_ID")
    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "League cannot contain id", groups = Create.class)
    private Long id;

    @NotNull(message = "Name cannot be null", groups =  Any.class)
    @Column(name = "NAME")
    private String name;

    @NotNull(message = "Country cannot be null", groups =  Any.class)
    @Column(name = "COUNTRY")
    private String country;

    @JsonbTransient
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABOVE_LEAGUE_ID")
    private League aboveLeague;

    @JsonbTransient
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BELOW_LEAGUE_ID")
    private League belowLeague;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public League getAboveLeague() {
        return aboveLeague;
    }

    public void setAboveLeague(League aboveLeague) {
        this.aboveLeague = aboveLeague;
    }

    public League getBelowLeague() {
        return belowLeague;
    }

    public void setBelowLeague(League belowLeague) {
        this.belowLeague = belowLeague;
    }
}
