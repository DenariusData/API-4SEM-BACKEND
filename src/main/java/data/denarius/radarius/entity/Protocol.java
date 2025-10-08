package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "protocol")
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "pro_id")
    private Integer Id;

    @OneToOne(mappedBy = "root_cause")
    private Protocol rootCause;

    @Column(name = "pro_name")
    private String name;

    @Column(name = "pro_created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Person createdBy;

    @OneToMany(mappedBy = "alert")
    private List<Alert> alerts;

}

