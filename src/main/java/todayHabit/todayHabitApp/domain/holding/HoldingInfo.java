package todayHabit.todayHabitApp.domain.holding;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	
	@Column(name = "req_cancel")
	private String reqCancel;
	
	@Column(name = "hold_start_day")
    private LocalDate holdStartDay;
	
	@Column(name = "hold_end_day")
    private LocalDate holdEndDay;
	
	@Column(name = "hold_total_period")
    private int holdTotalPeriod;
	
	@Column(name = "hold_use_period")
    private int holdUsePeriod;

    private String memo;
    
    public String getAvailable() {
        int compare = this.memberOwnMembership.getEndDay().compareTo(LocalDate.now());
        LocalDate today = LocalDate.now();
        
        // 홀딩권이 있을 경우
        if(this.getReqUse().equals("Y")) {
        	if(today.compareTo(this.getHoldEndDay()) > 0  || this.getReqCancel().equals("Y")) {
        		return "Expired"; //홀딩권 기간 만료
        	} else if (today.compareTo(this.getHoldStartDay()) >= 0 && today.compareTo(this.getHoldEndDay()) <= 0) {
                return "use"; // 홀딩권 사용중
            }
        }else { // 홀딩권 미사용
        	return "notUse";
        }
            
        return "notFound";
    }

    @Builder
	public HoldingInfo(Long holdingId, Gym gym, MemberOwnMembership memberOwnMembership, Long memberId, String reqType,
			String reqUse, String reqCancel, LocalDate holdStartDay, LocalDate holdEndDay, int holdTotalPeriod, int holdUsePeriod,
			String memo) {
		this.holdingId = holdingId;
		this.gym = gym;
		this.memberOwnMembership = memberOwnMembership;
		this.memberId = memberId;
		this.reqType = reqType;
		this.reqUse = reqUse;
		this.reqCancel = reqCancel;
		this.holdStartDay = holdStartDay;
		this.holdEndDay = holdEndDay;
		this.holdTotalPeriod = holdTotalPeriod;
		this.holdUsePeriod = holdUsePeriod;
		this.memo = memo;
	}

    // 홀딩 사용 여부 변경
    public void updateReqType(String reqType) {
        this.reqType = reqType;
    }

    // 홀딩 시작일 변경
    public void updateHoldStartDay(LocalDate holdStartDay) {
        this.holdStartDay = holdStartDay;
    }
    
    // 홀딩 종료일 변경
    public void updateHoldEndDay(LocalDate holdEndDay) {
        this.holdEndDay = holdEndDay;
    }
    
    // 홀딩 사용일수 변경
    public void updateHoldUsePeriod(int holdUsePeriod) {
        this.holdUsePeriod = holdUsePeriod;
    }
    
    // 홀딩 사용구분 변경
    public void updateReqUse(String reqUse) {
        this.reqUse = reqUse;
    }
    
    // 홀딩 취소 요청
    public void updateReqCancel(String reqCancel) {
        this.reqCancel = reqCancel;
    }
    
    // 홀딩 사유 변경
    public void updateMemo(String memo) {
        this.memo = memo;
    }


    
    

}
