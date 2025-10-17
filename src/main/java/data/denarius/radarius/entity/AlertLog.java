package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert_log")
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "al_id")
    private Integer id;

    @Column(name = "al_created_at")
    private LocalDateTime createdAt;

    @Column(name = "al_previous_level")
    private Short previousLevel;

    @Column(name = "al_new_level")
    private Short newLevel;

    @Column(name = "al_closed_at")
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "ale_id")
    private Alert alert;

    @ManyToOne
    @JoinColumn(name = "reg_id")
    private Region region;

    @ManyToOne
    @JoinColumn(name = "cri_id")
    private Criterion criterion;
}
