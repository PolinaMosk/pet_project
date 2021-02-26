package s4s.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import s4s.chat.Dialog;

public interface DialogRepository extends JpaRepository<Dialog, String> {
}
