package org.football.league;

import lombok.*;
import org.football.club.ClubDto;
import org.football.util.validationgroups.Any;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeagueDto implements Serializable {

    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "League cannot contain id", groups = Create.class)
    private Long id;

    @NotNull(message = "Name cannot be null", groups =  Any.class)//
    private String name;

    @NotNull(message = "CountryId cannot be null", groups = Any.class)
    private Long countryId;

    private String countryName;

    @NotNull(message = "Club number cannot be null", groups = Any.class)
    @Min(value = 2, message = "Club number must be greater that 1", groups = Any.class)
    private short clubNumber;

    @NotNull(message = "Promotion number cannot be null", groups = Any.class)
    private short promotionNumber;

    @NotNull(message = "Relegation number cannot be null", groups = Any.class)
    private short relegationNumber;

    @NotNull(message = "Playoff promotion number cannot be null", groups = Any.class)
    private short playoffPromotionNumber;

    @NotNull(message = "Playoff relegation number cannot be null", groups = Any.class)
    private short playoffRelegationNumber;

    private List<ClubDto> clubs;

    private Set<LeagueShortDto> lowerLeagues;

    private Set<LeagueShortDto> higherLeagues;

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
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

    public Set<LeagueShortDto> getLowerLeagues() {
        return lowerLeagues;
    }

    public void setLowerLeagues(Set<LeagueShortDto> lowerLeagues) {
        this.lowerLeagues = lowerLeagues;
    }

    public Set<LeagueShortDto> getHigherLeagues() {
        return higherLeagues;
    }

    public void setHigherLeagues(Set<LeagueShortDto> higherLeagues) {
        this.higherLeagues = higherLeagues;
    }

    public List<ClubDto> getClubs() {
        return clubs;
    }

    public void setClubs(List<ClubDto> clubs) {
        this.clubs = clubs;
    }

}