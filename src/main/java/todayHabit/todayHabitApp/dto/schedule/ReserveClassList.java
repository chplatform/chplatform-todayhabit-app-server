package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.schedule.CoachClass;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ReserveClassList {
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
    private int attend;
    private List<ClassCoachDto> coachList;

    public ReserveClassList(MemberClass memberClass) {
        this.id = memberClass.getId();
        this.classId = memberClass.getSchedule().getId();
        this.classTypeName = memberClass.getSchedule().getClassTypeName();
        this.classTypeColor = memberClass.getSchedule().getClassTypeColor();
        this.category = memberClass.getSchedule().getCategory();
        this.startDay = memberClass.getSchedule().getStartDay();
        this.startTime = memberClass.getSchedule().getStartTime();
        this.period = memberClass.getSchedule().getPeriod();
        this.totalReservePerson = memberClass.getSchedule().getTotalReservation();
        this.reservePerson = memberClass.getSchedule().getReserveNumber();
        this.decrease = memberClass.getSchedule().getDecrease();
        this.repeatDay = memberClass.getSchedule().getRepeatDay();
        this.cycle = memberClass.getSchedule().getCycle();
        this.attend = memberClass.getBeforeAttend();
        this.coachList = memberClass.getSchedule().getCoachClasses()
                .stream()
                .map(coachClass -> new ClassCoachDto(coachClass))
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
