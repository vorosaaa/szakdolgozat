package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity(name = "TASK")
public class Task {
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
    private String description;
    @Getter
    @Setter
    @ManyToMany
    private Set<Skills> requiredSkills;
    @Getter
    @Setter
    @ManyToOne
    private Group group;
    @Getter
    @Setter
    @ManyToOne(targetEntity = UserDTO.class, fetch = FetchType.EAGER)
    private UserDTO assignedUser;
    @Getter
    @Setter
    @ElementCollection
    private Map<UserDTO, Integer> votes;
    @Getter
    @Setter
    @ElementCollection
    private Map<UserDTO, Boolean> authenticatedUserVoted;
    @Getter
    @Setter
    private boolean voteFinished;
    @Getter
    @Setter
    private StatusEnum statusEnum;

    public Task() {
        this.requiredSkills = new HashSet<>();
        this.votes = new HashMap<>();
        this.authenticatedUserVoted = new HashMap<>();
        this.voteFinished = false;
        this.statusEnum = StatusEnum.IN_PROGRESS;
    }
}
