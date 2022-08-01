package todayHabit.todayHabitApp.repository.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.dto.gym.GymMembershipCountDto;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberOwnMembershipRepository {
    private final EntityManager em;

    // 미 사용
    public List<MemberOwnMembership> findMemberOwnMembershipFindByMemberId(Long memberId, Long membershipId) {
        return em.createQuery("select mom from MemberOwnMembership mom " +
                        " join mom.member m" +
                        " join mom.membership ms" +
                        " where m.id = :memberId" +
                        " and ms.id = :membershipId")
                .setParameter("memberId", memberId)
                .setParameter("membershipId", membershipId)
                .getResultList();
    }

    public MemberOwnMembership findByIdWithMemberOwnMembership(Long membershipId) {
        return em.createQuery("select mom from MemberOwnMembership mom " +
                        " join fetch mom.membership ms" +
                        " where mom.id = :membershipId", MemberOwnMembership.class)
                .setParameter("membershipId", membershipId)
                .getSingleResult();
    }

//    public MemberOwnMembership findByIdToHolding(Long membershipId) {
//        return em.createQuery("select mom from MemberOwnMembership mom " +
//                        " join fetch mom.membership ms" +
//                        " where mom.id = :membershipId", MemberOwnMembership.class)
//                .setParameter("membershipId", membershipId)
//                .getSingleResult();
//    }

    public List<Object[]> findByIdWithEndDay(Long memberId) {
        return em.createQuery("select g.id, count(*) from MemberOwnMembership mom" +
                        " join mom.member m " +
                        " join mom.gym g " +
                        " where (mom.countClass < mom.maxCountClass " +
                        " and mom.endDay >= :endDay) " +
                        " and m.id = :memberId" +
                        " group by g.id")
                .setParameter("memberId", memberId)
                .setParameter("endDay", LocalDate.now())
                .getResultList();
    }
}

