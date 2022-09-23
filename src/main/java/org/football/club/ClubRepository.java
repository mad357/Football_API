package org.football.club;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClubRepository implements IClubRepository, PanacheRepository<Club> {

}
