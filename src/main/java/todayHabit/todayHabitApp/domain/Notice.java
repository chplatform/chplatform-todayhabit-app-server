package todayHabit.todayHabitApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    private String primaryKey;
    private String title;
    private String description;
    private LocalDateTime date;
    private int count;

    public void increaseCount() {
        this.count += 1;
    }

}
