package org.football.league;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.football.club.Club;
import org.football.country.Country;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "LEAGUE")
public class League implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LEAGUE_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID",referencedColumnName = "COUNTRY_ID")
    private Country country;

    @Column(name = "CLUB_NUMBER")
    private short clubNumber;

    @Column(name = "PROMOTION_NUMBER")
    private short promotionNumber;

    @Column(name = "RELEGATION_NUMBER")
    private short relegationNumber;

    @Column(name = "PLAYOFF_PROMOTION_NUMBER")
    private short playoffPromotionNumber;

    @Column(name = "PLAYOFF_RELEGATION_NUMBER")
    private short playoffRelegationNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "LEAGUE_RELATION",
        joinColumns = @JoinColumn(name = "HIGHER_LEAGUE_ID"),
        inverseJoinColumns = @JoinColumn(name = "LOWER_LEAGUE_ID"))
    private Set<League> lowerLeagues;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "LEAGUE_RELATION",
            joinColumns = @JoinColumn(name = "LOWER_LEAGUE_ID"),
            inverseJoinColumns = @JoinColumn(name = "HIGHER_LEAGUE_ID"))
    private Set<League> higherLeagues;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy="league")
//    private List<Club> clubs;

}
