package todayHabit.todayHabitApp.dto;

import lombok.Data;
import todayHabit.todayHabitApp.domain.Notice;

import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private int count;

    public NoticeDto(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.description = notice.getDescription();
        this.date = notice.getDate();
        this.count = notice.getCount();
    }
}
