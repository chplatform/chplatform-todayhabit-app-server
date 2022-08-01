package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.schedule.CoachClass;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DayClassDto {

    private Long classId;
    private String classTypeName;
    private String classTypeColor;
    private int category;
    private LocalDate startDay;
    private LocalTime startTime;
    private int period;
    private int totalReservePerson;
    private int reservePerson;
    private int decrease;
    private String repeatDay;
    private String cycle;
    private boolean reserve;
    private int waitingNumber;
    private List<ClassCoachDto> coachList;

    public DayClassDto(Schedule classList) {
        this.classId = classList.getId();
        this.classTypeName = classList.getClassTypeName();
        this.classTypeColor = classList.getClassTypeColor();
        this.category = classList.getCategory();
        this.startDay = classList.getStartDay();
        this.startTime = classList.getStartTime();
        this.period = classList.getPeriod();
        this.totalReservePerson = classList.getTotalReservation();
        this.reservePerson = classList.getReserveNumber() + classList.getWaitingMemberList().size();;
        this.decrease = classList.getDecrease();
        this.repeatDay = classList.getRepeatDay();
        this.cycle = classList.getCycle();
        this.reserve = false;
        this.waitingNumber = classList.getWaitingMemberList().size();
        this.coachList = classList.getCoachClasses().stream()
                .map(coachInfo -> new ClassCoachDto(coachInfo))
                .collect(Collectors.toList());
    }

    public DayClassDto(MemberClass classList) {
        this.classId = classList.getSchedule().getId();
        this.classTypeName = classList.getSchedule().getClassTypeName();
        this.classTypeColor = classList.getSchedule().getClassTypeColor();
        this.category = classList.getSchedule().getCategory();
        this.startDay = classList.getSchedule().getStartDay();
        this.startTime = classList.getSchedule().getStartTime();
        this.period = classList.getSchedule().getPeriod();
        this.totalReservePerson = classList.getSchedule().getTotalReservation();
        this.reservePerson = classList.getSchedule().getReserveNumber();
        this.decrease = classList.getSchedule().getDecrease();
        this.repeatDay = classList.getSchedule().getRepeatDay();
        this.cycle = classList.getSchedule().getCycle();
        this.coachList = classList.getSchedule().getCoachClasses().stream()
                .map(coachInfo -> new ClassCoachDto(coachInfo))
                .collect(Collectors.toList());
    }

    @Data
    static class ClassCoachDto {
        private String name;

        public ClassCoachDto(CoachClass coachClass) {
            this.name = coachClass.getCoachName();
        }
    }
}
