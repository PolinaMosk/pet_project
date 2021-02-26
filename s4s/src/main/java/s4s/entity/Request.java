package s4s.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private User sender;

    @ManyToOne(cascade = CascadeType.ALL)
    private User receiver;

    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "status")
    private Status status;
}
