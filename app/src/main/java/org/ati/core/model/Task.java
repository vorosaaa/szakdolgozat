package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity(name = "TASK")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @NotNull
    @NotEmpty
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @ElementCollection
    @Getter
    @Setter
    private Set<String> optionalAnswers;

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
    private LocalDateTime validTo;

    @Getter
    @Setter
    private SystemConstants.TypeEnum typeEnum;

    @Getter
    @Setter
    @ManyToOne(targetEntity = UserDTO.class, fetch = FetchType.EAGER)
    private UserDTO assignedUser;

    @ElementCollection
    @Getter
    @Setter
    private Map<Long, Integer> votes;

    @ElementCollection
    @Getter
    @Setter
    private Map<String, Integer> answers;

    @ElementCollection
    @Getter
    @Setter
    private Map<UserDTO, Boolean> authenticatedUserVoted;

    @Getter
    @Setter
    private boolean voteFinished;

    @Getter
    @Setter
    private SystemConstants.StatusEnum statusEnum;

    public Task() {
        this.requiredSkills = new HashSet<>();
        this.optionalAnswers = new HashSet<>();
        this.votes = new HashMap<>();
        this.answers = new HashMap<>();
        this.authenticatedUserVoted = new HashMap<>();
        this.voteFinished = false;
        this.statusEnum = SystemConstants.StatusEnum.IN_PROGRESS;
        this.typeEnum = SystemConstants.TypeEnum.QUESTION;
    }
}
