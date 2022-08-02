package todayHabit.todayHabitApp.error;

import java.time.LocalDate;
import java.util.List;

import todayHabit.todayHabitApp.domain.holding.HoldingInfo;


public class HoldingException extends Exception {

    private HoldingInfo holdingInfo;
    private int resCode;

    public HoldingException(HoldingInfo holdingInfo, int resCode) {
        this.holdingInfo = holdingInfo;
        this.resCode = resCode;
    }
    
	public HoldingInfo getHoldingInfo() {
		return holdingInfo;
	}

	public int getResCode() {
		return resCode;
	}
	


}
