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
import org.springframework.test.web.servlet.MvcResult;
import s4s.entity.*;
import s4s.repository.*;
import s4s.service.CustomUserDetailsService;
import s4s.service.JwtUtil;
import s4s.web.AuthController;
import s4s.web.SearchController;
import s4s.web.SignUpController;
import s4s.web.UserController;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest({SignUpController.class, UserController.class, AuthController.class, SearchController.class})
public class SearchControllerTest {
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
    void search() throws Exception {
        Set<University> uni_list = new HashSet<>();
        Set<Specialization> spec_list = new HashSet<>();
        Set<Subject> sub_list = new HashSet<>();
        List<User> user_list = new ArrayList<>();
        List<User> all_users = new ArrayList<>();
        User user1 = new User();
        user1.setUser_type(UserType.STUDENT);
        user1.setLogin("login");
        user1.setPswd("pswd");
        user1.setFirst_name("Polina");
        user1.setSecond_name("Polina");
        user1.setEmail("p@p");
        user1.setId(0);
        University uni = new University();
        uni.setId(8);
        uni.setName("СПБГУ");
        uni_repo.save(uni);
        user1.setUni(uni);
        Specialization spec = new Specialization();
        spec.setId(7);
        spec.setName("Прикладная информатика");
        user1.setSpecialization(spec);
        Subject s4 = new Subject();
        s4.setName("Математика");
        Set<Subject> set = new HashSet<>();
        set.add(s4);
        user1.setSubjects(set);
        spec_repo.save(spec);
        user_repo.save(user1);
        all_users.add(user1);

        User user2 = new User();
        user2.setUser_type(UserType.HIGH_SCHOOL_STUDENT);
        user2.setLogin("login1");
        user2.setPswd("pswd1");
        user2.setFirst_name("Алина");
        user2.setSecond_name("Алина");
        user2.setEmail("p@p");
        user2.setId(1);
        Subject s1 = new Subject();
        s1.setName("Математика");
        Set<Subject> set1 = new HashSet<>();
        set1.add(s1);
        user2.setSubjects(set1);
        user_repo.save(user2);
        all_users.add(user2);

        User user3 = new User();
        user3.setUser_type(UserType.APPLICANT);
        user3.setLogin("login2");
        user3.setPswd("pswd2");
        user3.setFirst_name("Алиса");
        user3.setSecond_name("Алиса");
        user3.setEmail("p@p");
        user3.setId(2);
        Subject s2 = new Subject();
        s2.setName("Математика");
        Subject s3 = new Subject();
        s3.setName("Информатика");
        Set<Subject> set2 = new HashSet<>();
        set2.add(s2);
        set2.add(s3);
        user2.setSubjects(set2);
        user_repo.save(user3);
        all_users.add(user3);

        User user4 = new User();
        user4.setUser_type(UserType.STUDENT);
        user4.setLogin("login3");
        user4.setPswd("pswd3");
        user4.setFirst_name("Валерия");
        user4.setSecond_name("Валерия");
        user4.setEmail("p@p");
        user4.setId(3);
        University uni1 = new University();
        uni1.setId(4);
        uni1.setName("СПБПУ");
        uni_repo.save(uni1);
        user4.setUni(uni1);
        Specialization spec2 = new Specialization();
        spec2.setId(5);
        spec2.setName("Программная инжинерия");
        user4.setSpecialization(spec2);
        spec_repo.save(spec2);
        user_repo.save(user4);
        all_users.add(user4);

        Mockito.when(uni_repo.findByName(uni.getName())).thenReturn(uni);
        Mockito.when(spec_repo.findByName(spec.getName())).thenReturn(spec);
        Mockito.when(spec_repo.findByName(spec2.getName())).thenReturn(spec2);
        Mockito.when(sub_repo.findByName(s4.getName())).thenReturn(java.util.Optional.of(s4));
        Mockito.when(sub_repo.findByName(s1.getName())).thenReturn(java.util.Optional.of(s1));
        Mockito.when(user_repo.findAll()).thenReturn(all_users);
        Mockito.when(user_repo.save(user1)).thenReturn(user1);
        Mockito.when(user_repo.save(user2)).thenReturn(user2);
        Mockito.when(user_repo.save(user3)).thenReturn(user3);
        Mockito.when(user_repo.save(user4)).thenReturn(user4);
        University university = new University();
        university.setName("СПБГУ");
        uni_list.add(university);
        user_list.add(user1);
        Specialization specialization = new Specialization();
        specialization.setName("Прикладная информатика");
        spec_list.add(specialization);
        Specialization specialization1 = new Specialization();
        specialization1.setName("Программная инжинерия");
        spec_list.add(specialization1);
        Subject subject = new Subject();
        subject.setName("Математика");
        sub_list.add(subject);
        Mockito.when(uni_repo.findByName(university.getName())).thenReturn(university);
        Mockito.when(spec_repo.findByName(specialization.getName())).thenReturn(specialization);
        Mockito.when(spec_repo.findByName(specialization1.getName())).thenReturn(specialization1);
        Mockito.when(user_repo.findAllByUniIn(uni_list)).thenReturn(java.util.Optional.of(user_list));
        Mockito.when(user_repo.findAllBySpecializationIn(spec_list)).thenReturn(java.util.Optional.of(user_list));

        Search search_params = new Search();
        search_params.setSpecs(spec_list);
        search_params.setUnis(uni_list);
        search_params.setSubs(sub_list);
        search_params.setUser_type(UserType.STUDENT);
        MvcResult result = mvc.perform(post("/search").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(search_params))).andReturn();
        assertThat(result.equals(user1));

        University university1 = new University();
        university.setName("СПБПУ");
        uni_list.add(university);
        user_list.add(user1);
        user_list.add(user4);
        sub_list.clear();
        search_params.setUnis(uni_list);
        search_params.setSubs(sub_list);
        MvcResult result1 = mvc.perform(post("/search").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(search_params))).andReturn();
        List<User> result_list = new ArrayList<>();
        result_list.add(user1);
        result_list.add(user4);
        assertThat(result.getResponse().getContentAsString().equals(result_list.toString()));

        uni_list.clear();
        spec_list.clear();
        sub_list.add(subject);
        search_params.setSpecs(spec_list);
        search_params.setUnis(uni_list);
        search_params.setSubs(sub_list);
        search_params.setUser_type(UserType.HIGH_SCHOOL_STUDENT);

        MvcResult result2 = mvc.perform(post("/search").header("Origin", "http://localhost:8080")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(search_params))).andReturn();
        List<User> result_list1 = new ArrayList<>();
        result_list1.add(user2);
        result_list1.add(user3);
        assertThat(result.getResponse().getContentAsString().equals(result_list1.toString()));
    }
}
