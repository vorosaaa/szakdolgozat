package org.ati.core.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "USERDTO")
@Data
public class UserDTO implements UserDetails {

    @Builder.Default
    private final UserRole userRole = UserRole.USER;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long id;

    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String email;
    @Transient

    @NotNull
    @NotEmpty
    private String matchingPassword;


    @ElementCollection
    private Map<Task, String> stats;

    @ManyToMany(targetEntity = Skills.class)
    private Set<Skills> skillSet;

    @ElementCollection
    private Map<Task, UserDTO> votedFor;

    @ManyToMany
    @JoinColumn(name = "group_id", nullable = true)
    private Set<Group> group;
    @Builder.Default
    private Boolean locked = false;
    @Builder.Default
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
