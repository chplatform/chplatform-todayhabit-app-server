package todayHabit.todayHabitApp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
public class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void 회원출력() throws Exception{
        //given
        Member singleResult = em.createQuery("select m from Member m where member_id = 140", Member.class)
                .getSingleResult();

        //when
        Gym gym = singleResult.getGym();
        //then
        System.out.println("-----------------------------------");
        System.out.println("gym = " + gym.getClass());
    }
}