package todayHabit.todayHabitApp.repository.holdingMembership;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.domain.holding.HoldingInfo;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.holding.HoldingStatus;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public List<HoldingInfo> findByMembershipId(Long membershipId, LocalDate startDay, LocalDate endDay) {
        return em.createNativeQuery("select * FROM holding_info hi " +
                " where hi.member_membership_id = ? " +
                " and hi.req_use != ?", HoldingInfo.class)
                .setParameter(1, membershipId)
                .setParameter(2, "N")
                .getResultList();
    }
    
    public List<HoldingInfo> findByMembershipIdAndStartDayAndEndDay(Long membershipId, LocalDate startDay, LocalDate endDay) {
        return em.createNativeQuery("select * FROM holding_info hi " +
                " where hi.member_membership_id = :membershipId " +
                " and (" + 
                "(hi.hold_start_day <= :startDay "+
                "and hi.hold_end_day >= :startDay )"
                +")" +
                " and hi.req_use != :reqUse", HoldingInfo.class)
                .setParameter("membershipId", membershipId)
                .setParameter("startDay", startDay)
                .setParameter("startDay", startDay)
                .setParameter("reqUse", "N")
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
