package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.gym.GymContainMember;
import todayHabit.todayHabitApp.domain.member.*;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.dto.member.LoginMemberDto;
import todayHabit.todayHabitApp.dto.member.MemberOwnMembershipsDto;
import todayHabit.todayHabitApp.dto.schedule.MembershipClassListDto;
import todayHabit.todayHabitApp.error.*;
import todayHabit.todayHabitApp.repository.WaitingMemberRepository;
import todayHabit.todayHabitApp.repository.gym.GymContainMemberRepository;
import todayHabit.todayHabitApp.repository.gym.GymRepository;
import todayHabit.todayHabitApp.repository.member.MemberOwnMembershipRepository;
import todayHabit.todayHabitApp.repository.member.MemberRepository;
import todayHabit.todayHabitApp.repository.schedule.ClassRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final GymContainMemberRepository gymContainMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberOwnMembershipRepository memberOwnMembershipRepository;
    private final ClassRepository classRepository;
    private final WaitingMemberRepository waitingMemberRepository;

    @Transactional
    public Long joinMember(Member member) throws Exception{
        validateDuplicateMember(member);
        memberRepository.saveMember(member);
        return member.getId();
    }

    @Transactional
    public String updateCenterInfo(String serialNumber, Long memberId) throws Exception {
        Optional<Gym> findGym = gymRepository.findGymBySerialNumber(serialNumber);
        Member findMember = memberRepository.findMemberById(memberId);
        Gym gym = findGym.get();
        List<GymContainMember> gymContainMemberList
                = gymRepository.findGymContainMemberByGymIdWithMemberId(gym.getId(), findMember.getId());
        GymContainMember gymContainMember = new GymContainMember(gym, findMember);
        if (findMember.getGym() == null) { // 센터 정보가 없을 때 -- 회원정보에 센터 정보 추가
            gymContainMember.BookmarkGym();
            findMember.updateGymInfo(gym);
        }
        if(gymContainMemberList.isEmpty()) { // 등록된 센터가 아닐 때 -- 센터 등록
            gymRepository.insertGymContainMember(gymContainMember);
        }
        return "등록이 완료되었습니다.";
    }

    @Transactional
    public long logIn(String email, String passwd, String memberToken) throws Exception{
        List<Member> findMember = memberRepository.findMemberByEmail(email);
        if(findMember.isEmpty()){
            throw new NotExistMemberException();
        }else if(findMember.get(0).getStatus().equals(Status.삭제)){
            throw new ExpireMemberException();
        }
        if(passwordEncoder.matches(passwd,findMember.get(0).getPasswd())){
            findMember.get(0).updateToken(memberToken);
            return findMember.get(0).getId();
        }else{
            throw new NotCorrectPasswdException();
        }
    }

    @Transactional
    public void updatePasswd(String email, String newPasswd, String apiKey) throws Exception{
        if(!apiKey.equals("AIzaSyA4xnXye_bngFET6vDOu3OcG2ozOisB_6E")){
            throw new IllegalStateException("key error!");
        }
        List<Member> findMember = memberRepository.findMemberByEmail(email);
        String encodingPasswd = passwordEncoder.encode(newPasswd);
        findMember.get(0).updatePasswd(encodingPasswd);
    }

    @Transactional
    public void changePasswd(Long memberId, String oldPasswd, String newPasswd) throws Exception{
        Member findMemberInfo = memberRepository.findMemberById(memberId);
        if (!passwordEncoder.matches(oldPasswd, findMemberInfo.getPasswd())) {
            throw new NotCorrectPasswdException();
        }else{
            String encodingPasswd = passwordEncoder.encode(newPasswd);
            findMemberInfo.updatePasswd(encodingPasswd);
        }
    }

    public List<MemberOwnMembershipsDto> changeMemberOwnMembership(Long memberId, Long gymId) {
        List<MemberOwnMembership> membershipList = memberRepository.findMemberOwnMembershipByGymId(memberId, gymId);
        return membershipList.stream()
                .map(membership -> new MemberOwnMembershipsDto(membership))
                .collect(Collectors.toList());
    }

    @Transactional
    public void bookmarkGym(Long oldGymId, Long newGymId,Long memberId) {
        Optional<GymContainMember> oldGym = gymContainMemberRepository.findByGymIdWithMemberId(memberId, oldGymId);
        Optional<GymContainMember> newGym = gymContainMemberRepository.findByGymIdWithMemberId(memberId, newGymId);
        oldGym.get().UnBookmarkGym();
        newGym.get().BookmarkGym();
    }

    public MembershipClassListDto getMembershipClassList(Long membershipId, Long memberId) {
        MemberOwnMembership membershipInfo = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        List<MemberClass> classList = classRepository.findByMembershipIdWithMemberId(memberId, membershipId);
        List<WaitingMember> waitingMemberList = waitingMemberRepository.findByDateAndMemberIdAndGymId(memberId, membershipId);
        return new MembershipClassListDto(membershipInfo.getCountClass(), membershipInfo.getStartDay(), membershipInfo.getEndDay(), classList,waitingMemberList);
    }

    private void validateDuplicateMember(Member member) throws Exception{
        List<Member> findMember = memberRepository.findMemberByEmail(member.getEmail());
        if (!findMember.isEmpty()) {
            if(findMember.get(0).getStatus().equals(Status.삭제)){
                throw new ExpireMemberException();
            }
            throw new AlreadyExistMemberException();
        }
    }

    @Transactional
    public String updateMemberImage(Long id, String image) {
        Member memberInfo = memberRepository.findMemberById(id);
        memberInfo.updateImage(image);
        return "수정이 완료되었습니다.";
    }

    @Transactional
    public String updateMemberInfo(Long id, String name, Male male, String phone) {
        Member findMember = memberRepository.findMemberById(id);
        findMember.updateMemberInfo(phone, name, male);
        return "수정이 완료되었습니다.";
    }

    public LoginMemberDto getMemberInfo(Long memberId) {
        Map<Long, Long> gymMembershipList = new HashMap<>();

        List<Member> memberInfo = memberRepository.findMemberByIdWithEveryData(memberId);
        List<Object[]> membershipInfo = memberOwnMembershipRepository.findByIdWithEndDay(memberId);
        for (Object[] o : membershipInfo) {
            gymMembershipList.put((Long)o[0], (Long)o[1]);
        }

        return new LoginMemberDto(memberInfo.get(0), gymMembershipList);
    }

    public String checkMemberEmailAvailable(String email) {
        List<Member> memberByEmail = memberRepository.findMemberByEmail(email);
        if (memberByEmail.isEmpty()) {
            return "존재하지 않은 회원입니다.";
        }else{
            return "존재하는 회원입니다.";
        }
    }

    @Transactional
    public String memberLogout(Long memberId) {
        Member findMemberInfo = memberRepository.findMemberById(memberId);
        findMemberInfo.deleteToken();
        return "로그아웃 완료";
    }

    @Transactional
    public String memberExpire(Long memberId) {
        Member findMemberInfo = memberRepository.findMemberById(memberId);
        findMemberInfo.expireMember();
        findMemberInfo.deleteToken();
        return "회원 탈퇴 완료";
    }
}
