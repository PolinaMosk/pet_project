package s4s.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Data
@AllArgsConstructor
@Entity
@Table(name = "User", schema = "\"public\"")
public class User implements UserDetails {
    public User() {
        authorities.add(new SimpleGrantedAuthority("USER"));
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "user_type", nullable = false)
    private UserType user_type;

    @Column(name = "first_name", nullable = false)
    private String first_name;

    @Column(name = "second_name")
    private String second_name;

    @Column(name = "description")
    private String description;

    @Column(name = "third_name")
    private String third_name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    private String pswd;

    @ManyToOne(fetch = FetchType.EAGER)
    private University uni;

    @ManyToOne(fetch = FetchType.EAGER)
    private Specialization specialization;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Subject> subjects = new HashSet<>();

    @Column
    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Service> services = new ArrayList<>();

    @Column(name = "isOpenForRequests", nullable = false)
    private Boolean isOpenForRequests;

    @Column(name = "avatar")
    private String avatar_file;

    @JsonIgnore
    @ElementCollection
    private final Set<GrantedAuthority> authorities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return user_type == user.user_type &&
                first_name.equals(user.first_name) &&
                second_name.equals(user.second_name) &&
                Objects.equals(third_name, user.third_name) &&
                email.equals(user.email) &&
                login.equals(user.login) &&
                pswd.equals(user.pswd) &&
                Objects.equals(uni, user.uni) &&
                Objects.equals(specialization, user.specialization) &&
                Objects.equals(subjects, user.subjects) &&
                Objects.equals(services, user.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user_type, first_name, second_name, description, third_name, email, login, pswd, uni, specialization, subjects, services, isOpenForRequests, avatar_file, getAuthorities());
    }

    public void addService(Service pl){
        this.services.add(pl);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return pswd;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
