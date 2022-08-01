package todayHabit.todayHabitApp.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.classType.ClassType;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "member_membership_classType")
public class MemberOwnMembershipClassType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_membership_id")
    private MemberOwnMembership memberOwnMembership;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classType_id")
    private ClassType classType;

    @Column(name = "classType_name")
    private String classTypeName;
}
