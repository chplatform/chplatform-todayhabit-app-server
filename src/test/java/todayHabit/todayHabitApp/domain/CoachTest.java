package todayHabit.todayHabitApp.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import todayHabit.todayHabitApp.domain.coach.Coach;
import todayHabit.todayHabitApp.repository.CoachRepository;
import static org.junit.Assert.assertEquals;

@SpringBootTest
class CoachTest {

    @Autowired
    CoachRepository coachRepository;

    @Test
    public void 코치정보불러오기() throws Exception{
        //given
        //when
        Coach findCoach = coachRepository.findById(229L);
        //then
        assertEquals(findCoach.getId().longValue(), 229L);
    }
}