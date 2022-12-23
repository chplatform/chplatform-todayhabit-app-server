package todayHabit.todayHabitApp.domain.member;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

@EqualsAndHashCode( of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member_membership_history")
public class MemberMembershipHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_membership_history_id")
    private Integer id;
    
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
    private MemberOwnMembership memberMembership;
    
    private Integer attend;
    private Integer class_count;
    private LocalDateTime insert_date;
    
    @Builder
    public MemberMembershipHistory(Integer id, Gym gym, Schedule schedule, Member member,
    		MemberOwnMembership memberMembership, Integer attend, Integer class_count, LocalDateTime insert_date) {
		this.id = id;
		this.gym = gym;
		this.schedule = schedule;
		this.member = member;
		this.memberMembership = memberMembership;
		this.attend = attend;
		this.class_count = class_count;
		this.insert_date = insert_date;
	}
}
