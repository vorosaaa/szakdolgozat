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
    @ElementCollection
    private Set<UserRole> userRole;
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
    private Map<String, UserDTO> votedFor;

    @ElementCollection
    @Getter
    @Setter
    private Map<Task, String> answered;

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
        this.userRole = new HashSet<>();
        userRole.add(UserRole.USER);
        this.stats = new HashMap<>();
        this.skillSet = new HashSet<>();
        this.votedFor = new HashMap<>();
        this.answered = new HashMap<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (UserRole userRoleElement : userRole) {
            grantedAuthorities.add(new SimpleGrantedAuthority(userRoleElement.name()));
        }
        return grantedAuthorities;
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
