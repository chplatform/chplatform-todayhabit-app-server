package todayHabit.todayHabitApp.domain.holding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(name = "holdingMembership")
public class HoldingMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holdingMembership_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_membership_id")
    private MemberOwnMembership memberOwnMembership;

    @JsonIgnore
    @OneToMany(mappedBy = "holdingMembership")
    private List<HoldingList> holdingList;

    private int holdingPeriod;
    private int usingPeriod;

    @Enumerated(value = EnumType.STRING)
    private HoldingStatus deleteValue;

    public LocalDate getStartDay() {
        return this.getHoldingList().get(0).getStartDay();
    }

    public LocalDate getEndDay() {
        return this.getHoldingList().get(0).getEndDay();
    }
    public void changeStatusUsingDeleteValue() {
        this.deleteValue = HoldingStatus.완료;
    }

    public void changeStatusCancelDeleteValue() {
        this.deleteValue = HoldingStatus.취소;
    }

    public void setUsingPeriod(int usingPeriod) {
        this.usingPeriod = usingPeriod;
    }


    public HoldingStatus getHoldingStatus() {
        if(!this.getHoldingList().isEmpty()){ //사용했던 홀딩권 중(완료)
            if(this.getEndDay().isBefore(LocalDate.now()) ) { //홀딩이 끝난 회원권
                return HoldingStatus.만료;
            }else{
                return HoldingStatus.사용중;
            }
        }
        return this.deleteValue;
    }
}
