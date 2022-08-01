package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingLocation;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
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
        List<MemberClass> classList = memberClassRepository.findBetweenDate(gymId, startDay, endDay, membershipId);
        return classList.size();
    }

    @Transactional
    public String holdingMembership(Long membershipId, Long holdingMembershipId, LocalDate startDay, LocalDate endDay) throws Exception{
        /*
         * 1. 시작일이 회원권 종료기간보다 안쪽인지 확인
         * 2. 종료일이 회원권 종료기간보다 안쪽인지 확인
         * 3. 홀딩권 삭제 상태 변경 후 홀딩 리스트에 저장
         * 4. 홀딩 기간 만큼 회원권 증가
         * */
        MemberOwnMembership membershipInfo = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        List<HoldingList> alreadyHoldingList = holdingListRepository.findByMembershipIdAndStartDayAndEndDay(membershipId, startDay, endDay);
        List<HoldingList> holdingListInWeb = holdingListRepository.findByHoldingMembershipIdZero(membershipId, startDay, endDay);
        HoldingMembership holdingMembership = holdingMembershipRepository.findById(holdingMembershipId);
        if (!holdingMembership.getDeleteValue().equals(HoldingStatus.미사용)) {
            throw new AlreadyUsingHoldingException();
        } else if (!checkHoldingPeriod(alreadyHoldingList, startDay, endDay)) {
            throw new AlreadyHoldingException();
        } else if (!checkHoldingPeriod(holdingListInWeb, startDay, endDay)) {
            throw new AlreadyHoldingException();
        }else if (startDay.isAfter(membershipInfo.getEndDay())) {
            throw new OutOfStartDayMembershipPeriodException();
        } else {
            Period period = startDay.until(endDay);
            HoldingMembership holdingMembershipInfo = holdingMembership;
            holdingMembershipInfo.changeStatusUsingDeleteValue();
            holdingMembershipInfo.setUsingPeriod(period.getDays() + 1);
            HoldingList holdingList = new HoldingList(holdingMembershipInfo, membershipInfo, startDay, endDay, HoldingLocation.APP);
            holdingListRepository.save(holdingList);
            membershipInfo.increaseMembershipEndDay(period.getDays() + 1);
            return "홀딩이 완료되었습니다.";
        }
    }

    @Transactional
    public String cancelHoldingMembership(Long membershipId, Long holdingMembershipId) throws Exception{
        /*
        * 1. 남은 일수만큼 회원권 일 감소
        * 2. 홀딩 리스트 삭제 (보류)
        * 3-1 사용안된 홀딩권일 때 홀딩 시작일 부터
        * 3-2 사용중인 홀딩권일 때 오늘 날짜 부터
        * */
        if(holdingMembershipId.equals(0l)){
            throw new WebHoldingException();
        }
        LocalDate today = LocalDate.now();
        MemberOwnMembership membershipInfo = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        HoldingMembership holdingMembership = holdingMembershipRepository.findById(holdingMembershipId);
        HoldingList holdingList = holdingListRepository.findByHoldingMembershipId(holdingMembershipId);
        holdingMembership.changeStatusCancelDeleteValue();
        Period period;
        if (holdingList.getStartDay().isAfter(today)) { // 사용안된 홀딩권일 때 홀딩 시작일 부터
            period = holdingList.getStartDay().until(holdingList.getEndDay());
        }else{ //사용중인 홀딩권일 때 오늘 날짜 부터
            period = today.until(holdingList.getEndDay());
        }
        membershipInfo.decreaseMembershipEndDay(period.getDays()+1);
        return "홀딩 취소가 완료되었습니다.";
    }

    private boolean checkHoldingPeriod(List<HoldingList> alreadyHoldingList, LocalDate startDay, LocalDate endDay) {
        for (HoldingList holdingList : alreadyHoldingList) {
            if((endDay.isAfter(holdingList.getStartDay()) || endDay.isEqual(holdingList.getStartDay()))
                    && (holdingList.getEndDay().isAfter(startDay) || holdingList.getEndDay().isEqual(startDay))){
                return false;
            }
        }
        return true;
    }
}
