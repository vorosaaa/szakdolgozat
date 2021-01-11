package org.ati.core.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "USERGROUP")
@Data
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true)
    private Long id;

    private String name;


    @ManyToMany
    private Set<UserDTO> members;

    @OneToMany
    private Set<Task> tasks;

    @OneToOne(targetEntity = Flow.class, fetch = FetchType.EAGER)
    private Flow assignedFlow;

    public Group() {
        this.members = new HashSet<>();
        this.tasks = new HashSet<>();
    }
}
