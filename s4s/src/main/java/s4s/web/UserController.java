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
    UserRepository userRepo;
    @Autowired
    UniversityRepository universityRepository;
    @Autowired
    SpecializationRepository specializationRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    RequestRepository requestRepository;


    @GetMapping("/logins")
    public List<String> getAllLogins(){
        List<String> logins = new ArrayList<>();
        for (User u: userRepo.findAll()) logins.add(u.getLogin());
        return logins;
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){

        return userRepo.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        return user.get();
    }

    @GetMapping("/current_user")
    public ResponseEntity<User> getUserByJwt(){
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        return new ResponseEntity<>(userRepo.findUserByLogin(username).get(), HttpStatus.OK);
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
        Optional<User> user = userRepo.findById(id);
        for (Request r : requestRepository.findAllByReceiver(user.get())) {
            r.setReceiver(null);
            r.setStatus(Status.USER_NOT_EXIST);
        }
        for (Request r : requestRepository.findAllBySender(user.get())) {
            requestRepository.delete(r);
        }
        userRepo.delete(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/users/{id}/edit")
    public ResponseEntity<?> editUser(@PathVariable Long id, @RequestBody User edited_user) {
        Optional<User> user = userRepo.findById(id);
        if (user.get().equals(edited_user)) return new ResponseEntity<>(HttpStatus.OK);
        if (edited_user.getFirstName() != null) user.get().setFirstName(edited_user.getFirstName());
        if (edited_user.getSecondName() != null) user.get().setSecondName(edited_user.getSecondName());
        if (edited_user.getThirdName() != null) user.get().setThirdName(edited_user.getThirdName());
        if (edited_user.getUserType() != null) user.get().setUserType(edited_user.getUserType());
        if (edited_user.getEmail() != null) user.get().setEmail(edited_user.getEmail());
        if (edited_user.getPswd() != null) user.get().setPswd(edited_user.getPswd());
        user.get().setDescription(edited_user.getDescription());
        if (edited_user.getAvatarFile() != user.get().getAvatarFile()) user.get().setAvatarFile(edited_user.getAvatarFile());
        if (edited_user.getUserType() != null) {
            if (edited_user.getUserType() == UserType.STUDENT) {
                if (edited_user.getUni() != null && !universityRepository.existsByName(edited_user.getUni().getName()))
                    user.get().setUni(universityRepository.save(edited_user.getUni()));
                else if (edited_user.getUni() != null)
                    user.get().setUni(universityRepository.findByName(edited_user.getUni().getName()));
                if (edited_user.getSpecialization() != null && !specializationRepository.existsByName(edited_user.getSpecialization().getName()))
                    user.get().setSpecialization(specializationRepository.save(edited_user.getSpecialization()));
                else if (edited_user.getSpecialization() != null)
                    user.get().setSpecialization(specializationRepository.findByName(edited_user.getSpecialization().getName()));
            } else {
                user.get().setUni(null);
                user.get().setSpecialization(null);
                user.get().setServices(null);
            }
        } else if (user.get().getUserType() == UserType.STUDENT){
            if (edited_user.getUni() != null && !universityRepository.existsByName(edited_user.getUni().getName()))
                user.get().setUni(universityRepository.save(edited_user.getUni()));
            else if (edited_user.getUni() != null)
                user.get().setUni(universityRepository.findByName(edited_user.getUni().getName()));
            if (edited_user.getSpecialization() != null && !specializationRepository.existsByName(edited_user.getSpecialization().getName()))
                user.get().setSpecialization(specializationRepository.save(edited_user.getSpecialization()));
            else if (edited_user.getSpecialization() != null)
                user.get().setSpecialization(specializationRepository.findByName(edited_user.getSpecialization().getName()));
        }
        userRepo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/add_subject")
    public ResponseEntity<?> addSubjects(@PathVariable Long id, @RequestBody Subject subject) {
        Optional<User> user = userRepo.findById(id);
        if (!subjectRepository.existsByName(subject.getName())) {
            subjectRepository.save(subject);
        }
        user.get().getSubjects().add(subjectRepository.findByName(subject.getName()).get());
        userRepo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}/delete_subject")
    public ResponseEntity<?> deleteSubject(@PathVariable Long id, @RequestBody Subject subject) {
        Optional<User> user = userRepo.findById(id);
        user.get().getSubjects().remove(subjectRepository.findByName(subject.getName()).get());
        userRepo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/users/{id}/add_service")
    public ResponseEntity<?> addService(@PathVariable Long id, @RequestBody Service service) {
        Optional<User> user = userRepo.findById(id);
        if (user.get().getServices().contains(service)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        serviceRepository.save(service);
        user.get().getServices().add(serviceRepository.findByName(service.getName()));
        userRepo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}/delete_service")
    public ResponseEntity<?> deleteService(@PathVariable Long id, @RequestBody String service_name) {
        Service service = serviceRepository.findByName(service_name);
        Optional<User> user = userRepo.findById(id);
        List<User> l = new ArrayList<>();
        user.get().getServices().remove(service);
        serviceRepository.delete(service);
        userRepo.save(user.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
