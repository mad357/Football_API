package org.football.club;

import org.football.util.validationgroups.Create;
import org.football.util.validationgroups.Update;
import org.football.AppConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import java.util.Set;

@ApplicationScoped
public class ClubService {
    @Inject
    ClubRepository clubRepository;

    @Transactional
    public long create(Club club) {
        Set<ConstraintViolation<Club>> errors = AppConfig.getValidator().validate(club, Create.class);
        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        Club alreadyExist = clubRepository.find( "fullname = ?1 and country = ?2", club.getFullname(), club.getCountry()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("Club already exist");
        }

        clubRepository.persist(club);

        return club.getId();
    }

    @Transactional
    public void update(Club club) {
        Set<ConstraintViolation<Club>> errors = AppConfig.getValidator().validate(club, Update.class);

        if (clubRepository.findByIdOptional(club.getId()).orElse(null) == null) {
            throw new RuntimeException("Club doesn't exist");
        }

        if (errors.size() > 0) {
            throw new RuntimeException(errors.iterator().next().getMessage());
        }
        Club alreadyExist = clubRepository.find( "fullname = ?1 and country = ?2 and id != ?3 ", club.getFullname(), club.getCountry(), club.getId()).firstResult();
        if (alreadyExist != null) {
            throw new RuntimeException("Club already exist");
        }
        clubRepository.getEntityManager().merge(club);
    }

    @Transactional
    public void delete(Long id) {
        Club club = clubRepository.findById(id);
        if (club != null) {
            clubRepository.delete(club);
        }
        else {
            throw new RuntimeException("Club was not found");
        }
    }
}
