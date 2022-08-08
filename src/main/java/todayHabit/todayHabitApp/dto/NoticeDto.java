package todayHabit.todayHabitApp.dto;

import lombok.Data;
import todayHabit.todayHabitApp.domain.Notice;

import java.time.LocalDateTime;
import java.util.Base64;

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
        //this.description = notice.getDescription();
        if(!notice.getDescription().isEmpty() 
        		&& notice.getDescription().substring(0,3).equals("PHA")) {
        	byte[] bytes = notice.getDescription().getBytes();
			this.description = new String(Base64.getDecoder().decode(bytes));
        }else {
        	this.description = notice.getDescription();
        }
        this.date = notice.getDate();
        this.count = notice.getCount();
    }
}
