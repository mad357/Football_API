package org.country;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CountryRepository implements ICountryRepository, PanacheRepository<Country> {

}
