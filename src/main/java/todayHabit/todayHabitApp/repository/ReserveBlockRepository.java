package todayHabit.todayHabitApp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.ReserveBlock;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReserveBlockRepository {
    private final EntityManager em;

    // 사용 무
    public List<ReserveBlock> findByStartDay(LocalDate date) {
        return em.createQuery("select r from ReserveBlock r " +
                        " where r.startDay = :startDay")
                .setParameter("startDay", date)
                .getResultList();
    }

    public List<ReserveBlock> findByMonthAndGymId(LocalDate date, Long gymId) {
        return em.createQuery("select r from ReserveBlock r" +
                        " join r.gym g " +
                        " where g.id = :gymId" +
                        " and EXTRACT(month from r.startDay) = :month" +
                        " and r.startTime = :startTime" +
                        " and r.endTime = :endTime")
                .setParameter("gymId", gymId)
                .setParameter("month", date.getMonthValue())
                .setParameter("startTime", LocalTime.of(00,00,00))
                .setParameter("endTime", LocalTime.of(23,59,59))
                .getResultList();
    }
}
