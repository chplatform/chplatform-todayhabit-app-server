package todayHabit.todayHabitApp.dto.member;

import lombok.Data;
import todayHabit.todayHabitApp.domain.member.Member;

@Data
public class MemberDto {
    private String name;
    private String email;
    private String passwd;
    private String phone;

    protected void memberDto() {

    }

    public MemberDto(String name, String email, String passwd, String phone) {
        this.name = name;
        this.email = email;
        this.passwd = passwd;
        this.phone = phone;
    }
}
