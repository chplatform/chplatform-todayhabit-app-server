package todayHabit.todayHabitApp.domain.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.holding.HoldingInfo;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingLocation;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.membership.Membership;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Table(name = "member_membership")
public class MemberOwnMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    @JsonIgnore
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id",insertable = false,updatable = false)
    @JsonIgnore
    private Membership membership;

    @JsonIgnore
    @OneToMany(mappedBy = "memberOwnMembership")
    private List<MemberOwnMembershipClassType> membershipClassTypes;

//    @JsonIgnore
//    @OneToMany(mappedBy = "memberOwnMembership")
//    @BatchSize(size = 10)
//    private List<HoldingMembership> holdingMemberships;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "memberOwnMembership")
//    @BatchSize(size = 10)
//    private List<HoldingList> holdingList;
    
    @OneToMany(mappedBy = "memberOwnMembership", fetch = FetchType.LAZY)
    private List<HoldingInfo> holdingInfo;

    @Column(name = "membership_name")
    private String name;
    private LocalDate startDay;

    private LocalDate endDay;
    private int countClass;
    private int payment;
    private LocalDate paymentDay;
    private int cash;
    private int card;
    private int accountReceivable;
    private int DayAttend;
    private int weekAttend;
    private LocalDateTime registerDate;
    private int maxCountClass;

    @Transient
    private LocalDate holdingStartDay;
    @Transient
    private LocalDate holdingEndDay;

    public String getAvailable() {
        int compare = this.endDay.compareTo(LocalDate.now());
        LocalDate today = LocalDate.now();
        List<HoldingInfo> holdingInfo = this.holdingInfo;
        
        // 회원권 기간 만료
        if (compare < 0 || (this.maxCountClass <= this.countClass)) {
            return "Expired";
        }
        
        // 홀딩권이 있을 경우
        if(!holdingInfo.isEmpty()) {
            for (HoldingInfo list : holdingInfo) {
            	// 홀딩권 사용중 or 홀딩권 기간 만료
                if(list.getReqUse().equals("Y")) {
                    if (today.compareTo(list.getHoldStartDay()) >= 0 && today.compareTo(list.getHoldEndDay()) <= 0) {
                        this.holdingStartDay = list.getHoldStartDay();
                        this.holdingEndDay = list.getHoldEndDay();
                        return "use"; // 홀딩권 사용중
                    }else if(today.compareTo(list.getHoldEndDay()) > 0){
                    	return "Expired"; //홀딩권 기간 만료
                    }
                }else { // 홀딩권 미사용
                	return "notUse";
                }
                
 
            }
        }
            
        return "notFound";
    }

    public boolean increaseMembership(int count) {
        if (this.countClass + count > this.maxCountClass) {
            return false;
        }
        this.countClass += count;
        return true;
    }

    public void decreaseMembership(int count) {
        this.countClass -= count;
    }

    public void increaseAttend() {
        this.DayAttend += 1;
        this.weekAttend += 1;
    }

    public void decreaseAttend() {
        this.DayAttend -= 1;
        this.weekAttend -= 1;
    }

    public void increaseMembershipEndDay(int period) {
        System.out.println("endDay: " + this.endDay.plusDays(period));
        this.endDay = this.endDay.plusDays(period);
    }
    public void decreaseMembershipEndDay(int period) {
        this.endDay = this.endDay.minusDays(period);
    }
}

