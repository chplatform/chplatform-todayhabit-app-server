package todayHabit.todayHabitApp.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse<T> extends DefaultRes{
    List<T> data;
}
