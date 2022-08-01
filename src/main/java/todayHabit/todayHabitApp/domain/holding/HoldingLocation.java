package todayHabit.todayHabitApp.domain.holding;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum HoldingLocation {
    WEB, APP;

    @JsonCreator
    public static HoldingLocation RequestToEnum(String val) {
        for (HoldingLocation holdingLocation : HoldingLocation.values()) {
            if (holdingLocation.name().equals(val)) {
                return holdingLocation;
            }
        }
        return null;
    }
}
