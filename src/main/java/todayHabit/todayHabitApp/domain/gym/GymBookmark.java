package todayHabit.todayHabitApp.domain.gym;

import com.fasterxml.jackson.annotation.JsonCreator;
import todayHabit.todayHabitApp.domain.member.Male;

import javax.persistence.Enumerated;

public enum GymBookmark {
    즐겨찾기,
    일반;

    @JsonCreator
    public static GymBookmark RequestToEnum(String val) {
        for (GymBookmark gymBookmark : GymBookmark.values()) {
            if (gymBookmark.name().equals(val)) {
                return gymBookmark;
            }
        }
        return null;
    }
}
