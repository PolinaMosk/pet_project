package s4s.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import s4s.entity.*;
import s4s.repository.SpecializationRepository;
import s4s.repository.SubjectRepository;
import s4s.repository.UniversityRepository;
import s4s.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class SearchController {
    @Autowired
    SubjectRepository sub_repo;
    @Autowired
    UniversityRepository uni_repo;
    @Autowired
    SpecializationRepository spec_repo;
    @Autowired
    UserRepository user_repo;

    @GetMapping("/universities")
    public ResponseEntity<List<University>> getUnis(){
        return new ResponseEntity<>(uni_repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getSubjects(){
        return new ResponseEntity<>(sub_repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<Specialization>> getSpecializations(){
        return new ResponseEntity<>(spec_repo.findAll(), HttpStatus.OK);
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    @PostMapping("/search")
    public List<User> search(@RequestBody Search parameters) {
        if (parameters.getUser_type() == UserType.STUDENT) {
            List<User> users = new ArrayList<>();
            List<User> users_uni = new ArrayList<>();
            Set<University> unis = new HashSet<>();
            if (parameters.getUnis() != null) {
                for (University u : parameters.getUnis()) {
                    unis.add(uni_repo.findByName(u.getName()));
                }
                if (user_repo.findAllByUniIn(unis).isPresent()) {
                    users_uni = user_repo.findAllByUniIn(unis).get();
                } else {
                    return null;
                }
            }

            List<User> users_spec = new ArrayList<>();
            Set<Specialization> specs = new HashSet<>();
            if (parameters.getSpecs() != null) {
                for (Specialization s : parameters.getSpecs()) {
                    specs.add(spec_repo.findByName(s.getName()));
                }
                users_spec = user_repo.findAllBySpecializationIn(specs).get();
            }

            users = intersection(users_uni, users_spec);

            List<User> users_subs;
            Set<Subject> subs = new HashSet<>();
            for (Subject s : parameters.getSubs()) {
                subs.add(sub_repo.findByName(s.getName()).get());
            }
            List<User> users1 = new ArrayList<>();
            for (User u : users) {
                if ((intersection(u.getSubjects().stream().collect(Collectors.toList()), new ArrayList<Subject>(subs))).size() != 0)
                    users1.add(u);
            }

            List<User> users_final = new ArrayList<>();
            for (User u : users1) if (u.getUser_type() == parameters.getUser_type()) users_final.add(u);
            return users_final;
        } else {
            Set<Subject> subs = new HashSet<>();
            for (Subject s : parameters.getSubs()) {
                subs.add(sub_repo.findByName(s.getName()).get());
            }
            List<User> users = new ArrayList<>();
            for (User u : user_repo.findAll()) {
                if ((intersection(u.getSubjects().stream().collect(Collectors.toList()), new ArrayList<Subject>(subs))).size() != 0 &&
                    u.getUser_type() != UserType.STUDENT)
                    users.add(u);
            }
            return users;
        }
    }
}
