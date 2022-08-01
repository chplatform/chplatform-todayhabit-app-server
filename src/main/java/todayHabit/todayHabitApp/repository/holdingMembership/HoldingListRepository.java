package todayHabit.todayHabitApp.repository.holdingMembership;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HoldingListRepository {

    private final EntityManager em;

    @Transactional
    public void save(HoldingList holdingList) {
        em.persist(holdingList);
    }

    public HoldingList findByHoldingMembershipId(long id) {
        return em.createQuery("select hl from HoldingList hl " +
                        " join hl.holdingMembership h" +
                        " where h.id = :id ",HoldingList.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    @Transactional
    public void deleteByHoldingMembershipId(HoldingList holdingList) {
        em.remove(holdingList);
    }

    public List<HoldingList> findByMembershipIdAndStartDayAndEndDay(Long membershipId, LocalDate startDay, LocalDate endDay) {
        return em.createNativeQuery("select * FROM holdingList hl " +
                " left join holdingMembership hm on hl.holdingMembership_id = hm.holdingMembership_id " +
                " where hl.member_membership_id = ? " +
                " and hm.deleteValue != ?", HoldingList.class)
                .setParameter(1, membershipId)
                .setParameter(2, "취소")
                .getResultList();
    }

    public List<HoldingList> findByHoldingMembershipIdZero(Long membershipId, LocalDate startDay, LocalDate endDay) {
        return em.createNativeQuery("SELECT * FROM holdingList hl " +
                        " where hl.holdingMembership_id = ? and hl.member_membership_id = ? ", HoldingList.class)
                .setParameter(1, 0)
                .setParameter(2, membershipId)
                .getResultList();
    }
}
