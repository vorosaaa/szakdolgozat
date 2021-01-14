package org.ati.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
public class Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private int flowID;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @OneToMany
    @Getter
    @Setter
    private Set<UserDTO> usersOfFlow;
    @OneToMany
    @Getter
    @Setter
    private Set<Task> tasksOfFlow;
}
