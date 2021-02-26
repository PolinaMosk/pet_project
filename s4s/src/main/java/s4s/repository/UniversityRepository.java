package s4s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s4s.entity.University;

public interface UniversityRepository extends JpaRepository<University, Long> {
    public Boolean existsByName(String name);
    public University findByName(String name);
}
