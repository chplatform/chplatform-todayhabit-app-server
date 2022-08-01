package todayHabit.todayHabitApp.repository.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.dto.gym.GymMembershipCountDto;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GymContainMemberRepository {
    private final EntityManager em;

    public List<GymContainMember> findByMemberId(Long memberId) {
        return em.createQuery("select gcm from GymContainMember gcm " +
                        " join gcm.member m " +
                        " where m.id = :memberId")
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<GymContainMember> findByGymIdWithMemberId(Long memberId, Long gymId) {
        return Optional.ofNullable(em.createQuery("select gcm from GymContainMember gcm " +
                        " join gcm.member m" +
                        " join gcm.gym g " +
                        " where m.id = :memberId" +
                        " and g.id = :gymId", GymContainMember.class)
                .setParameter("memberId", memberId)
                .setParameter("gymId", gymId)
                .getSingleResult());
    }

}
