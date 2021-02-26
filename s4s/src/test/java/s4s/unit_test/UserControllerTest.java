package s4s.unit_test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import s4s.entity.*;
import s4s.repository.*;
import s4s.service.CustomUserDetailsService;
import s4s.service.JwtUtil;
import s4s.web.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest({SignUpController.class, UserController.class, AuthController.class})
public class UserControllerTest {
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
    @WithMockUser
    void findById() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        mvc.perform(get("/users/" + 0).header("Origin", "http://localhost:8080"));
        Mockito.verify(user_repo, times(1)).findById((long) 0);
    }

    @Test
    @WithMockUser
    void editUser() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        User user1 = new User();
        user1.setUser_type(UserType.STUDENT);
        user1.setLogin("login");
        user1.setPswd("pswd");
        user1.setFirst_name("Valeria");
        user1.setSecond_name("Valeria");
        user1.setEmail("p@p");
        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        Mockito.when(user_repo.findById((long) 1)).thenReturn(java.util.Optional.of(user1));

        mvc.perform(put("/users/" + 0 + "/edit").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user1)));
        Mockito.verify(user_repo, times(1)).findById((long) 0);
        Mockito.verify(user_repo, times(2)).save(user);
        assertThat(user_repo.findById((long) 0)).get().isEqualTo(user1);
    }

    @Test
    @WithMockUser
    void editUserUniSpec() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        User user1 = new User();
        user1.setUser_type(UserType.STUDENT);
        user1.setLogin("login");
        user1.setPswd("pswd");
        user1.setFirst_name("Valeria");
        user1.setSecond_name("Valeria");
        user1.setEmail("p@p");
        University uni = new University();
        uni.setId(4);
        uni.setName("СПБПУ");
        uni_repo.save(uni);
        user1.setUni(uni);
        Specialization spec = new Specialization();
        spec.setId(5);
        spec.setName("Программная инжинерия");
        spec_repo.save(spec);
        user1.setSpecialization(spec);
        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(uni_repo.save(uni)).thenReturn(uni);
        Mockito.when(spec_repo.save(spec)).thenReturn(spec);

        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        Mockito.when(user_repo.findById((long) 1)).thenReturn(java.util.Optional.of(user1));
        Mockito.when(uni_repo.findByName(uni.getName())).thenReturn(uni);
        Mockito.when(spec_repo.findByName(spec.getName())).thenReturn(spec);

        mvc.perform(put("/users/" + 0 + "/edit").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user1)));
        Mockito.verify(user_repo, times(1)).findById((long) 0);
        Mockito.verify(user_repo, times(2)).save(user);
        Mockito.verify(uni_repo, times(2)).save(uni);
        Mockito.verify(spec_repo, times(2)).save(spec);
        assertThat(user_repo.findById((long) 0)).get().isEqualTo(user1);
        assertThat(user_repo.findById((long) 0).get().getUni().equals(uni));
        assertThat(user_repo.findById((long) 0).get().getSpecialization().equals(spec));
    }

    @Test
    @WithMockUser
    void addSubject() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        Subject s = new Subject();
        s.setName("Информатика");
        s.setId((long) 1);

        sub_repo.save(s);
        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(sub_repo.save(s)).thenReturn(s);
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        Mockito.when(sub_repo.findByName(s.getName())).thenReturn(java.util.Optional.of(s));

        mvc.perform(post("/users/" + 0 + "/add_subject").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(s)));
        assertThat(user_repo.findById((long) 0).get().getSubjects().contains(s));
        Mockito.verify(sub_repo, times(2)).save(s);
        Mockito.verify(user_repo, times(2)).save(user);
    }

    @Test
    @WithMockUser
    void deleteSubject() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        Subject s = new Subject();
        s.setName("Информатика");
        s.setId((long) 1);

        sub_repo.save(s);
        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(sub_repo.save(s)).thenReturn(s);
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        Mockito.when(sub_repo.findByName(s.getName())).thenReturn(java.util.Optional.of(s));
        mvc.perform(post("/users/" + 0 + "/add_subject").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(s)));
        assertThat(user_repo.findById((long) 0).get().getSubjects().contains(s));
        mvc.perform(delete("/users/" + 0 + "/delete_subject").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(s)));
        Mockito.verify(sub_repo, times(2)).save(s);
        Mockito.verify(user_repo, times(3)).save(user);
        assertThat(!user_repo.findById((long) 0).get().getSubjects().contains(s));
    }

    @Test
    @WithMockUser
    void addService() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        Service s = new Service();
        s.setId(2);
        s.setName("Подготовка к ЕГЭ по профильной математике");
        s.setPrice("от 800 р./час");
        service_repo.save(s);

        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(service_repo.save(s)).thenReturn(s);
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));

        mvc.perform(post("/users/" + 0 + "/add_service").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(s)));
        assertThat(user_repo.findById((long) 0).get().getServices().contains(s));
        Mockito.verify(service_repo, times(2)).save(s);
        Mockito.verify(user_repo, times(2)).save(user);

    }

    @Test
    @WithMockUser
    void deleteService() throws Exception {
        User user = new User();
        user.setUser_type(UserType.STUDENT);
        user.setLogin("login");
        user.setPswd("pswd");
        user.setFirst_name("Polina");
        user.setSecond_name("Polina");
        user.setEmail("p@p");
        user.setId(0);
        user_repo.save(user);

        Service s = new Service();
        s.setId(2);
        s.setName("Подготовка к ЕГЭ по профильной математике");
        s.setPrice("от 800 р./час");
        service_repo.save(s);

        Mockito.when(user_repo.save(user)).thenReturn(user);
        Mockito.when(service_repo.save(s)).thenReturn(s);
        Mockito.when(user_repo.findById((long) 0)).thenReturn(java.util.Optional.of(user));
        Mockito.when(service_repo.findByName(s.getName())).thenReturn(s);

        mvc.perform(post("/users/" + 0 + "/add_service").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(s)));
        assertThat(user_repo.findById((long) 0).get().getServices().contains(s));
        mvc.perform(delete("/users/" + 0 + "/delete_service").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s.getName()));

        Mockito.verify(service_repo, times(2)).save(s);
        Mockito.verify(user_repo, times(3)).save(user);
        Mockito.verify(service_repo, times(2)).findByName(s.getName());
        assertThat(!user_repo.findById((long) 0).get().getServices().contains(s));
    }

}
