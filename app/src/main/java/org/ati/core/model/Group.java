package org.ati.core.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "USERGROUP")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    @Getter
    @Setter
    private Long id;
    @Getter
    @Setter
    private String name;


    @ManyToMany
    @Getter
    @Setter
    private Set<UserDTO> members;

    @OneToMany
    @Getter
    @Setter
    private Set<Task> tasks;

    @OneToOne(targetEntity = Flow.class, fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Flow assignedFlow;

    public Group() {
        this.members = new HashSet<>();
        this.tasks = new HashSet<>();
    }
}
