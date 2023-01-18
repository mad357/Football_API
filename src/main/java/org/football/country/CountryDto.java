package org.football.country;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.football.util.validationgroups.Any;
import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CountryDto implements Serializable {

    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "Country cannot contain id", groups = Create.class)
    private Long id;

    @Size(max = 50, message = "name size exceeded")
    @NotNull(message = "Name cannot be null", groups =  Any.class)
    private String name;

    @Size(min = 3, max = 3, message = "Prefix must contains 3 letters")
    @NotNull(message = "Prefix cannot be null", groups =  Any.class)
    private String prefix;

}
