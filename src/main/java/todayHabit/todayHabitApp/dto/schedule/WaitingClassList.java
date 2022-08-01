package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.schedule.CoachClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WaitingClassList {
    private Long id;
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
    private int waitingNumber;
    private List<ClassCoachDto> coachList;

    public WaitingClassList(WaitingMember waitingMember) {
        this.id = waitingMember.getId();
        this.classId = waitingMember.getSchedule().getId();
        this.classTypeName = waitingMember.getSchedule().getClassTypeName();
        this.classTypeColor = waitingMember.getSchedule().getClassTypeColor();
        this.category = waitingMember.getSchedule().getCategory();
        this.startDay = waitingMember.getSchedule().getStartDay();
        this.startTime = waitingMember.getSchedule().getStartTime();
        this.period = waitingMember.getSchedule().getPeriod();
        this.totalReservePerson = waitingMember.getSchedule().getTotalReservation();
        this.reservePerson = waitingMember.getSchedule().getReserveNumber();
        this.decrease = waitingMember.getSchedule().getDecrease();
        this.repeatDay = waitingMember.getSchedule().getRepeatDay();
        this.cycle = waitingMember.getSchedule().getCycle();
        this.waitingNumber = waitingMember.getWaitingNumber();
        this.coachList = waitingMember.getSchedule().getCoachClasses().stream()
                .map(coachInfo -> new ClassCoachDto(coachInfo))
                .collect(Collectors.toList());;
    }

    @Data
    static class ClassCoachDto {
        private String name;

        public ClassCoachDto(CoachClass coachClass) {
            this.name = coachClass.getCoachName();
        }
    }
}
