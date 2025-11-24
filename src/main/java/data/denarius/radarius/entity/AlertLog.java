package data.denarius.radarius.entity;

import data.denarius.radarius.listeners.AlertLogEntityListener;
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
@EntityListeners(AlertLogEntityListener.class)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "ale_id", nullable = false)
    private Alert alert;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reg_id", nullable = false)
    private Region region;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cri_id", nullable = false)
    private Criterion criterion;
}
