package todayHabit.todayHabitApp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.coach.Coach;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class CoachRepository {
    private final EntityManager em;

    public Coach findById(Long id) {
        return em.find(Coach.class, id);
    }
}
