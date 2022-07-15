package org.league;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LeagueRepository implements ILeagueRepository, PanacheRepository<League> {

}
