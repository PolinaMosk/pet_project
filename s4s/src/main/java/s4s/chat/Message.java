package s4s.chat;

import lombok.Data;
import s4s.entity.Status;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "Msgs")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String sender_login;
    private String receiver_login;
    private String content;
    @ElementCollection
    private Set<String> filenames;
    private Date send_date;
    private Status status;

}
