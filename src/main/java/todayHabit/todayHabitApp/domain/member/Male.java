package todayHabit.todayHabitApp.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Male {
    남,
    여,
    없음;

    @JsonCreator
    public static Male RequestToEnum(String val) {
        for (Male male : Male.values()) {
            if (male.name().equals(val)) {
                return male;
            }
        }
        return null;
    }
}
