package org.football.club;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.football.country.CountryDto;
import org.football.league.LeagueDto;
import org.football.league.LeagueShortDto;
import org.football.util.validationgroups.Any;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClubDto {

    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "Country cannot contain id", groups = Create.class)
    private Long id;

    @Size(max = 50, message = "name size exceeded")
    @NotNull(message = "Name cannot be null", groups =  Any.class)
    private String name;

    @Size(max = 100, message = "full name size exceeded")
    @NotNull(message = "Fullname cannot be null", groups =  Any.class)
    private String fullname;

    @Size(max = 50, message = "alias size exceeded")
    private String alias;

    private Short yearFound;

    private LeagueDto league;

    private LeagueShortDto leagueShort;

    private CountryDto country;

}