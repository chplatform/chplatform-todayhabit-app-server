package todayHabit.todayHabitApp.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.schedule.Schedule;
import todayHabit.todayHabitApp.dto.schedule.ClassListDto;
import todayHabit.todayHabitApp.dto.schedule.DayClassDto;
import todayHabit.todayHabitApp.error.AlreadyReserveClassException;
import todayHabit.todayHabitApp.error.TimeoutCancelException;
import todayHabit.todayHabitApp.error.TimeoutReserveException;
import todayHabit.todayHabitApp.repository.schedule.ClassRepository;
import todayHabit.todayHabitApp.repository.member.MemberClassRepository;
import todayHabit.todayHabitApp.repository.member.MemberOwnMembershipRepository;
import todayHabit.todayHabitApp.repository.WaitingMemberRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


@SpringBootTest
@Transactional
class ClassServiceTest {

    @Autowired
    ClassService classService;
    @Autowired
    ClassRepository classRepository;
    @Autowired
    MemberOwnMembershipRepository memberOwnMembershipRepository;
    @Autowired
    MemberClassRepository memberClassRepository;
    @Autowired
    WaitingMemberRepository waitingMemberRepository;
    @Autowired
    EntityManager em;

    @Test
    public void 수업예약_예약성공() throws Exception{
        //given
        //when
        classService.reserveClass(193l,5l,111l,60700l);
        em.flush();
        em.clear();
        //then
        Schedule findClass = classRepository.findById(60700l);
        //MemberOwnMembership findMembership = memberOwnMembershipRepository.findById(111l);
        List<MemberClass> memberClassList = memberClassRepository.findByMemberIdWithClassId(193l, 60700l);
        assertEquals(findClass.getReserveNumber(),1);
        //assertEquals(findMembership.getCountClass(), 6);
        assertEquals(memberClassList.size(), 1);
    }

    @Test
    public void 수업예약_에약시간불가() throws Exception{
        //given
        //when
        //then
        assertThrows(TimeoutReserveException.class, () -> {
            classService.reserveClass(193l, 5l, 111l, 60674l);
        });
    }

    @Test
    public void 수업예약_대기인원() throws Exception{
        //given

        //when
        classService.reserveClass(193l, 5l, 111l, 60700l);
        classService.reserveClass(248l, 5l, 114l, 60700l);
        em.flush();
        em.clear();

        //then
        //MemberOwnMembership findMembership = memberOwnMembershipRepository.findById(114l);
        List<WaitingMember> byMemberIdWithClassId = waitingMemberRepository.findByMemberIdWithClassId(248l, 60700l);
        //assertEquals(findMembership.getCountClass(),7);
        assertEquals(byMemberIdWithClassId.size(), 1);
    }

    @Test
    public void 수업예약_이미예약된회원() throws Exception{
        //given
        classService.reserveClass(193l, 5l, 111l, 60700l);
        //when

        //then
        assertThrows(AlreadyReserveClassException.class, () -> {
            classService.reserveClass(193l, 5l, 111l, 60700l);
        });
    }

    @Test
    public void 예약취소_취소가능시간불가() throws Exception{
        //given
        //when
        //then
        assertThrows(TimeoutCancelException.class, () -> {
            classService.cancelClass(193l, 5l, 111l, 60675l);
        });
    }

    @Test
    public void 예약회원취소_성공() throws Exception{
        //given
        classService.reserveClass(193l, 5l, 111l, 60700l);
        //when
        classService.cancelClass(193l, 5l, 111l, 60700l);
        //then
        List<MemberClass> findClass = memberClassRepository.findByMemberIdWithClassId(248l, 60700l);
        //MemberOwnMembership membership = memberOwnMembershipRepository.findById(111l);
        assertEquals(findClass.size(), 0);
        //assertEquals(membership.getCountClass(), 5);
    }

    @Test
    public void 대기회원취소_성공() throws Exception{
        //given
        classService.reserveClass(193l, 5l, 111l, 60700l);
        classService.reserveClass(248l, 5l, 114l, 60700l);

        //when
        classService.cancelClass(248l, 5l, 114l, 60700l);
        //then
        List<WaitingMember> waitingMemberList = waitingMemberRepository.findByClassId(60700l);
        //MemberOwnMembership membership = memberOwnMembershipRepository.findById(114l);
        assertEquals(waitingMemberList.size(),0);
        //assertEquals(membership.getCountClass(), 6);
    }

    @Test
    public void 오늘예약수업조사() throws Exception{
        //given
        classService.reserveClass(193l, 5l, 111l, 61549l);
        //when
        List<DayClassDto> dayClassDtos = classService.memberReserveClass(5l, 193l);
        //then
        assertEquals(dayClassDtos.size(),1);
    }

    @Test
    public void 이번달수업목록가져오기() throws Exception{
        //given
        //when
        ClassListDto classList = classService.getClassList(LocalDate.now(), 5l, 111l);
        //then
        assertEquals(classList.getClassList().size(),5);
        assertEquals(classList.getReserveBlockList().size(),1);

    }
}