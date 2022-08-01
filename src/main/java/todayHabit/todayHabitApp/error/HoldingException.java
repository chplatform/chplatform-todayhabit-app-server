package todayHabit.todayHabitApp.error;

import java.time.LocalDate;


public class HoldingException extends Exception {
    private LocalDate startDay;
    private LocalDate endDay;
    private Long holdingMembershipId;

    public HoldingException(LocalDate startDay, LocalDate endDay, Long holdingMembershipId) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.holdingMembershipId = holdingMembershipId;
    }

    public LocalDate getStartDay() {
        return startDay;
    }

    public LocalDate getEndDay() {
        return endDay;
    }

    public Long getHoldingMembershipId() {
        return holdingMembershipId;
    }

}
