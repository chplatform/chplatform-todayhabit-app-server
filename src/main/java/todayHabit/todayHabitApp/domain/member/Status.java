package todayHabit.todayHabitApp.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {

    유효,
    삭제;

    @JsonCreator
    public static Status RequestToEnum(String val) {
        for (Status status : Status.values()) {
            if (status.name().equals(val)) {
                return status;
            }
        }
        return null;
    }
}
