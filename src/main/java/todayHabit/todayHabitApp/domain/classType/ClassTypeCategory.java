package todayHabit.todayHabitApp.domain.classType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.*;

@Entity
@Table(name = "classCategory")
public class ClassTypeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classCategory_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;

    private String name;


}
