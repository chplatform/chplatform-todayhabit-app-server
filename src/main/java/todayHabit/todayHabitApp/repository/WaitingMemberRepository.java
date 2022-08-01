package todayHabit.todayHabitApp.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.WaitingMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WaitingMemberRepository {

    public final EntityManager em;

    public Optional<Integer> findMaxWaitingNumber(Long classId) {
        return Optional.ofNullable(em.createQuery("select max(w.waitingNumber) from WaitingMember w " +
                        " join w.schedule s " +
                        " where s.id = :classId ", Integer.class)
                .setParameter("classId", classId)
                .getSingleResult());
    }

    public List<WaitingMember> findByMemberIdWithClassId(Long memberId, Long classId) {
        return em.createQuery("select w from WaitingMember w " +
                        " join w.member m " +
                        " join w.schedule s " +
                        " where m.id = :memberId" +
                        " and s.id = :classId", WaitingMember.class)
                .setParameter("memberId", memberId)
                .setParameter("classId", classId)
                .getResultList();
    }
    public List<WaitingMember> findByClassId(Long classId) {
        return em.createQuery("select w from WaitingMember w " +
                        " join w.member m " +
                        " join w.schedule s " +
                        " where s.id = :classId", WaitingMember.class)
                .setParameter("classId", classId)
                .getResultList();
    }

    public void save(WaitingMember waitingMember) {
        em.persist(waitingMember);
    }

    public void deleteById(WaitingMember waitingMember) {
        em.remove(waitingMember);
    }

    public List<WaitingMember>  findByBehindWaitingMember(Long classId, int waitingNumber) {
        return em.createQuery("select w from WaitingMember w" +
                        " join w.schedule s" +
                        " where s.id = :classId " +
                        " and w.waitingNumber > :waitingNumber")
                .setParameter("classId", classId)
                .setParameter("waitingNumber", waitingNumber)
                .getResultList();
    }

    public List<WaitingMember> findByMonthAndMembershipId(LocalDate date, Long gymId, Long membershipId) {
        return em.createQuery("select w from WaitingMember w " +
                        " join fetch w.schedule s " +
                        " join w.gym g " +
                        " join w.memberOwnMembership mom " +
                        " where g.id = :gymId " +
                        " and mom.id = :membershipId " +
                        " and EXTRACT(month from s.startDay) = :month", WaitingMember.class)
                .setParameter("gymId", gymId)
                .setParameter("membershipId", membershipId)
                .setParameter("month", date.getMonthValue())
                .getResultList();
    }

    public void deleteByClassId(Long classId) {
        em.createQuery("delete from WaitingMember w " +
                        " join w.schedule s " +
                        " where s.id = :classId")
                .setParameter("classId", classId);
    }

    public List<WaitingMember> findByGymIdAndConfirmTime(Long gymId, int reserveConfirmTimeValue) {
        return em.createNativeQuery("select * from waitingMember wm " +
                        " join member_membership mom on wm.member_membership_id = mom.id" +
                        " join class c on wm.class_id = c.class_id" +
                        " where c.GYM_id = ? " +
                        " and (c.startDay = ? " +
                        " and date_sub(c.startTime, interval ? minute) <= ?)" +
                        " group by c.GYM_id", WaitingMember.class)
                .setParameter(1, gymId)
                .setParameter(2, LocalDate.now())
                .setParameter(3, reserveConfirmTimeValue)
                .setParameter(4, LocalTime.now())
                .getResultList();
//        return em.createQuery("select wm from WaitingMember wm" +
//                        " join fetch wm.memberOwnMembership mom " +
//                        " join wm.gym g " +
//                        " join fetch wm.schedule s " +
//                        " where g.id = :gymId" +
//                        " and (s.startDay = :startDay" +
//                        " and date_sub(s.startTime, interval :reserveConfirmTime minutes) <= :nowTime)", WaitingMember.class)
//                .setParameter("gymId", gymId)
//                .setParameter("startDay", LocalDate.now())
//                .setParameter("reserveConfirmTime", reserveConfirmTimeValue)
//                .setParameter("nowTime", LocalTime.now())
//                .getResultList();
    }

    public List<WaitingMember> findByDateAndMemberIdAndGymId(Long memberId, Long membershipId) {
        return em.createQuery("select wm from WaitingMember wm" +
                        " join fetch wm.schedule s " +
                        " join wm.memberOwnMembership mom " +
                        " join wm.member m " +
                        " where m.id = :memberId " +
                        " and mom.id = :membershipId")
                .setParameter("memberId", memberId)
                .setParameter("membershipId", membershipId)
                .getResultList();
    }
}
