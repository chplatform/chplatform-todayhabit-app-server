package todayHabit.todayHabitApp.error;

import java.time.LocalDate;


public class HoldingException extends Exception {
    private LocalDate startDay;
    private LocalDate endDay;
    private Long holdingId;

    public HoldingException(LocalDate startDay, LocalDate endDay, Long holdingId) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.holdingId = holdingId;
    }

    public LocalDate getStartDay() {
        return startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }

    public Long getHoldingId() {
        return holdingId;
    }

}
