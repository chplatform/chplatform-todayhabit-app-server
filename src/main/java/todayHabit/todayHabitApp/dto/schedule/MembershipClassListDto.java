package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.schedule.CoachClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MembershipClassListDto {

    private int count;
    private LocalDate membershipStartDate;
    private LocalDate membershipEndDate;
    private List<ClassInfo> classList;
    private List<WaitingInfo> waitingList;

    public MembershipClassListDto(int count, LocalDate membershipStartDate, LocalDate membershipEndDate, List<MemberClass> classList, List<WaitingMember> waitingList) {
        this.count = count;
        this.membershipStartDate = membershipStartDate;
        this.membershipEndDate = membershipEndDate;
        this.classList = classList.stream()
                .map(classLists -> new ClassInfo(classLists))
                .collect(Collectors.toList());
        this.waitingList = waitingList.stream()
                .map(waitingMember -> new WaitingInfo(waitingMember))
                .collect(Collectors.toList());
    }

    @Data
    static class ClassInfo{
        private Long id;
        private String classTypeName;
        private LocalDate startDate;
        private LocalTime startTime;
        private int count;
        private List<ClassCoachDto> coachName;
        private String status;
        public ClassInfo(MemberClass memberClass) {
            this.id = memberClass.getSchedule().getId();
            this.classTypeName = memberClass.getSchedule().getClassTypeName();
            this.startDate = memberClass.getSchedule().getStartDay();
            this.startTime = memberClass.getSchedule().getStartTime();
            this.count = memberClass.getSchedule().getDecrease();
            this.status = "예약";
            this.coachName = memberClass.getSchedule().getCoachClasses()
                    .stream().map(coachClass -> new ClassCoachDto(coachClass))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class WaitingInfo {
        private Long id;
        private String classTypeName;
        private LocalDate startDate;
        private LocalTime startTime;
        private int count;
        private int waitingNumber;
        private String status;
        private List<ClassCoachDto> coachName;

        public WaitingInfo(WaitingMember waitingMember) {
            this.id = waitingMember.getSchedule().getId();
            this.classTypeName = waitingMember.getSchedule().getClassTypeName();
            this.startDate = waitingMember.getSchedule().getStartDay();
            this.startTime = waitingMember.getSchedule().getStartTime();
            this.count = waitingMember.getSchedule().getDecrease();
            this.waitingNumber = waitingMember.getWaitingNumber();
            this.status = "대기";
            this.coachName = waitingMember.getSchedule().getCoachClasses()
                    .stream().map(coachClass -> new ClassCoachDto(coachClass))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class ClassCoachDto {
        private Long id;
        private String name;

        public ClassCoachDto(CoachClass coachClass) {
            this.id = coachClass.getCoach().getId();
            this.name = coachClass.getCoachName();
        }
    }
}
