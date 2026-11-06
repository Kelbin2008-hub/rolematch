package com.rolematch.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Enterprise Skill entity cataloging normalized competencies for candidate matching.
 */
@Entity
@Table(name = "skills", indexes = {
        @Index(name = "idx_skill_name_unique", columnList = "name", unique = true)
})
public class Skill extends AuditableEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    public Skill() {
    }

    public Skill(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill name must not be blank.");
        }
        this.name = name.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill skill)) return false;
        return name != null && name.equalsIgnoreCase(skill.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
