package org.football.country;

import exceptions.DuplicateException;
import exceptions.NotFoundException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class CountryService {
    @Inject
    CountryRepository countryRepository;

    @Transactional
    public long create(Country country) {
        Country alreadyExist = countryRepository.find( "prefix = ?1 ", country.getPrefix()).firstResult();
        if (alreadyExist != null) {
            throw new DuplicateException("Country already exist");
        }

        countryRepository.persist(country);

        return country.getId();
    }

    @Transactional
    public void update(Country country) {
        if (countryRepository.findByIdOptional(country.getId()).orElse(null) == null) {
            throw new NotFoundException("Country doesn't exist");
        }
        Country alreadyExist = countryRepository.find( "prefix = ?1 and id != ?2 ", country.getPrefix(), country.getId()).firstResult();
        if (alreadyExist != null) {
            throw new NotFoundException("Country already exist");
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
            throw new NotFoundException("Country was not found");
        }
    }
}
