package s4s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s4s.entity.Request;
import s4s.entity.User;

import java.util.Set;

public interface RequestRepository extends JpaRepository<Request, Long> {
    public Set<Request> findAllBySender(User sender);
    public Set<Request> findAllByReceiver(User receiver);
}
