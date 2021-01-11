package org.ati.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;


@Entity
@Data
public class Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int flowID;
    private String name;
    private String description;
    @OneToMany
    private Set<UserDTO> usersOfFlow;
    @OneToMany
    private Set<Task> tasksOfFlow;
}
