package todayHabit.todayHabitApp.domain.membership;

import com.fasterxml.jackson.annotation.JsonIgnore;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.*;

@Entity
public class MembershipCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membershipCategory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    private String name;


}
