package s4s.unit_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import s4s.entity.User;
import s4s.entity.UserType;
import s4s.repository.*;
import s4s.service.CustomUserDetailsService;
import s4s.service.JwtUtil;
import s4s.web.SignUpController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SignUpController.class)
class SignUpControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    JwtUtil jwtUtil;
    @MockBean
    CustomUserDetailsService userDetailsService;
    @MockBean
    UserRepository user_repo;
    @MockBean
    UniversityRepository uni_repo;
    @MockBean
    SpecializationRepository spec_repo;
    @MockBean
    SubjectRepository sub_repo;
    @MockBean
    ServiceRepository service_repo;
    @MockBean
    RequestRepository req_repo;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void rightTest() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");


        mvc.perform(post("/sign_up").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user)))
                .andExpect(status().isCreated());
        user_repo.save(user);
    }

}