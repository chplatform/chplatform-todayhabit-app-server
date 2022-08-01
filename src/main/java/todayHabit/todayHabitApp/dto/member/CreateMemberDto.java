package todayHabit.todayHabitApp.dto.member;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.regex.Pattern;

@Data
public class CreateMemberDto {
    @NotEmpty(message = "이름을 입력해주세요")
    private String name;
    @NotEmpty(message = "생년월일을 입력해주세요")
    private String birth;
    @NotEmpty(message = "성별을 선택해주세요")
    private String male;
    @NotEmpty(message = "휴대전화번호를 입력해주세요")
    private String phone;
    @NotEmpty(message = "이메일계정을 입력해주세요")
    private String email;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    private String passwd;

    public boolean matchesPasswd() {
        String pattern = "^(?=.*?[a-z]|[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";

        return Pattern.matches(pattern, this.passwd);
    }
}
