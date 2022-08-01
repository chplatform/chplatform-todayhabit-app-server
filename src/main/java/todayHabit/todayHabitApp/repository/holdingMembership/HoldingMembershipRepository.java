package todayHabit.todayHabitApp.repository.holdingMembership;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.holding.HoldingMembership;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class HoldingMembershipRepository {
    private final EntityManager em;

    public HoldingMembership findById(Long id) {
        return em.find(HoldingMembership.class, id);
    }

}
