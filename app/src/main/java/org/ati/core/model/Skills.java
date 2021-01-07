package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "SKILLS")
public class Skills {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    @NotNull
    @NotEmpty
    private String name;
    @Getter
    @Setter
    @NotNull
    @NotEmpty
    private String description;
}
