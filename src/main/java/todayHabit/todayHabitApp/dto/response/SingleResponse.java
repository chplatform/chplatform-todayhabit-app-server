package todayHabit.todayHabitApp.dto.response;

import lombok.Data;

@Data
public class SingleResponse<T> extends DefaultRes {
    T data;
}
