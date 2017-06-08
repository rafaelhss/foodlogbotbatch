package com.foodlog.scheduledmeal;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * A ScheduledMeal.
 */
@Entity
@Table(name = "scheduled_meal")
public class ScheduledMeal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "target_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date target_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ScheduledMeal name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public ScheduledMeal description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTarget_time() {
        return target_time;
    }

    public ScheduledMeal target_time(Date target_time) {
        this.target_time = target_time;
        return this;
    }

    public void setTarget_time(Date target_time) {
        this.target_time = target_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduledMeal scheduledMeal = (ScheduledMeal) o;
        if (scheduledMeal.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), scheduledMeal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ScheduledMeal{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", target_time='" + getTarget_time() + "'" +
            "}";
    }
}
