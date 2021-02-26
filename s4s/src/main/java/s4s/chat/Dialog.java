package s4s.chat;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "Dialogs")
public class Dialog {
    @Id
    private String id;
    @OneToMany
    private List<Message> messages;

    public Dialog(Long id_user1, Long id_user2, List<Message> messages) {
        this.id = id_user1.toString() + "_" + id_user2.toString();
        this.messages = messages;
    }

    public Dialog() {

    }
}
