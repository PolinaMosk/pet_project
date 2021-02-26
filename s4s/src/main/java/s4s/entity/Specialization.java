package s4s.entity;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Specializations")
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "number")
    private String number;
}
