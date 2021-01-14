package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "USERDTO")
public class UserDTO implements UserDetails {

    @Builder.Default
    private final UserRole userRole = UserRole.USER;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    @Getter
    @Setter
    private Long id;

    @NotNull
    @NotEmpty
    @Getter
    @Setter
    private String name;
    @NotNull
    @NotEmpty
    @Setter
    private String username;

    @NotNull
    @NotEmpty
    @Setter
    private String password;

    @NotNull
    @NotEmpty
    @Getter
    @Setter
    private String email;
    @Transient
    @NotNull
    @NotEmpty
    @Getter
    @Setter
    private String matchingPassword;

    @ElementCollection
    @Getter
    @Setter
    private Map<Task, String> stats;

    @ManyToMany(targetEntity = Skills.class)
    @Getter
    @Setter
    private Set<Skills> skillSet;

    @ElementCollection
    @Getter
    @Setter
    private Map<Task, UserDTO> votedFor;

    @ManyToMany
    @JoinColumn(name = "group_id", nullable = true)
    @Getter
    @Setter
    private Set<Group> group;
    @Builder.Default
    @Setter
    private Boolean locked = false;
    @Builder.Default
    @Setter
    private Boolean enabled = false;

    public UserDTO() {
        this.stats = new HashMap<>();
        this.skillSet = new HashSet<>();
        this.votedFor = new HashMap<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
