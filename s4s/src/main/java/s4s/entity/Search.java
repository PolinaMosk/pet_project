package s4s.entity;


import java.util.Set;


public class Search {
    private Set<University> unis;
    private Set<Specialization> specs;
    private Set<Subject> subs;
    private UserType userType;

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
