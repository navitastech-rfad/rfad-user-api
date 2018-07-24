package com.navitas.rfad.model.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PersonRole extends AbstractAuditEntity {
    private static final long serialVersionUID = 169489592151932909L;

    @EmbeddedId
    private PersonRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId", insertable = false, updatable = false)
    Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleCode", insertable = false, updatable = false)
    Role role;

    public PersonRole() {}

    public PersonRole(PersonRoleId id) {
        this.id = id;
    }

    public PersonRoleId getId() {
        return id;
    }

    public void setId(PersonRoleId id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
