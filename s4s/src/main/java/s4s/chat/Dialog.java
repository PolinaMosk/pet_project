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

    public Dialog(Long idUser1, Long idUser2, List<Message> messages) {
        this.id = idUser1.toString() + "_" + idUser2.toString();
        this.messages = messages;
    }

    public Dialog() {

    }
}
