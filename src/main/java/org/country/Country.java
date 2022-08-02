package org.country;

import org.util.validationgroups.Any;
import org.util.validationgroups.Create;
import org.util.validationgroups.Update;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "COUNTRY")
public class Country implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUNTRY_ID")
    @NotNull(message = "Id cannot be null", groups = Update.class)
    @Null(message = "Country cannot contain id", groups = Create.class)
    private Long id;

    @Size(max = 50, message = "name size exceeded")
    @NotNull(message = "Name cannot be null", groups =  Any.class)
    @Column(name = "NAME")
    private String name;

    @Size(min = 3, max = 3, message = "Prefix must contains 3 letters")
    @NotNull(message = "Prefix cannot be null", groups =  Any.class)
    @Column(name = "PREFIX")
    private String prefix;


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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
