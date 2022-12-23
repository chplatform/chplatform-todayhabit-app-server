package todayHabit.todayHabitApp.dto.member;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberMembershipHistory;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

@Data
@NoArgsConstructor
public class MemberMembershipHistoryDto {
	private Long gym_id;
    private Integer member_membership_history_id;
    private Integer member_membership_id;
    private Integer member_id;
    private Integer class_id;
    private Integer attend;
    private Integer class_count;
    private LocalDateTime insert_date;
    private Gym gym;
    private Member member;
    private MemberOwnMembership memberMembership;
    private Schedule schedule;
    
    @Builder
	public MemberMembershipHistoryDto(Long gym_id, Integer member_membership_history_id, Integer member_membership_id,
			Integer member_id, Integer class_id, Integer attend, Integer class_count, LocalDateTime insert_date,
			Gym gym, Member member, MemberOwnMembership memberMembership, Schedule schedule) {
		this.gym_id = gym_id;
		this.member_membership_history_id = member_membership_history_id;
		this.member_membership_id = member_membership_id;
		this.member_id = member_id;
		this.class_id = class_id;
		this.attend = attend;
		this.class_count = class_count;
		this.insert_date = insert_date;
		this.gym = gym;
		this.member = member;
		this.memberMembership = memberMembership;
		this.schedule = schedule;
	}
    
	public MemberMembershipHistory toEntity() {
		 return MemberMembershipHistory.builder()
				 .gym(gym)
				 .member(member)
				 .memberMembership(memberMembership)
				 .schedule(schedule)
				 .attend(attend)
				 .class_count(class_count)
				 .insert_date(insert_date)
				 .build();
	 }
}
