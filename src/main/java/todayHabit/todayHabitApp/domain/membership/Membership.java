package todayHabit.todayHabitApp.domain.membership;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.hibernate.mapping.Join;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "membership")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membershipCategory_id")
    @JsonIgnore
    private MembershipCategory membershipCategory;

    private String name;
    private int price;
    private int periodType;
    private int period;
    private int maxApply;
    private int maxDayAttend;
    private int maxWeekAttend;
    private int holdingMembershipCount;
    private int holdingMembershipPeriod;

}
