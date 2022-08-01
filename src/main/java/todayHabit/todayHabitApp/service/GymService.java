package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.error.AlreadyRegisterException;
import todayHabit.todayHabitApp.error.NonExistGymException;
import todayHabit.todayHabitApp.repository.gym.GymRepository;
import todayHabit.todayHabitApp.repository.member.MemberRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;
    private final MemberRepository memberRepository;

    public String checkGymSerialNUmber(Long memberId, String serialNumber) {
        Optional<Gym> findGym = gymRepository.findGymBySerialNumber(serialNumber);
        if (findGym.isEmpty()) {
            throw new NonExistGymException();
        }
        Member findMember = memberRepository.findMemberById(memberId);
        Gym gym = findGym.get();
        List<GymContainMember> gymContainMemberList
                = gymRepository.findGymContainMemberByGymIdWithMemberId(gym.getId(), findMember.getId());
        if (!gymContainMemberList.isEmpty()) { // 등록된 센터가 아닐 때 -- 센터 등록
            throw new AlreadyRegisterException();
        }
        return findGym.get().getName();
    }
}
