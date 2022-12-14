package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import todayHabit.todayHabitApp.domain.WaitingMember;
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
import todayHabit.todayHabitApp.repository.WaitingMemberRepository;
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
    private final WaitingMemberRepository waitingMemberRepository;
    

    public int checkHoldingMembership(Long gymId, Long membershipId, LocalDate startDay, LocalDate endDay) {
        int classCount = memberClassRepository.findBetweenDate(gymId, startDay, endDay, membershipId).size();
        int waitClassCount= waitingMemberRepository.findBetweenDate(gymId, startDay, endDay, membershipId).size();
        return classCount + waitClassCount;
    }

    @Transactional
    public String holdingMembership(Long membershipId, Long holdingId, LocalDate startDay, LocalDate endDay, String memo) throws Exception{	
        
    	/*
         * 1. ???????????? ????????? ?????????????????? ???????????? ??????
         * 2. ???????????? ????????? ?????????????????? ???????????? ??????
         * 3. ??????????????? ?????? ????????? ??????????????? ??????
         * 4. ????????? ?????? ????????? ????????????
         * 5. ?????? ?????? ?????? ????????? ??????
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
            return "????????? ?????????????????????.";
        }
    }  

    @Transactional
    public String cancelHoldingMembership(Long membershipId, Long holdingId) throws Exception{

        /*
        * 1. ?????? ???????????? ????????? ??? ??????
        * 2. ???????????? ???????????? ??? ?????? ????????? ??????
        * 3. ???????????? ???????????? ??? ?????? ?????? ??????
        * 4. ?????? ????????? ????????? ?????? ??? ??? ?????? ?????? ????????????????????? ?????????.
        * 5. ?????? ????????? ????????? ?????? ??????
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

        if(today.isBefore(holdingInfo.getHoldStartDay())) {
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
        	holdingInfo.updateHoldEndDay(today.minusDays(1));
        };
            
        return "?????? ????????? ?????????????????????.";
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
