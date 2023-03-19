package com.example.springprojectfrombook.antiHero.controller;


import com.example.springprojectfrombook.antiHero.dto.AntiHeroDto;
import com.example.springprojectfrombook.antiHero.entity.AntiHeroEntity;
import com.example.springprojectfrombook.antiHero.service.AntiHeroService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1/anti-heroes")
@PreAuthorize("isAuthenticated()")
public class AntiHeroController {

    private final AntiHeroService service;
    private final ModelMapper mapper;

    private static final Logger LOGGER =  LoggerFactory.getLogger(AntiHeroController.class);

    @GetMapping("/{id}")
    public AntiHeroDto getAntiHeroById(@PathVariable("id") UUID id) {
        return convertToDto(service.findAntiHeroById(id));
    }

    @PostMapping
    public AntiHeroDto postAntiHero(@Valid @RequestBody AntiHeroDto antiHeroDto) {
        var entity = convertToEntity(antiHeroDto);
        var antiHero = service.addAntiHero(entity);

        return convertToDto(antiHero);
    }

    @PutMapping("/{id}")
    public void putAntiHero(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AntiHeroDto antiHeroDto
    ) {
        if (!id.equals(antiHeroDto.getId())) throw new
                ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "id does not match"
        );

        var antiHeroEntity = convertToEntity(antiHeroDto);
        service.updateAntiHero(id, antiHeroEntity);
    }

    @DeleteMapping("/{id}")
    public void deleteAntiHeroById(@PathVariable("id") UUID id) {
        service.removeAntiHeroById(id);
    }

    @GetMapping
    public List<AntiHeroDto> getAntiHeroes(Pageable pageable) {
        int toSkip = pageable.getPageSize() * pageable.getPageNumber();

        LOGGER.info("Using SLF4J: Getting anti hero list - getAntiHeroes()");

        var antiHeroList = StreamSupport
                .stream(service.findAllAntiHeroes().spliterator(), false)
                .skip(toSkip).limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return antiHeroList
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AntiHeroDto convertToDto(AntiHeroEntity entity) {
        return mapper.map(entity, AntiHeroDto.class);
    }

    private AntiHeroEntity convertToEntity(AntiHeroDto dto) {
        return mapper.map(dto, AntiHeroEntity.class);
    }
}
