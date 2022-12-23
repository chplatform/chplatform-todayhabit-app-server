package todayHabit.todayHabitApp.repository.member;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import todayHabit.todayHabitApp.domain.member.MemberMembershipHistory;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberMembershipHistoryRepository {

	private final EntityManager em;
	
    @Transactional
    public void save(MemberMembershipHistory memberMembershipHistory) {
        em.persist(memberMembershipHistory);
    }
}
