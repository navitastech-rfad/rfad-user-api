package com.navitas.rfad.model.repository;

import com.navitas.rfad.model.entity.Person;

import java.util.UUID;

public interface PersonRepository extends BaseRepository<Person, UUID> {
  Person findByEmail(String email);
}
