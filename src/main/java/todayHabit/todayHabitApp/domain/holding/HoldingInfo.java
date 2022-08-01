package todayHabit.todayHabitApp.domain.holding;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@ToString
@Entity
@Getter
@Table(name = "holding_info")
public class HoldingInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "holding_id")
    private Long holdingId;
	
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GYM_id")
    private Gym gym;
	
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_membership_id")
    private MemberOwnMembership memberOwnMembership;
	
	@Column(name = "member_id")
    private Long memberId;
	
	@Column(name = "req_type")
    private String reqType;
	
	@Column(name = "req_use")
    private String reqUse;
	
	@Column(name = "hold_start_day")
    private LocalDate holdStartDay;
	
	@Column(name = "hold_end_day")
    private LocalDate holdEndDay;
	
	@Column(name = "hold_total_period")
    private Long holdTotalPeriod;
	
	@Column(name = "hold_use_period")
    private Long holdUsePeriod;

    private String memo;
    
    public String getAvailable() {
        int compare = this.memberOwnMembership.getEndDay().compareTo(LocalDate.now());
        LocalDate today = LocalDate.now();
        
        // 홀딩권이 있을 경우
        if(this.getReqUse().equals("Y")) {
            if (today.compareTo(this.getHoldStartDay()) >= 0 && today.compareTo(this.getHoldEndDay()) <= 0) {
                return "use"; // 홀딩권 사용중
            }else if(today.compareTo(this.getHoldEndDay()) > 0){
            	return "Expired"; //홀딩권 기간 만료
            }
        }else { // 홀딩권 미사용
        	return "notUse";
        }
            
        return "notFound";
    }
	
    protected HoldingInfo() {

    }

	public HoldingInfo(Long holdingId, Gym gym, MemberOwnMembership memberOwnMembership, Long memberId, String reqType,
			String reqUse, LocalDate holdStartDay, LocalDate holdEndDay, Long holdTotalPeriod, Long holdUsePeriod,
			String memo) {
		this.holdingId = holdingId;
		this.gym = gym;
		this.memberOwnMembership = memberOwnMembership;
		this.memberId = memberId;
		this.reqType = reqType;
		this.reqUse = reqUse;
		this.holdStartDay = holdStartDay;
		this.holdEndDay = holdEndDay;
		this.holdTotalPeriod = holdTotalPeriod;
		this.holdUsePeriod = holdUsePeriod;
		this.memo = memo;
	}





    
    

}
