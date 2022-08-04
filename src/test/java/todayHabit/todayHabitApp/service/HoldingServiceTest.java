package todayHabit.todayHabitApp.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.error.OutOfStartDayMembershipPeriodException;
import todayHabit.todayHabitApp.repository.holdingMembership.HoldingListRepository;
import todayHabit.todayHabitApp.repository.holdingMembership.HoldingMembershipRepository;
import todayHabit.todayHabitApp.repository.member.MemberOwnMembershipRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static java.util.Optional.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
class HoldingServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private HoldingService holdingService;
    @Autowired
    private MemberOwnMembershipRepository memberOwnMembershipRepository;
    @Autowired
    private HoldingListRepository holdingListRepository;
    @Autowired
    private HoldingMembershipRepository holdingMembershipRepository;

    @Test
    public void 기간내홀딩_수업개수반환() throws Exception{
        //given

        //when
        int classList = holdingService.checkHoldingMembership(5l, 124l,
                LocalDate.of(2022, 02, 27), LocalDate.of(2022, 03, 01));
        //then
        Assert.assertEquals(classList,1);
    }

    @Test
    public void 회원권_홀딩() throws Exception{
        //given
        //when
        //MemberOwnMembership beforeMembershipInfo = memberOwnMembershipRepository.findById(124l);
//        holdingService.holdingMembership(124l, 161l,
//                LocalDate.of(2022, 02, 10), LocalDate.of(2022, 02, 12));
        em.flush();
        em.clear();
        //then
        //MemberOwnMembership afterMembershipInfo = memberOwnMembershipRepository.findById(124l);
        //Period period = beforeMembershipInfo.getEndDay().until(afterMembershipInfo.getEndDay());
        Period holdingPeriod = LocalDate.of(2022, 02, 10).until(LocalDate.of(2022, 02, 12));
        HoldingList holdingList = holdingListRepository.findByHoldingMembershipId(161l);
        //HoldingMembership holdingMembership = holdingMembershipRepository.findById(161l);
        Assert.assertEquals(ofNullable(holdingList.getHoldingMembership().getId()), Optional.of(161l));
        //Assert.assertEquals(holdingMembership.getHoldingStatus(), HoldingStatus.완료);
    }

    @Test
    public void 홀딩_회원권기간밖() throws Exception{
        //given
        //when
        //then
        assertThrows(OutOfStartDayMembershipPeriodException.class, () -> {
//            holdingService.holdingMembership(124l, 161l,
//                    LocalDate.of(2022, 05, 10), LocalDate.of(2022, 05, 12));
        });
    }
    
    @Test
    public void 홀딩취소() throws Exception{
        /*
        * 홀딩권 취소처리되었는지
        * 회원권 날짜원상복구 되었는지 확인
        * */
        //given
        //MemberOwnMembership beforeMembershipInfo = memberOwnMembershipRepository.findById(124l);
//        holdingService.holdingMembership(124l, 161l,
//                LocalDate.of(2022, 02, 10), LocalDate.of(2022, 02, 12));
        //when
        holdingService.cancelHoldingMembership(124l, 161l);
        em.flush();
        em.clear();
        //then
//        MemberOwnMembership afterMembershipInfo = memberOwnMembershipRepository.findById(124l);
//        HoldingMembership findHoldingMembership = holdingMembershipRepository.findById(161l);
        //assertEquals(beforeMembershipInfo.getEndDay(), afterMembershipInfo.getEndDay());
    }
}