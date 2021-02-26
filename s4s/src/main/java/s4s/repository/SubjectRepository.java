package s4s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s4s.entity.Subject;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    public Boolean existsByName(String name);
    public Optional<Subject> findByName(String name);
}
