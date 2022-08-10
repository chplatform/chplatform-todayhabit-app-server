package todayHabit.todayHabitApp.dto.member;

import lombok.Data;
import todayHabit.todayHabitApp.domain.holding.HoldingInfo;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingLocation;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembershipClassType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Data
public class MemberOwnMembershipsDto {
    private Long id;
    private String name;
    private LocalDate startDay;
    private LocalDate endDay;
    private int countClass;
    private int payment;
    private int DayAttend;
    private int weekAttend;
    private LocalDateTime registerDate;
    private int maxCountClass;
    private String available;
    private LocalDate holdingStartDay;
    private LocalDate holdingEndDay;
    private List<MembershipClassTypeDto> membershipClassType;
//    private List<HoldingMembershipDto> holdingMembershipDtoList;
//    private List<HoldingListDto> holdingList;
    private List<HoldingInfoDto> holdingInfo;
    public MemberOwnMembershipsDto(MemberOwnMembership memberOwnMembership) {
        this.id = memberOwnMembership.getId();
        this.name = memberOwnMembership.getMembership().getName();
        this.startDay = memberOwnMembership.getStartDay();
        this.endDay = memberOwnMembership.getEndDay();
        this.countClass = memberOwnMembership.getCountClass();
        this.payment = memberOwnMembership.getPayment();
        this.DayAttend = memberOwnMembership.getDayAttend();
        this.weekAttend = memberOwnMembership.getWeekAttend();
        this.registerDate = memberOwnMembership.getRegisterDate();
        this.maxCountClass = memberOwnMembership.getMaxCountClass();
        this.membershipClassType = memberOwnMembership.getMembershipClassTypes().stream()
                .map(membershipClassType -> new MembershipClassTypeDto(membershipClassType))
                .collect(toList());
        this.holdingInfo = memberOwnMembership.getHoldingInfo().stream()
                .map(holdingLists -> new HoldingInfoDto(holdingLists))
                .collect(toList());
    }

    @Data
    static class MembershipClassTypeDto{
        private String name;

        public MembershipClassTypeDto(MemberOwnMembershipClassType memberOwnMembershipClassType){
            this.name = memberOwnMembershipClassType.getClassTypeName();
        }
    }

    @Data
    private static class HoldingMembershipDto {
        private Long id;
        private Long membershipId;
        private int holdingPeriod;
        private int usingPeriod;
        private HoldingStatus deleteValue;
        public HoldingMembershipDto(HoldingMembership holdingMembership) {
            this.id = holdingMembership.getId();
            Optional<MemberOwnMembership> memberOwnMembership = Optional.ofNullable(holdingMembership.getMemberOwnMembership());
            if(!memberOwnMembership.isEmpty()){
                this.membershipId = memberOwnMembership.get().getId();
            }
            this.holdingPeriod = holdingMembership.getHoldingPeriod();
            this.usingPeriod = holdingMembership.getUsingPeriod();
            if(holdingMembership.getDeleteValue().equals(HoldingStatus.취소)){
                this.deleteValue = holdingMembership.getDeleteValue();
            }else{
                this.deleteValue = holdingMembership.getHoldingStatus();
            }
        }
    }

    @Data
    private class HoldingListDto {
        private Long id;
        private Long holdingMembershipId;
        private Long membershipId;
        private LocalDate startDay;
        private LocalDate endDay;
        private HoldingLocation holdingLocation;

        public HoldingListDto(HoldingList holdingList) {
            this.id = holdingList.getId();
            if(holdingList.getHoldingMembership() != null){
                this.holdingMembershipId = holdingList.getHoldingMembership().getId();
            }
            this.membershipId = holdingList.getMemberOwnMembership().getId();
            this.startDay = holdingList.getStartDay();
            this.endDay = holdingList.getEndDay();
            this.holdingLocation = holdingList.getHoldingLocation();
        }
    }
    
    
    @Data
    private class HoldingInfoDto {

        private Long holdingId;
        private Long membershipId;  	
        private Long memberId;
        private String reqType;
        private String reqUse;
        private String reqCancel;
        private LocalDate holdStartDay;
        private LocalDate holdEndDay;
        private int holdTotalPeriod;
        private int holdUsePeriod;
        private String available;
        private int realUseHoldPeriod;
        private String memo;
        private LocalDate updateDate;
        private LocalDate cancelDate;

		public HoldingInfoDto(HoldingInfo holdingLists) {
			this.holdingId = holdingLists.getHoldingId();
			this.membershipId = holdingLists.getMemberOwnMembership().getId();
			this.memberId = holdingLists.getMemberId();
			this.reqType = holdingLists.getReqType();
			this.reqUse = holdingLists.getReqUse();
			this.reqCancel = holdingLists.getReqCancel();
			this.holdStartDay = holdingLists.getHoldStartDay();
			this.holdEndDay = holdingLists.getHoldEndDay();
			this.holdTotalPeriod = holdingLists.getHoldTotalPeriod();
			this.holdUsePeriod = holdingLists.getHoldUsePeriod();
			this.memo = holdingLists.getMemo();
	        this.available = holdingLists.getAvailable();
	        this.realUseHoldPeriod = holdingLists.getRealUseHoldPeriod();
	        this.updateDate = holdingLists.getUpdateDate();
	        this.cancelDate = holdingLists.getCancelDate();
		}


    }
}