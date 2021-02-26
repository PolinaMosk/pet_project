package s4s.entity;


import java.util.Set;


public class Search {
    private Set<University> unis;
    private Set<Specialization> specs;
    private Set<Subject> subs;
    private UserType user_type;

    public Set<University> getUnis() {
        return unis;
    }

    public void setUnis(Set<University> unis) {
        this.unis = unis;
    }

    public Set<Specialization> getSpecs() {
        return specs;
    }

    public void setSpecs(Set<Specialization> specs) {
        this.specs = specs;
    }

    public Set<Subject> getSubs() {
        return subs;
    }

    public void setSubs(Set<Subject> subs) {
        this.subs = subs;
    }

    public UserType getUser_type() {
        return user_type;
    }

    public void setUser_type(UserType user_type) {
        this.user_type = user_type;
    }
}
