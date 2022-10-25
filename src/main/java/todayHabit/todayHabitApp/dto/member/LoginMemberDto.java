package todayHabit.todayHabitApp.dto.member;

import lombok.Data;
import todayHabit.todayHabitApp.domain.gym.ApproveStatus;
import todayHabit.todayHabitApp.domain.gym.GymBookmark;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.member.Male;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembershipClassType;
import todayHabit.todayHabitApp.dto.gym.GymMembershipCountDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Data
public class LoginMemberDto {

    private Long memberId;
    private String name;
    private String email;
    private String birth;
    private Male male;
    private String phone;
    private int point;
    private List<gymListDto> gymList;

    public LoginMemberDto(Member member, Map<Long, Long> gymMembershipCountDto) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.birth = member.getBirth();
        this.male = member.getMale();
        this.phone = member.getPhone();
        this.point = member.getPoint();
        this.gymList = member.getGymList().stream()
                .map(gymList -> new gymListDto(gymList.getGym(), gymList.getBookmark(), gymList.getApprove(), gymMembershipCountDto))
                .collect(toList());
    }

    @Data
    private class gymListDto {
        private Long gymId;
        private String gymName;
        private GymBookmark bookmark;
        private ApproveStatus approve;
        private int openReserveDate; // 예약이 몇일 전부터 열릴지(일 단위)
        private String openReserveTime; // 예약이 몇 시 부터 열릴지

        private int pushAlarmTime; // 푸쉬 알림 시간(분단위)

        private int reservableTime; // 예약 가능 시간(분단위)

        private int changeReserveTime; // 예약 변경 가능 시간(분단위)

        private int cancelReserveTime; // 예약 취소 가능 시간(분단위)

        private int checkAttendTime; // 출석 가능 시간(분단위)
        private LocalTime lateTime; // 지각 기준 시간
        private int reserveConfirmTime; // 예약 확정 시간(예약 가능 시간과 동일:1, 예약 변경 가능시간과 동일:2, 예약 취소 가능시간과 동일:3)
        private int limitWaitingMember; // 대기 가능 회원 제한

        public gymListDto(Gym gym, GymBookmark bookmark, ApproveStatus approve, Map<Long, Long> gymMembershipCountDto) {
            this.gymId = gym.getId();
            this.gymName = gym.getName();
            this.bookmark = bookmark;
            this.approve = this.getApprove(gym.getId(), gymMembershipCountDto, approve);
            this.openReserveDate = gym.getOpenReserveDate();
            this.openReserveTime = gym.getOpenReserveTime();
            this.pushAlarmTime = gym.getPushAlarmTime();
            this.reservableTime = gym.getReservableTime();
            this.changeReserveTime = gym.getChangeReserveTime();
            this.cancelReserveTime = gym.getCancelReserveTime();
            this.checkAttendTime = gym.getCheckAttendTime();
            this.lateTime = gym.getLateTime();
            this.reserveConfirmTime = gym.getReserveConfirmTime();
            this.limitWaitingMember = gym.getLimitWaitingMember();
        }

        private ApproveStatus getApprove(Long gymId, Map<Long, Long> gymMembershipCountDto, ApproveStatus approve) {
            if(approve.equals(ApproveStatus.승인대기)){
                return approve;
            }else{
                if (gymMembershipCountDto.containsKey(gymId)) {
                    return approve;
                } else {
                    return ApproveStatus.만료;
                }
            }
        }
    }


}


