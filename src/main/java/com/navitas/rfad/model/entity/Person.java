package com.navitas.rfad.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Person extends AbstractAuditEntity {
  private static final long serialVersionUID = 6759513807301586837L;

  @Id @GeneratedValue private UUID id;

  @NotBlank(message = "Email must not be empty.")
  @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Must be a valid email: 'user@test.com'")
  private String email;

  @NotBlank(message = "Password must not be empty.")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @NotBlank(message = "First Name must not be empty.")
  @Size(max = 50, message = "Must be less than 50 characters")
  private String firstName;

  @NotBlank(message = "Last Name must not be empty.")
  @Size(max = 50, message = "Must be less than 50 characters")
  private String lastName;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "person")
  private Set<PersonRole> personRoles = new HashSet<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Set<PersonRole> getPersonRoles() {
    return personRoles;
  }

  public void setPersonRoles(Set<PersonRole> personRoles) {
    this.personRoles = personRoles;
  }

  public void addPersonRole(PersonRole personRole) {
    this.personRoles.add(personRole);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Person) {
      final Person other = (Person) obj;
      return Objects.equals(id, other.getId()) || Objects.equals(email, other.getEmail());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, email);
  }
}
