package todayHabit.todayHabitApp.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.member.Male;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GymServiceTest {

    @Autowired
    GymService gymService;
    @Autowired
    MemberService memberService;
    @Autowired
    EntityManager em;

    @Test
    public void 시리얼넘버체크() throws Exception{
        //given
        Member member = new Member("회원1", "tw4@naver.com", "19971109", Male.남, "01011112222", "111", "sample");
        memberService.joinMember(member);
        em.flush();
        em.clear();
        //when
        String gymName = gymService.checkGymSerialNUmber(member.getId(), "ft1333");
        //then
        Assert.assertEquals(gymName,"GoodHabit 1");
    }
}