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
    @Getter
    @Setter
    @Column(unique = true)
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToMany
    private Set<UserDTO> members;

    @Getter
    @Setter
    @OneToMany
    private Set<Task> tasks;

    @Getter
    @Setter
    @OneToOne(targetEntity = Flow.class, fetch = FetchType.EAGER)
    private Flow assignedFlow;

    public Group() {
        this.members = new HashSet<>();
        this.tasks = new HashSet<>();
    }
}
