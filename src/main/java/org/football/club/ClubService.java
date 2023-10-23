package org.football.club;

import exceptions.DuplicateException;
import exceptions.NotFoundException;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class ClubService {
    @Inject
    ClubRepository clubRepository;

    @Transactional
    public long create(Club club) {
        Club alreadyExist = clubRepository.find( "fullname = ?1 and country = ?2", club.getFullname(), club.getCountry()).firstResult();
        if (alreadyExist != null) {
            throw new DuplicateException("Club already exist");
        }

        clubRepository.persist(club);

        return club.getId();
    }

    @Transactional
    public void update(Club club) {
        if (clubRepository.findByIdOptional(club.getId()).orElse(null) == null) {
            throw new NotFoundException("Club doesn't exist");
        }

        Club alreadyExist = clubRepository.find( "fullname = ?1 and country = ?2 and id != ?3 ", club.getFullname(), club.getCountry(), club.getId()).firstResult();
        if (alreadyExist != null) {
            throw new DuplicateException("Club already exist");
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
            throw new NotFoundException("Club was not found");
        }
    }
}
