package todayHabit.todayHabitApp.domain.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.classType.ClassType;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Table(name = "class")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classType_id")
    private ClassType classType;

    @Column(name = "classType_name")
    private String classTypeName;

    @Column(name = "classType_color")
    private String classTypeColor;

    @JsonIgnore
    @OneToMany(mappedBy = "classes")
    @BatchSize(size = 10)
    private List<CoachClass> coachClasses;

    @JsonIgnore
    @OneToMany(mappedBy = "schedule")
    private List<WaitingMember> waitingMemberList;

    private int category;
    private LocalDate startDay;
    private LocalTime startTime;
    private int period;
    private int totalReservation;
    private int reserveNumber;
    private int decrease;
    private String repeatDay;
    private LocalDate repeatEndDay;
    private String cycle;

    protected Schedule() {

    }

    public void increaseCount() {
        this.reserveNumber += 1;
    }

    public void decreaseCount() {
        this.reserveNumber -= 1;
    }
}
