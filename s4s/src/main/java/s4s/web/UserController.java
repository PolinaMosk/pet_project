package s4s.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import s4s.entity.*;
import s4s.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class UserController {
    @Autowired
    UserRepository user_repo;
    @Autowired
    UniversityRepository uni_repo;
    @Autowired
    SpecializationRepository spec_repo;
    @Autowired
    SubjectRepository sub_repo;
    @Autowired
    ServiceRepository service_repo;
    @Autowired
    RequestRepository req_repo;


    @GetMapping("/logins")
    public List<String> getAllLogins(){
        List<String> logins = new ArrayList<>();
        for (User u: user_repo.findAll()) logins.add(u.getLogin());
        return logins;
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){

        return user_repo.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = user_repo.findById(id);
        return user.get();
    }

    @GetMapping("/current_user")
    public ResponseEntity<User> getUserByJwt(){
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        return new ResponseEntity<>(user_repo.findUserByLogin(username).get(), HttpStatus.OK);
    }

    @GetMapping("/services")
    public ResponseEntity<List<String>> getServices(){
        User user = getUserByJwt().getBody();
        List<String> services = new ArrayList<>();
        for (Service s: user.getServices())
            services.add(s.getName());
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = user_repo.findById(id);
        for (Request r : req_repo.findAllByReceiver(user.get())) {
            r.setReceiver(null);
            r.setStatus(Status.USER_NOT_EXIST);
        }
        for (Request r : req_repo.findAllBySender(user.get())) {
            req_repo.delete(r);
        }
        user_repo.delete(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/{id}/edit")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody User edited_user) {
        Optional<User> user = user_repo.findById(id);
        if (user.get().equals(edited_user)) return new ResponseEntity<>(HttpStatus.OK);
        if (edited_user.getFirst_name() != null) user.get().setFirst_name(edited_user.getFirst_name());
        if (edited_user.getSecond_name() != null) user.get().setSecond_name(edited_user.getSecond_name());
        if (edited_user.getThird_name() != null) user.get().setThird_name(edited_user.getThird_name());
        if (edited_user.getUser_type() != null) user.get().setUser_type(edited_user.getUser_type());
        if (edited_user.getEmail() != null) user.get().setEmail(edited_user.getEmail());
        if (edited_user.getPswd() != null) user.get().setPswd(edited_user.getPswd());
        user.get().setDescription(edited_user.getDescription());
        if (edited_user.getAvatar_file() != user.get().getAvatar_file()) user.get().setAvatar_file(edited_user.getAvatar_file());
        if (edited_user.getUser_type() != null) {
            if (edited_user.getUser_type() == UserType.STUDENT) {
                if (edited_user.getUni() != null && !uni_repo.existsByName(edited_user.getUni().getName()))
                    user.get().setUni(uni_repo.save(edited_user.getUni()));
                else if (edited_user.getUni() != null)
                    user.get().setUni(uni_repo.findByName(edited_user.getUni().getName()));
                if (edited_user.getSpecialization() != null && !spec_repo.existsByName(edited_user.getSpecialization().getName()))
                    user.get().setSpecialization(spec_repo.save(edited_user.getSpecialization()));
                else if (edited_user.getSpecialization() != null)
                    user.get().setSpecialization(spec_repo.findByName(edited_user.getSpecialization().getName()));
            } else {
                user.get().setUni(null);
                user.get().setSpecialization(null);
                user.get().setServices(null);
            }
        } else if (user.get().getUser_type() == UserType.STUDENT){
            if (edited_user.getUni() != null && !uni_repo.existsByName(edited_user.getUni().getName()))
                user.get().setUni(uni_repo.save(edited_user.getUni()));
            else if (edited_user.getUni() != null)
                user.get().setUni(uni_repo.findByName(edited_user.getUni().getName()));
            if (edited_user.getSpecialization() != null && !spec_repo.existsByName(edited_user.getSpecialization().getName()))
                user.get().setSpecialization(spec_repo.save(edited_user.getSpecialization()));
            else if (edited_user.getSpecialization() != null)
                user.get().setSpecialization(spec_repo.findByName(edited_user.getSpecialization().getName()));
        }
        user_repo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/add_subject")
    public ResponseEntity<?> addSubjects(@PathVariable Long id, @RequestBody Subject subject) {
        Optional<User> user = user_repo.findById(id);
        if (!sub_repo.existsByName(subject.getName())) {
            sub_repo.save(subject);
        }
        user.get().getSubjects().add(sub_repo.findByName(subject.getName()).get());
        user_repo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}/delete_subject")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id, @RequestBody Subject subject) {
        Optional<User> user = user_repo.findById(id);
        user.get().getSubjects().remove(sub_repo.findByName(subject.getName()).get());
        user_repo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/add_service")
    public ResponseEntity<?> addService(@PathVariable Long id, @RequestBody Service service) {
        Optional<User> user = user_repo.findById(id);
        if (user.get().getServices().contains(service)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        service_repo.save(service);
        user.get().getServices().add(service_repo.findByName(service.getName()));
        user_repo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}/delete_service")
    public ResponseEntity<?> deleteService(@PathVariable Long id, @RequestBody String service_name) {
        Service service = service_repo.findByName(service_name);
        Optional<User> user = user_repo.findById(id);
        List<User> l = new ArrayList<>();
        user.get().getServices().remove(service);
        service_repo.delete(service);
        user_repo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
