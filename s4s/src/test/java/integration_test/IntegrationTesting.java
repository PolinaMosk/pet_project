package integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import s4s.S4sApplication;
import s4s.entity.*;
import s4s.repository.SubjectRepository;
import s4s.repository.UserRepository;
import s4s.web.AuthRequest;
import s4s.web.AuthResponse;
import s4s.web.UserController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = {S4sApplication.class, UserController.class}
)
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:test-application.properties")
public class IntegrationTesting {
    @Autowired
    MockMvc mvc;

    private final static int EXPECTED_PORT = 8080;

    final String baseUrl = "http://localhost:"+ EXPECTED_PORT;

    TestRestTemplate restTemplate = new TestRestTemplate();

    private String jwt;

    @Autowired
    UserRepository user_repo;

    @Autowired
    SubjectRepository sub_repo;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void basic_functions_test() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:8080");
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<?> responseEntity = restTemplate.exchange(baseUrl + "/sign_up", HttpMethod.POST, entity, String.class);
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        assertThat(user_repo.findUserByLogin("login").get().equals(user));

        User userr = new User();
        userr.setUser_type(UserType.STUDENT);
        userr.setLogin("login");
        userr.setPswd("pswd");
        userr.setFirst_name("Polina1");
        userr.setSecond_name("Polina1");
        userr.setEmail("p@p");
        Specialization spec = new Specialization();
        spec.setName("Программная Инжинерия");
        userr.setSpecialization(spec);
        University uni = new University();
        uni.setName("СПБПУ");
        userr.setUni(uni);
        userr.setId(1);
        userr.setIsOpenForRequests(true);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setPassword(userr.getPswd());
        authRequest.setLogin(userr.getLogin());
        HttpEntity<AuthRequest> auth_entity = new HttpEntity<>(authRequest, headers);
        ResponseEntity<AuthResponse> auth_responseEntity = restTemplate.exchange(baseUrl + "/sign_in", HttpMethod.POST, auth_entity, AuthResponse.class);
        assertThat(responseEntity.getStatusCode() == HttpStatus.OK);
        String jwt = auth_responseEntity.getBody().getJwtToken();

        headers.add("Authorization", "Bearer " + jwt);
        HttpEntity<User> entity1 = new HttpEntity<>(userr, headers);
        ResponseEntity<?> responseEntity1 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/edit", HttpMethod.PUT, entity1, String.class);
        assertThat(user_repo.findById((long) 1).get().equals(userr));
        assertThat(user_repo.findAll().size() == 1);

        Subject s1 = new Subject();
        s1.setName("Информатика");
        Subject s2 = new Subject();
        s2.setName("Математика");
        Set<Subject> set = new HashSet<>();
        set.add(s1);
        set.add(s2);
        userr.setSubjects(set);

        HttpEntity<Subject> entity2 = new HttpEntity<>(s1, headers);
        ResponseEntity<?> responseEntity2 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/add_subject", HttpMethod.POST, entity2, String.class);
        assertThat(responseEntity2.getStatusCode() == HttpStatus.OK);
        System.out.println(user_repo.findById((long) 1).get());
        HttpEntity<Subject> entity3 = new HttpEntity<>(s2, headers);
        ResponseEntity<?> responseEntity3 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/add_subject", HttpMethod.POST, entity3, String.class);
        HttpEntity<Subject> entity4 = new HttpEntity<>(s2, headers);
        ResponseEntity<?> responseEntity4 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/add_subject", HttpMethod.POST, entity4, String.class);

