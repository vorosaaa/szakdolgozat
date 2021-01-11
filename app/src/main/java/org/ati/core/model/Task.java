package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity(name = "TASK")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    private String description;

    @ManyToMany
    private Set<Skills> requiredSkills;

    @ManyToOne
    private Group group;

    @ManyToOne(targetEntity = UserDTO.class, fetch = FetchType.EAGER)
    private UserDTO assignedUser;

    @ElementCollection
    private Map<UserDTO, Integer> votes;

    @ElementCollection
    private Map<UserDTO, Boolean> authenticatedUserVoted;

    private boolean voteFinished;

    private StatusEnum statusEnum;

    public Task() {
        this.requiredSkills = new HashSet<>();
        this.votes = new HashMap<>();
        this.authenticatedUserVoted = new HashMap<>();
        this.voteFinished = false;
        this.statusEnum = StatusEnum.IN_PROGRESS;
    }
}
