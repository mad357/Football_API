package org.football.league;

import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;
import org.football.AppConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import java.util.Set;

@ApplicationScoped
public class LeagueService {
    @Inject
    LeagueRepository leagueRepository;

    @Transactional
    public long create(League league) {
        Set<ConstraintViolation<League>> errors = AppConfig.getValidator().validate(league, Create.class);
        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        League alreadyExist = leagueRepository.find( "name = ?1 and country = ?2", league.getName(), league.getCountry()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("League already exist");
        }

        leagueRepository.persist(league);

        return league.getId();
    }

    @Transactional
    public void update(League league) {
        Set<ConstraintViolation<League>> errors = AppConfig.getValidator().validate(league, Update.class);
        if (leagueRepository.findByIdOptional(league.getId()).orElse(null) == null) {
            throw new RuntimeException("League doesn't exist");
        }

        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        League alreadyExist = leagueRepository.find( "name = ?1 and country = ?2 and id != ?3 ", league.getName(), league.getCountry(), league.getId()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("League already exist");
        }

        leagueRepository.getEntityManager().merge(league);
    }

    @Transactional
    public void delete(Long id) {
        League league = leagueRepository.findById(id);
        if (league != null) {
            leagueRepository.delete(league);
        }
        else {
            throw new RuntimeException("League was not found");
        }
    }
}