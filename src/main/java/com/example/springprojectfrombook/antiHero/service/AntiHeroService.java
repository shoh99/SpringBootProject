package com.example.springprojectfrombook.antiHero.service;

import com.example.springprojectfrombook.antiHero.entity.AntiHeroEntity;
import com.example.springprojectfrombook.antiHero.repository.AntiHeroRepository;
import com.example.springprojectfrombook.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;



@Service
@AllArgsConstructor
public class AntiHeroService {

    private final AntiHeroRepository repo;

    public Iterable<AntiHeroEntity> findAllAntiHeroes() {
        return repo.findAll();
    }

    public AntiHeroEntity findAntiHeroById(UUID id) {
        return findOrThrow(id);
    }


    public void removeAntiHeroById(UUID id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    public AntiHeroEntity addAntiHero(AntiHeroEntity antiHero) {
        return repo.save(antiHero);
    }

    public void updateAntiHero(UUID id, AntiHeroEntity antiHero) {
        findOrThrow(id);
        repo.save(antiHero);
    }

    private AntiHeroEntity findOrThrow(final UUID id) {
        return repo.
                findById(id)
                .orElseThrow(
        () -> new NotFoundException("Anti-hero by Id " + id + " was not found")
                );
    }
}
