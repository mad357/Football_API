package org.football.league;

import lombok.*;
import org.football.util.validationgroups.Any;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LeagueShortDto implements Serializable {

    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "League cannot contain id", groups = Create.class)
    private Long id;

    @NotNull(message = "Name cannot be null", groups =  Any.class)
    private String name;

}