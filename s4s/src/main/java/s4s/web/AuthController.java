package s4s.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import s4s.entity.User;
import s4s.repository.UserRepository;
import s4s.service.CustomUserDetailsService;
import s4s.service.JwtUtil;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
public class AuthController {
    @Autowired
    private CustomUserDetailsService ud_service;

    @Autowired
    private UserRepository user_repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign_in")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        String jwt = jwtUtil.generateToken((UserDetails) authentication.getPrincipal());
        Optional<User> user = user_repo.findUserByLogin(authRequest.getLogin());
        return new AuthResponse(jwt, user.get());
    }
}

