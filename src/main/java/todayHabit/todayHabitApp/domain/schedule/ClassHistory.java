package todayHabit.todayHabitApp.domain.schedule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "classHistory")
public class ClassHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    @JsonIgnore
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;
    private LocalDateTime date;

    /**
     * 0: 예약
     * 1: 출석
     * 2: 결석
     * 3: 대기
     * 4: 취소
     * 5: 대기 자동 취소
     */
    private int attend;

    public ClassHistory(Gym gym, Schedule schedule, Member member, int attend) {
        this.gym = gym;
        this.schedule = schedule;
        this.member = member;
        this.date = LocalDateTime.now();
        this.attend = attend;
    }
}
