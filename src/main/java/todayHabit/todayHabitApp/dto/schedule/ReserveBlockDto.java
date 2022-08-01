package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.ReserveBlock;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReserveBlockDto {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDay;
    private String description;

    public ReserveBlockDto(ReserveBlock reserveBlock) {
        this.id = reserveBlock.getId();
        this.startDay = reserveBlock.getStartDay();
        this.startTime = reserveBlock.getStartTime();
        this.endTime = reserveBlock.getEndTime();
        this.description = reserveBlock.getDescription();
    }
}
