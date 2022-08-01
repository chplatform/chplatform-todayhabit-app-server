package todayHabit.todayHabitApp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import todayHabit.todayHabitApp.domain.gym.Gym;

import javax.persistence.EntityManager;

@SpringBootTest
public class GymTest {

    @Autowired
    EntityManager em;
    @Test
    public void GymSelect() throws Exception{
        //given
        Gym singleResult = em.createQuery("select g from Gym g where g.id = 5", Gym.class)
                .getSingleResult();

        System.out.println("singleResult.toString() = " + singleResult.toString());
        //when
        
        //then

    }
}
