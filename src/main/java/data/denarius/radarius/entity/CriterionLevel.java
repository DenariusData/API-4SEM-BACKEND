package data.denarius.radarius.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "criterion_level")
public class CriterionLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer criterionLevelId;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    @Column(name = "cl_level")
    private Short level;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Person createdBy;

    public Integer getCriterionLevelId() {
        return criterionLevelId;
    }

    public void setCriterionLevelId(Integer criterionLevelId) {
        this.criterionLevelId = criterionLevelId;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public void setCriterion(Criterion criterion) {
        this.criterion = criterion;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }
}
