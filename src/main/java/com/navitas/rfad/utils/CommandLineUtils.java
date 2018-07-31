package com.navitas.rfad.utils;

import com.navitas.rfad.model.entity.Person;
import com.navitas.rfad.model.entity.PersonRole;
import com.navitas.rfad.model.entity.PersonRoleId;
import com.navitas.rfad.model.repository.PersonRepository;
import com.navitas.rfad.model.repository.PersonRoleRepository;

import java.util.Arrays;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Named
public class CommandLineUtils implements CommandLineRunner {
  @Inject private PersonRepository personRepository;
  @Inject private PersonRoleRepository personRoleRepository;
  @Inject private PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    final Person admin = createPerson("admin@admin.com", "admin", "Ad", "Min", "admin");
    final Person user = createPerson("user@user.com", "user", "John", "User", "user");

    final Person admin1 = personRepository.saveAndFlush(admin);
    final Person user1 = personRepository.saveAndFlush(user);

    personRoleRepository.saveAll(
        Arrays.asList(
            createPersonRole(admin1.getId(), "admin"), createPersonRole(user1.getId(), "user")));
  }

  private Person createPerson(
      String email, String password, String firstName, String lastName, String roleCode) {
    final Person person = new Person();
    person.setEmail(email);
    person.setFirstName(firstName);
    person.setLastName(lastName);
    person.setPassword(passwordEncoder.encode(password));
    return person;
  }

  private PersonRole createPersonRole(UUID id, String role) {
    final PersonRoleId personRoleId = new PersonRoleId(id, role);
    return new PersonRole(personRoleId);
  }
}
