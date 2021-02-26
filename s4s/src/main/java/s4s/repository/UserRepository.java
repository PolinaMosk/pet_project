package s4s.repository;

import s4s.entity.Specialization;
import s4s.entity.University;
import s4s.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<List<User>> findAllByUniIn(Set<University> uni);
    public Optional<List<User>> findAllBySpecializationIn(Set<Specialization> specialization);
    public Optional<User> findUserByLogin(String login);
}
