package s4s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s4s.entity.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
    public Boolean existsByName(String name);
    public Specialization findByName(String name);
}
