package org.football;

import org.modelmapper.ModelMapper;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("football")
public class AppConfig  extends Application {
    static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    public static Validator getValidator() {
        return validator;
    }

 static public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
