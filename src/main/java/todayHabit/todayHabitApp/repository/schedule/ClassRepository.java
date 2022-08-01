package todayHabit.todayHabitApp.repository.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClassRepository {
    private final EntityManager em;

    public List<Schedule> findByMonthAndGymIdAndMembershipId(LocalDate date, Long gymId, List<Long> classTypeList) {
        String query = "select s from Schedule s" +
                " join fetch s.classType ct " +
                " join s.gym g" +
                " left join fetch s.coachClasses cc" +
                " left join fetch cc.coach co" +
                " where EXTRACT(month from s.startDay) = :month " +
                " and g.id = :gymId";
        if (classTypeList.size() != 0) {
            query += " and ct.id in ( ";
        }
        for (int i = 0 ;i<classTypeList.size();i++) {
            if(i != classTypeList.size()-1){
                query += classTypeList.get(i).toString() + ",";
            }else{
                query += classTypeList.get(i).toString();
            }
        }
        if (classTypeList.size() != 0) {
            query += " ) " +
                    " order by s.startDay, s.startTime";
        }
        System.out.println("query = " + query);
        List<Schedule> classList = em.createQuery(query, Schedule.class)
                .setParameter("month", date.getMonthValue())
                .setParameter("gymId", gymId)
                .getResultList();
        return classList;
    }

    public Schedule findById(Long classId) {
        return em.find(Schedule.class, classId);
    }

    public List<MemberClass> findByMembershipIdWithMemberId(Long memberId, Long membershipId) {
        return em.createQuery("select mc from MemberClass mc " +
                        " join fetch mc.schedule s" +
                        " join mc.member m " +
                        " join mc.memberOwnMembership mom " +
                        " where m.id = :memberId " +
                        " and mom.id = :membershipId" +
                        " order By s.startDay desc, s.startTime desc", MemberClass.class)
                .setParameter("memberId", memberId)
                .setParameter("membershipId", membershipId)
                .getResultList();
    }

    public List<MemberClass> findByDateWithMemberIdAndGymID(Long gymId, Long memberId) {
        return em.createQuery(" select mc from MemberClass mc " +
                        " join fetch mc.schedule s" +
                        " join mc.gym g" +
                        " join mc.member m"+
                        " where m.id = :memberId" +
                        " and s.startDay =: day" +
                        " and g.id = :gymId", MemberClass.class)
                .setParameter("memberId", memberId)
                .setParameter("gymId", gymId)
                .setParameter("day",LocalDate.now())
                .getResultList();
    }

}
