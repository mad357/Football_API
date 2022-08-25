package org.league;

import org.country.Country;
import org.util.validationgroups.Any;
import org.util.validationgroups.Create;
import org.util.validationgroups.Update;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//@JsonbPropertyOrder( {"id", "name", "country", "aboveLeague"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Country cannot be null", groups =  Any.class)
    @JoinColumn(name = "COUNTRY_ID",referencedColumnName = "COUNTRY_ID")
    private Country country;

    @NotNull(message = "Club number cannot be null", groups =  Any.class)
    @Column(name = "CLUB_NUMBER")
    private short clubNumber;

    @NotNull(message = "Promotion number cannot be null", groups =  Any.class)
    @Column(name = "PROMOTION_NUMBER")
    private short promotionNumber;

    @NotNull(message = "Relegation number cannot be null", groups =  Any.class)
    @Column(name = "RELEGATION_NUMBER")
    private short relegationNumber;

    @NotNull(message = "Playoff promotion number cannot be null", groups =  Any.class)
    @Column(name = "PLAYOFF_PROMOTION_NUMBER")
    private short playoffPromotionNumber;

    @NotNull(message = "Playoff relegation number cannot be null", groups =  Any.class)
    @Column(name = "PLAYOFF_RELEGATION_NUMBER")
    private short playoffRelegationNumber;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(name="LEAGUE_RELATION",
            joinColumns={@JoinColumn(name="HIGHER_LEAGUE_ID")},
            inverseJoinColumns={@JoinColumn(name="LOWER_LEAGUE_ID")})
    private Set<League> lowerLeagueIds = new HashSet<>();

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(name="LEAGUE_RELATION",
            joinColumns={@JoinColumn(name="LOWER_LEAGUE_ID")},
            inverseJoinColumns={@JoinColumn(name="HIGHER_LEAGUE_ID")})
    private Set<League> higherLeagueIds = new HashSet<>();

    public Set<Long> getLowerLeagueIds() {
        return lowerLeagueIds.stream().map(League :: getId).collect(Collectors.toSet());
    }

    public void setLowerLeagueIds(Set<Long> lowerLeagueIds) {
        this.lowerLeagueIds = new HashSet<>();
        if (lowerLeagueIds != null) {
            for (Long id : lowerLeagueIds) {
                this.lowerLeagueIds.add(new League(){{setId(id);}});
            }
        }
    }

    public Set<Long> getHigherLeagueIds() {
        return higherLeagueIds.stream().map(League :: getId).collect(Collectors.toSet());
    }

    public void setHigherLeagueIds(Set<League> higherLeagueIds) {
        this.higherLeagueIds = higherLeagueIds;
    }

    public short getClubNumber() {
        return clubNumber;
    }

    public void setClubNumber(short clubNumber) {
        this.clubNumber = clubNumber;
    }

    public short getPromotionNumber() {
        return promotionNumber;
    }

    public void setPromotionNumber(short promotionNumber) {
        this.promotionNumber = promotionNumber;
    }

    public short getRelegationNumber() {
        return relegationNumber;
    }

    public void setRelegationNumber(short relegationNumber) {
        this.relegationNumber = relegationNumber;
    }

    public short getPlayoffPromotionNumber() {
        return playoffPromotionNumber;
    }

    public void setPlayoffPromotionNumber(short playoffPromotionNumber) {
        this.playoffPromotionNumber = playoffPromotionNumber;
    }

    public short getPlayoffRelegationNumber() {
        return playoffRelegationNumber;
    }

    public void setPlayoffRelegationNumber(short playoffRelegationNumber) {
        this.playoffRelegationNumber = playoffRelegationNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
