package todayHabit.todayHabitApp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "waitingMember")
public class WaitingMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @JsonIgnore
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_membership_id")
    @JsonIgnore
    private MemberOwnMembership memberOwnMembership;

    private LocalDateTime date;
    private int waitingNumber;

    protected WaitingMember() {

    }

    public WaitingMember(Gym gym, Schedule schedule, Member member, MemberOwnMembership memberOwnMembership, int waitingNumber) {
        this.gym = gym;
        this.schedule = schedule;
        this.member = member;
        this.memberOwnMembership = memberOwnMembership;
        this.date = LocalDateTime.now();
        this.waitingNumber = waitingNumber;
    }

    public void changeWaitingNumber() {
        this.waitingNumber -= 1;
    }
}
