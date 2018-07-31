package com.navitas.rfad.model.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

@Embeddable
public class PersonRoleId implements Serializable {
  private static final long serialVersionUID = -6906780686299358409L;

  private UUID personId;
  private String roleCode;

  public PersonRoleId() {}

  public PersonRoleId(UUID personId, String roleCode) {
    this.personId = personId;
    this.roleCode = roleCode;
  }

  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }
}
