package org.football.league;

import exceptions.DuplicateException;
import exceptions.NotFoundException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class LeagueService {
    @Inject
    LeagueRepository leagueRepository;

    @Transactional
    public long create(League league) {
        League alreadyExist = leagueRepository.find( "name = ?1 and country = ?2", league.getName(), league.getCountry()).firstResult();
        if (alreadyExist != null) {
            throw new DuplicateException("League already exist");
        }

        leagueRepository.persist(league);

        return league.getId();
    }

    @Transactional
    public void update(League league) {
        if (leagueRepository.findByIdOptional(league.getId()).orElse(null) == null) {
            throw new NotFoundException("League doesn't exist");
        }
        League alreadyExist = leagueRepository.find( "name = ?1 and country = ?2 and id != ?3 ", league.getName(), league.getCountry(), league.getId()).firstResult();
        if (alreadyExist != null) {
            throw new DuplicateException("League already exist");
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
            throw new NotFoundException("League was not found");
        }
    }
}
