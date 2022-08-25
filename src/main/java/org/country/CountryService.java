package org.country;

import org.AppConfig;
import org.util.validationgroups.Create;
import org.util.validationgroups.Update;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import java.util.Set;

@ApplicationScoped
public class CountryService {
    @Inject
    CountryRepository countryRepository;

    @Transactional
    public long create(Country country) {
        Set<ConstraintViolation<Country>> errors = AppConfig.getValidator().validate(country, Create.class);
        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        Country alreadyExist = countryRepository.find( "prefix = ?1 ", country.getPrefix()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("Country already exist");
        }

        countryRepository.persist(country);

        return country.getId();
    }

    @Transactional
    public void update(Country country) {
        Set<ConstraintViolation<Country>> errors = AppConfig.getValidator().validate(country, Update.class);
        if (countryRepository.findByIdOptional(country.getId()).orElse(null) == null) {
            throw new RuntimeException("Country doesn't exist");
        }
        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        Country alreadyExist = countryRepository.find( "prefix = ?1 and id != ?2 ", country.getPrefix(), country.getId()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("Country already exist");
        }
        countryRepository.getEntityManager().merge(country);
    }

    @Transactional
    public void delete(Long id) {
        Country country = countryRepository.findById(id);
        if (country != null) {
            countryRepository.delete(country);
        }
        else {
            throw new RuntimeException("Country was not found");
        }
    }
}
