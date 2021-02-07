package org.ati.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private LocalDateTime date;

    @Getter
    @Setter
    @ManyToOne
    private UserDTO commentedBy;
}
