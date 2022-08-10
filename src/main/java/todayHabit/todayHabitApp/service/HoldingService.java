package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todayHabit.todayHabitApp.domain.holding.HoldingInfo;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingLocation;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.dto.member.MemberOwnMembershipsDto;
import todayHabit.todayHabitApp.error.*;
import todayHabit.todayHabitApp.repository.holdingMembership.HoldingListRepository;
import todayHabit.todayHabitApp.repository.holdingMembership.HoldingMembershipRepository;
import todayHabit.todayHabitApp.repository.member.MemberClassRepository;
import todayHabit.todayHabitApp.repository.member.MemberOwnMembershipRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HoldingService {

    private final MemberClassRepository memberClassRepository;
    private final MemberOwnMembershipRepository memberOwnMembershipRepository;
    private final HoldingMembershipRepository holdingMembershipRepository;
    private final HoldingListRepository holdingListRepository;

    public int checkHoldingMembership(Long gymId, Long membershipId, LocalDate startDay, LocalDate endDay) {
    	// 홀딩권 req_period 기간만큼 회원권 종료일 수정
        List<MemberClass> classList = memberClassRepository.findBetweenDate(gymId, startDay, endDay, membershipId);
        return classList.size();
    }

    @Transactional
    public String holdingMembership(Long membershipId, Long holdingId, LocalDate startDay, LocalDate endDay, String memo) throws Exception{	
        
    	/*
         * 1. 시작일이 회원권 종료기간보다 안쪽인지 확인
         * 2. 종료일이 회원권 종료기간보다 안쪽인지 확인
         * 3. 사용가능한 홀딩 일수를 초과했는지 확인
         * 4. 홀딩권 사용 상태로 업데이트
         * 5. 홀딩 기간 만큼 회원권 증가
         * */
        MemberOwnMembership membershipInfo = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId); 
        List<HoldingInfo> alreadyHoldingList = holdingListRepository.findByMembershipId(membershipId, startDay, endDay);
        HoldingInfo holdingMembership = holdingMembershipRepository.findById(holdingId);
        Period period = startDay.until(endDay);
        
        if (!holdingMembership.getReqUse().equals("N")) {
            throw new AlreadyUsingHoldingException();
        } else if(holdingMembership.getHoldUsePeriod() + (period.getDays() + 1) > holdingMembership.getHoldTotalPeriod()) {
        	throw new ExpiredHoldingException();
        } else if (!checkHoldingPeriod(alreadyHoldingList, startDay, endDay)) {
            throw new AlreadyHoldingException();
        } else if (startDay.isAfter(membershipInfo.getEndDay())) {
            throw new OutOfStartDayMembershipPeriodException();
        } else {
            holdingMembership.updateHoldStartDay(startDay);
            holdingMembership.updateHoldEndDay(endDay);
            holdingMembership.updateHoldUsePeriod(period.getDays() + 1);
            holdingMembership.updateReqUse("Y");
            holdingMembership.updateMemo(memo);
            membershipInfo.increaseMembershipEndDay(period.getDays() + 1);
            return "홀딩이 완료되었습니다.";
        }
    }  

    @Transactional
    public String cancelHoldingMembership(Long membershipId, Long holdingId) throws Exception{

        /*
        * 1. 남은 일수만큼 회원권 일 감소
        * 2. 사용안된 홀딩권일 때 홀딩 시작일 부터
        * 3. 사용중인 홀딩권일 때 오늘 날짜 부터
        * 4. 홀딩 신청 기간이 회원권 시작 전 일 경우 다시 사용가능하도록 초기화.
        * 5. 홀딩 기간이 남아도 만료 처리
        * */
    	LocalDate today = LocalDate.now();
        HoldingInfo holdingInfo = holdingMembershipRepository.findById(holdingId);
        MemberOwnMembership membershipInfo = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        Period period;
        int decrePeriod = 0;
        
        if (holdingInfo.getHoldStartDay().isAfter(today) && !holdingInfo.getHoldStartDay().isEqual(today)) {
            period = holdingInfo.getHoldStartDay().until(holdingInfo.getHoldEndDay());
            decrePeriod = period.getDays()+1;
        }else{
            period = today.until(holdingInfo.getHoldEndDay());
            decrePeriod = period.getDays()+1;
        }
        membershipInfo.decreaseMembershipEndDay(decrePeriod);

        if(holdingInfo.getHoldStartDay().isBefore(membershipInfo.getStartDay())
        		|| !holdingInfo.getHoldStartDay().isEqual(today)) {
        	holdingInfo.updateHoldStartDay(null);
        	holdingInfo.updateHoldEndDay(null);
        	holdingInfo.updateHoldUsePeriod(0);
        	holdingInfo.updateReqUse("N");
        	holdingInfo.updateMemo(null);
        	holdingInfo.updateUpdateDate(today);
        }else {
        	holdingInfo.updateReqCancel("Y");
        	holdingInfo.updateUpdateDate(today);
        	holdingInfo.updateCancelDate(today);
        	holdingInfo.updateHoldEndDay(today);
        };
            
        return "홀딩 취소가 완료되었습니다.";
    }

    public boolean checkHoldingPeriod(List<HoldingInfo> alreadyHoldingList, LocalDate startDay, LocalDate endDay) {
        for (HoldingInfo holdingList : alreadyHoldingList) {
            if((endDay.isAfter(holdingList.getHoldStartDay()) || endDay.isEqual(holdingList.getHoldEndDay()))
                    && (holdingList.getHoldEndDay().isAfter(startDay) || holdingList.getHoldEndDay().isEqual(startDay))){
                return false;
            }
        }
        return true;
    }
}
