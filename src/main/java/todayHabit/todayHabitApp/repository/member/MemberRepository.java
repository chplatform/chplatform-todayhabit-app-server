package todayHabit.todayHabitApp.repository.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.member.Status;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRepository {
    private final EntityManager em;

    @Transactional
    public void saveMember(Member member) {
        em.persist(member);
    }

    public Member findMemberById(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findMemberByEmail(String email) {
        return em.createQuery("select m from Member m " +
                        " where m.email = :email ", Member.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<Member> findMemberByIdWithEveryData(Long memberId) {
        return em.createQuery("select m from Member m " +
                        " left join fetch m.gym g " +
                        " where m.id = :memberId" , Member.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<MemberOwnMembership> findMemberOwnMembershipByGymId(Long memberId, Long gymId) {
        return em.createQuery("select distinct mom from MemberOwnMembership mom " +
                        " join mom.gym g" +
                        " join mom.member mb " +
                        " left join fetch mom.membership m " +
                        " where g.id = :gymId " +
                        " and mb.id = :memberId" +
                        " order by mom.endDay desc", MemberOwnMembership.class)
                .setParameter("gymId", gymId)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
