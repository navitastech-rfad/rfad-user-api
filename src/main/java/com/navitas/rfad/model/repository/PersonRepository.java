package com.navitas.rfad.model.repository;

import java.util.UUID;

import com.navitas.rfad.model.entity.Person;

public interface PersonRepository extends BaseRepository<Person, UUID> {
    Person findByEmail(String email);
}
