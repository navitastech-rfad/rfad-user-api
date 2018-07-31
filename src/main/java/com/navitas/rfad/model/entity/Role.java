package com.navitas.rfad.model.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Role extends AbstractBaseEntity {
  private static final long serialVersionUID = 6240795079648404557L;

  @Id
  @Column(length = 50)
  private String code;

  private String description;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Role) {
      final Role other = (Role) obj;
      return Objects.equals(code, other.getCode());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, description);
  }

  @Override
  public String toString() {
    final StringBuilder sb =
        new StringBuilder().append(this.getClass().getSimpleName()).append(": {");
    sb.append("code: ").append(code);
    sb.append(", ");
    sb.append("description: ").append(description);
    return sb.append('}').toString();
  }
}
