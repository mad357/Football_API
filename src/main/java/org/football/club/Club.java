package org.football.club;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.football.league.League;
import org.football.country.Country;

import javax.persistence.*;

@Entity
@Table(name = "CLUB")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLUB_ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "FULLNAME")
    private String fullname;

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

}