package s4s.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import s4s.entity.User;
import s4s.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class SignUpController {
    @Autowired
    private UserRepository user_repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value = "/sign_up", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<?> signUp(@RequestBody User newUser) {
        newUser.setIsOpenForRequests(true);
        newUser.setPswd(passwordEncoder.encode(newUser.getPswd()));
        user_repo.save(newUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
