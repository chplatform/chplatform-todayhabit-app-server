package todayHabit.todayHabitApp.domain.gym;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "gym_member")
@NoArgsConstructor
public class GymContainMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Enumerated(EnumType.STRING)
    private GymBookmark bookmark;

    @Enumerated(EnumType.STRING)
    private ApproveStatus approve;

    public GymContainMember(Gym gym, Member member) {
        this.gym = gym;
        this.member = member;
        this.bookmark = GymBookmark.일반;
        this.approve = ApproveStatus.승인대기;
    }

    public void BookmarkGym() {
        this.bookmark = GymBookmark.즐겨찾기;
    }

    public void UnBookmarkGym() {
        this.bookmark = GymBookmark.일반;
    }
}
