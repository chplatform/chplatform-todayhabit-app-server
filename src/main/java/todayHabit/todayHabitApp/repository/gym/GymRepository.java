package todayHabit.todayHabitApp.repository.gym;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.domain.member.Member;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GymRepository {
    private final EntityManager em;

    public Gym findById(Long id) {
        return em.find(Gym.class, id);
    }

    public Optional<Gym> findGymBySerialNumber(String serialNumber) {
        try{
            System.out.println("serialNumber = " + serialNumber);
           return Optional.ofNullable(em.createQuery("select g from Gym g where g.serialNumber = :serialNumber", Gym.class)
                    .setParameter("serialNumber", serialNumber)
                    .getSingleResult());
        }catch(NoResultException e){
            return Optional.empty();
        }
    }

    public void insertGymContainMember(GymContainMember gymContainMember) {
        em.persist(gymContainMember);
    }

    public List<GymContainMember> findGymContainMemberByGymIdWithMemberId(Long gymId, Long memberId) {
        List<GymContainMember> resultList = em.createQuery("select gcm from GymContainMember gcm " +
                        " join gcm.gym g" +
                        " join gcm.member m" +
                        " where g.id = :gymId " +
                        " and m.id = :memberId", GymContainMember.class)
                .setParameter("gymId", gymId)
                .setParameter("memberId", memberId)
                .getResultList();
        return resultList;

    }

    public List<Gym> findAll() {
        return em.createQuery("select g from Gym g ", Gym.class)
                .getResultList();
    }
}
