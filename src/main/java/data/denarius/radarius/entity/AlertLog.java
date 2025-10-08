package data.denarius.radarius.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert_log")
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "al_id")
    private Integer Id;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    private Alert alert;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @Column(name = "al_created_at")
    private LocalDateTime createdAt;

    @Column(name = "al_previous_level")
    private Short previousLevel;

    @Column(name = "al_new_level")
    private Short newLevel;

    @Column(name = "al_closed_at")
    private LocalDateTime closedAt;


}
