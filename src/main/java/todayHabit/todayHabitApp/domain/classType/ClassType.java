package todayHabit.todayHabitApp.domain.classType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "classType")
public class ClassType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classType_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classCategory_id")
    private ClassTypeCategory classTypeCategory;

    private String name;
    private int decrease;
    private String color;
    private int time;
    private int reservePerson;

}
