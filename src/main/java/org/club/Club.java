package org.club;

import org.country.Country;
import org.league.League;
import org.util.validationgroups.Any;
import org.util.validationgroups.Create;
import org.util.validationgroups.Update;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CLUB")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLUB_ID", nullable = false)
    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "Country cannot contain id", groups = Create.class)
    private Long id;

    @Size(max = 50, message = "name size exceeded")
    @NotNull(message = "Name cannot be null", groups =  Any.class)
    @Column(name = "NAME")
    private String name;

    @Size(max = 100, message = "full name size exceeded")
    @NotNull(message = "Fullname cannot be null", groups =  Any.class)
    @Column(name = "FULLNAME")
    private String fullname;

    @Size(max = 50, message = "alias size exceeded")
    @Column(name = "ALIAS")
    private String alias;

    @Column(name = "YEAR_FOUNDED")
    private short yearFounded;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAGUE_ID",referencedColumnName = "LEAGUE_ID")
    private League league;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "COUNTRY_ID",referencedColumnName = "COUNTRY_ID")
    private Country country;

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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public short getYearFounded() {
        return yearFounded;
    }

    public void setYearFounded(short yearFounded) {
        this.yearFounded = yearFounded;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}