        s1.setId((long) 4);
        s2.setId((long) 5);
        uni.setId(2);
        spec.setId(3);
        HttpEntity<String> entity5 = new HttpEntity<>(null, headers);
        ResponseEntity<?> user_response = restTemplate.exchange(baseUrl + "/users/" + 1, HttpMethod.GET, entity5, String.class);
        Assert.assertEquals(user_response.getBody(), asJsonString(userr));;
    }

    @Test
    public void search(){
        User user = new User();
        user.setUser_type(UserType.HIGH_SCHOOL_STUDENT);
        user.setLogin("login2");
        user.setPswd("pswd2");
        user.setFirst_name("Valeria");
        user.setSecond_name("Valeria");
        user.setEmail("p@p");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:8080");
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<?> responseEntity = restTemplate.exchange(baseUrl + "/sign_up", HttpMethod.POST, entity, String.class);
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
        assertThat(user_repo.findUserByLogin("login2").get().equals(user));

        Subject s1 = new Subject();
        s1.setName("Математика");
        Subject s2 = new Subject();
        s2.setName("Информатика");
        Set<Subject> set = new HashSet<>();
        set.add(s1);
        set.add(s2);
        user.setSubjects(set);

        AuthRequest authRequest = new AuthRequest();
        authRequest.setPassword(user.getPswd());
        authRequest.setLogin(user.getLogin());
        HttpEntity<AuthRequest> entity1 = new HttpEntity<>(authRequest, headers);
        ResponseEntity<AuthResponse> responseEntity1 = restTemplate.exchange(baseUrl + "/sign_in", HttpMethod.POST, entity1, AuthResponse.class);
        assertThat(responseEntity.getStatusCode() == HttpStatus.OK);
        String jwt = responseEntity1.getBody().getJwtToken();

        headers.add("Authorization", "Bearer " + jwt);
        HttpEntity<Subject> entity2 = new HttpEntity<>(s1, headers);
        ResponseEntity<?> responseEntity2 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/add_subject", HttpMethod.POST, entity2, String.class);
        assertThat(responseEntity2.getStatusCode() == HttpStatus.OK);
        HttpEntity<Subject> entity3 = new HttpEntity<>(s2, headers);
        ResponseEntity<?> responseEntity3 = restTemplate.exchange(baseUrl + "/users/" + 1 + "/add_subject", HttpMethod.POST, entity3, String.class);
        assertThat(responseEntity2.getStatusCode() == HttpStatus.OK);

        User user1 = new User();
        user1.setUser_type(UserType.STUDENT);
        user1.setLogin("login3");
        user1.setPswd("pswd3");
        user1.setFirst_name("Anastasia");
        user1.setSecond_name("Anastasia");
        user1.setEmail("p@p");

        HttpEntity<User> entity4 = new HttpEntity<>(user1, headers);
        ResponseEntity<?> responseEntity4 = restTemplate.exchange(baseUrl + "/sign_up", HttpMethod.POST, entity4, String.class);
        Assert.assertEquals(responseEntity4.getStatusCode(), HttpStatus.CREATED);
        assertThat(user_repo.findUserByLogin("login3").get().equals(user1));

        University uni = new University();
        uni.setName("СПБГУ");
        user1.setUni(uni);

        headers.add("Authorization", "Bearer " + jwt);
        HttpEntity<User> entity5 = new HttpEntity<>(user1, headers);
        ResponseEntity<?> responseEntity5 = restTemplate.exchange(baseUrl + "/users/" + user_repo.findUserByLogin("login3").get().getId() + "/edit", HttpMethod.PUT, entity5, String.class);

        user.setId(6);
        user1.setId(7);
        Set<University> uni_list = new HashSet<>();
        University uni1 = new University();
        uni1.setName("СПБПУ");
        uni_list.add(uni1);
        Set<Specialization> spec_list = new HashSet<>();
        Specialization spec = new Specialization();
        spec.setName("Программная Инжинерия");
        spec_list.add(spec);

        Search search_params = new Search();
        search_params.setSubs(set);
        search_params.setUser_type(UserType.STUDENT);
        search_params.setUnis(uni_list);
        search_params.setSpecs(spec_list);
        HttpEntity<Search> entity6 = new HttpEntity<>(search_params, headers);
        ResponseEntity<?> responseEntity6 = restTemplate.exchange(baseUrl + "/search", HttpMethod.POST, entity6, String.class);
        List<User> list = new ArrayList<>();
        list.add(user_repo.findUserByLogin("login").get());
        Assert.assertNotEquals(responseEntity6.getBody(), null);
        Assert.assertEquals(responseEntity6.getBody().toString(), asJsonString(list));
    }
}
