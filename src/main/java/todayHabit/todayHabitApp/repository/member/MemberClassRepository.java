package todayHabit.todayHabitApp.repository.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.member.MemberClass;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberClassRepository {

    private final EntityManager em;

    public List<MemberClass> findByMemberOwnMembershipId(LocalDate date, Long gymId, Long membershipId) {
        return em.createQuery("select mc from MemberClass mc " +
                        " join fetch mc.schedule c " +
                        " join mc.memberOwnMembership mom " +
                        " join mc.gym g " +
                        " where g.id = :gymId " +
                        " and mom.id = :membershipId" +
                        " and EXTRACT(month from c.startDay) = :month " , MemberClass.class)
                .setParameter("gymId", gymId)
                .setParameter("membershipId", membershipId)
                .setParameter("month", date.getMonthValue())
                .getResultList();
    }

    @Transactional
    public void save(MemberClass memberClass) {
        em.persist(memberClass);
    }

    public List<MemberClass> findByMemberIdWithClassId(Long memberId, Long classId) {
        return em.createQuery("select mc from MemberClass mc " +
                        " join mc.member m " +
                        " join mc.gym g " +
                        " join mc.schedule s " +
                        " where m.id = :memberId" +
                        " and s.id = :classId")
                .setParameter("memberId", memberId)
                .setParameter("classId", classId)
                .getResultList();
    }

    @Transactional
    public void deleteById(MemberClass memberClass) {
        em.remove(memberClass);
    }

    public List<MemberClass> findBetweenDate(Long gymId, LocalDate startDay, LocalDate endDay, Long membershipId) {
        return em.createQuery("select mc from MemberClass mc " +
                        " join fetch mc.schedule s " +
                        " join mc.memberOwnMembership mom " +
                        " where mom.id = :membershipId " +
                        " and s.startDay between :startDay and :endDay ", MemberClass.class)
                .setParameter("membershipId", membershipId)
                .setParameter("startDay", startDay)
                .setParameter("endDay", endDay)
                .getResultList();
    }

    public List<BigInteger> findByMemberIdWithClassIdAndDay(Long memberId, Long membershipId, LocalDate startDay) {
        return em.createNativeQuery("select coalesce(sum(c.decrease),0) from member_class mc" +
                        " join class c on mc.class_id = c.class_id" +
                        " where date_format(c.startDay, '%y-%m-%d') = date_format(?, '%y-%m-%d')" +
                        " and mc.member_id = ?" +
                        " and mc.member_membership_id = ?")
                .setParameter(1, startDay)
                .setParameter(2, memberId)
                .setParameter(3, membershipId)
                .getResultList();
    }

    public List<BigInteger>  findByMemberIdWithClassIdAndWeek(Long memberId, Long membershipId, LocalDate startDay) {
        return em.createNativeQuery("select coalesce(sum(c.decrease),0) from member_class mc" +
                        " join class c on mc.class_id = c.class_id" +
                        " where week(c.startDay) = week(?)" +
                        " and mc.member_id = ?" +
                        " and mc.member_membership_id = ?")
                .setParameter(1, startDay)
                .setParameter(2, memberId)
                .setParameter(3, membershipId)
                .getResultList();
    }
}
