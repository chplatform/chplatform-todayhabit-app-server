package todayHabit.todayHabitApp.repository.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.schedule.ClassHistory;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassHistoryRepository {

    private final EntityManager em;

    @Transactional
    public void save(ClassHistory classHistory) {
        em.persist(classHistory);
    }
}
