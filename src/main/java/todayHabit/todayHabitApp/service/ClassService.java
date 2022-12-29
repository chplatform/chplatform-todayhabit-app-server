package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;

import todayHabit.todayHabitApp.domain.ReserveBlock;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.holding.HoldingInfo;
import todayHabit.todayHabitApp.domain.holding.HoldingList;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembership;
import todayHabit.todayHabitApp.domain.member.MemberOwnMembershipClassType;
import todayHabit.todayHabitApp.domain.schedule.ClassHistory;
import todayHabit.todayHabitApp.domain.schedule.Schedule;
import todayHabit.todayHabitApp.dto.member.MemberMembershipHistoryDto;
import todayHabit.todayHabitApp.dto.schedule.ClassListDto;
import todayHabit.todayHabitApp.dto.schedule.DayClassDto;
import todayHabit.todayHabitApp.error.*;
import todayHabit.todayHabitApp.repository.*;
import todayHabit.todayHabitApp.repository.gym.GymRepository;
import todayHabit.todayHabitApp.repository.holdingMembership.HoldingListRepository;
import todayHabit.todayHabitApp.repository.member.MemberClassRepository;
import todayHabit.todayHabitApp.repository.member.MemberMembershipHistoryRepository;
import todayHabit.todayHabitApp.repository.member.MemberOwnMembershipRepository;
import todayHabit.todayHabitApp.repository.member.MemberRepository;
import todayHabit.todayHabitApp.repository.schedule.ClassHistoryRepository;
import todayHabit.todayHabitApp.repository.schedule.ClassRepository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassService {
	 private final FirebaseMessageService firebaseMessageService;
    private final MemberRepository memberRepository;
    private final ClassRepository classRepository;
    private final MemberOwnMembershipRepository memberOwnMembershipRepository;
    private final MemberClassRepository memberClassRepository;
    private final ReserveBlockRepository reserveBlockRepository;
    private final GymRepository gymRepository;
    private final WaitingMemberRepository waitingMemberRepository;
    private final ClassHistoryRepository classHistoryRepository;
    private final HoldingListRepository holdingListRepository;
    private final HoldingService holdingService;
    private final MemberMembershipHistoryRepository memberMembershipHistoryRepository;
    
    public ClassListDto getClassList(LocalDate date, Long gymId, Long membershipId) {
        List<ReserveBlock> reserveBlockList = reserveBlockRepository.findByMonthAndGymId(date, gymId);

        List<Long> classTypeList = new ArrayList();
        MemberOwnMembership membership = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        List<MemberOwnMembershipClassType> membershipClassTypes = membership.getMembershipClassTypes();
        for (MemberOwnMembershipClassType membershipClassType : membershipClassTypes) {
            classTypeList.add(membershipClassType.getClassType().getId());
        }
        List<MemberClass> reserveClassLists = memberClassRepository.findByMemberOwnMembershipId(date, gymId, membershipId);
        List<Schedule> scheduleList = classRepository.findByMonthAndGymIdAndMembershipId(date, gymId, classTypeList);
        List<WaitingMember> waitingMemberList = waitingMemberRepository.findByMonthAndMembershipId(date, gymId, membershipId);
        return new ClassListDto(reserveBlockList, reserveClassLists, scheduleList, waitingMemberList);
    }

    @Transactional
    public String reserveClass(Long memberId, Long gymId, Long membershipId, Long classId) throws Exception {
        // 1. 예약 가능한 시간인지 확인
        // 1.1 이미 예약된 회원인지 확인
        // 2. 예약 정원이 있나 확인 -> 없으면 대기
        // 2-1. 예약 정원이 있으면 예약
        // 2-2-1 회원권 차감
        // 2-2-2 수업 정원 증가
        // 2-2-3 수업 인원 리스트 삽입
        // 2-2. 예약 정원이 없으면 대기 가능인원 확인
        // 2-2-0 센터 대기 가능 인원 세기
        // 2-2-1 회원권 차감
        // 2-2-2 대기 인원에 추가
        Gym gymInfo = gymRepository.findById(gymId);
        Member memberInfo = memberRepository.findMemberById(memberId);
        Schedule classInfo = classRepository.findById(classId);
        MemberOwnMembership membership = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        List<MemberClass> memberClassList = memberClassRepository.findByMemberIdWithClassId(memberId, classId);
        List<BigInteger> memberDayClassSize = memberClassRepository.findByMemberIdWithClassIdAndDay(memberId, membershipId, classInfo.getStartDay());
        List<BigInteger> waitngMemberDayClassSize = waitingMemberRepository.findByMemberIdWithClassIdAndDay(memberId, membershipId, classInfo.getStartDay());
        List<BigInteger> memberWeekClassSize= memberClassRepository.findByMemberIdWithClassIdAndWeek(memberId, membershipId, classInfo.getStartDay());
        List<BigInteger> watingMemberWeekClassSize= waitingMemberRepository.findByMemberIdWithClassIdAndWeek(memberId, membershipId, classInfo.getStartDay());
        List<WaitingMember> waitingMemberList = waitingMemberRepository.findByMemberIdWithClassId(memberId, classId);
        List<HoldingInfo> alreadyHoldingInfo = holdingListRepository.findByMembershipIdAndStartDayAndEndDay(membershipId, classInfo.getStartDay(), classInfo.getStartDay());
        LocalDateTime openTime = gymInfo.getOpenTime(classInfo.getStartDay());
        LocalDateTime reservableTime = LocalDateTime
                .of(classInfo.getStartDay(), classInfo.getStartTime())
                .minusMinutes(gymInfo.getReservableTime());
        
        int dayClassSize = Integer.parseInt(String.valueOf(memberDayClassSize.get(0))) + Integer.parseInt(String.valueOf(waitngMemberDayClassSize.get(0)));
        int weekClassSize = Integer.parseInt(String.valueOf(memberWeekClassSize.get(0))) + Integer.parseInt(String.valueOf(watingMemberWeekClassSize.get(0)));
        
        if(openTime.isAfter(LocalDateTime.now())){ // 아직 예약 오픈 시점이 아닐 때
            throw new TimeoutOpenReserveException();
        }else if(alreadyHoldingInfo.size() > 0){
        	int statusCode = 500;
        	if(alreadyHoldingInfo.get(0).getReqType().equals("admin")) {
        		statusCode = 1300;
        	}else if(alreadyHoldingInfo.get(0).getReqType().equals("default")) {
        		statusCode = 1400;
        	}
            throw new HoldingException(alreadyHoldingInfo.get(0), statusCode);
        }else if(membership.getStartDay().isAfter(classInfo.getStartDay()) || membership.getEndDay().isBefore(classInfo.getStartDay())){
            throw new OutOfRangeMembershipException();
        }else if (LocalDateTime.now().isAfter(reservableTime)) { // 현재 시간이 예약 가능 시간 보다 지났을 때 예약 불가능
            throw new TimeoutReserveException();
        }else if (membership.getMaxCountClass() < (membership.getCountClass()+classInfo.getDecrease())) { // 회원권이 모자를 때
            throw new NotEnoughMembershipException();
        }else if(membership.getMembership().getMaxDayAttend() < dayClassSize + classInfo.getDecrease()) {
            throw new OverDayAttendException();
        }else if(membership.getMembership().getMaxWeekAttend() < weekClassSize + classInfo.getDecrease()) {
            throw new OverWeekAttendException();
        }else if(!memberClassList.isEmpty() || !waitingMemberList.isEmpty()) { // 이미 예약된 회원일 때
            throw new AlreadyReserveClassException();
        }else if (classInfo.getTotalReservation() <= classInfo.getReserveNumber()) { //예약 정원이 다찼을 때 -> 대기
            int waitingNumber;
            Optional<Integer> maxWaitingNumber = waitingMemberRepository.findMaxWaitingNumber(classId);
            ClassHistory classHistory = new ClassHistory(gymInfo, classInfo, memberInfo, 3);
            classHistoryRepository.save(classHistory);
            if (maxWaitingNumber.isEmpty()) { 
                waitingNumber = 1;
            } else {
                waitingNumber = maxWaitingNumber.get().intValue() + 1;
            }
            if (waitingNumber > gymInfo.getLimitWaitingMember()) {
                throw new MaxWaitingMemberException();
            }
            WaitingMember waitingMember = new WaitingMember(gymInfo, classInfo, memberInfo, membership, waitingNumber);
            waitingMemberRepository.save(waitingMember);
            membership.increaseMembership(classInfo.getDecrease());
            membership.increaseAttend();
            
            // 회원권 히스토리 저장
    		memberMembershipHistoryRepository.save(new MemberMembershipHistoryDto().builder()
    				.gym(gymInfo)
    				.member(memberInfo)
    				.memberMembership(membership)
    				.schedule(classInfo)
    				.attend(3)
    				.class_count(classInfo.getDecrease())
    				.insert_date(LocalDateTime.now())
    				.build()
    				.toEntity());	
            
            return classInfo.getStartTime().minusMinutes(gymInfo.getReserveConfirmTimeValue()) + "까지 결원이 발생하지 않으면 대기가 자동으로 취소됩니다." + classInfo.getStartTime().minusMinutes(gymInfo.getReserveConfirmTimeValue()) + "결원이 발생시 자동으로 대기예약이 확정됩니다.";
        } else { // 예약 가능
            ClassHistory classHistory = new ClassHistory(gymInfo, classInfo, memberInfo, 0);
            classHistoryRepository.save(classHistory);
            classInfo.increaseCount();
            MemberClass memberClass = new MemberClass(gymInfo, classInfo, memberInfo, membership);
            memberClassRepository.save(memberClass);
            membership.increaseMembership(classInfo.getDecrease());
            membership.increaseAttend();
            
            // 회원권 히스토리 저장
    		memberMembershipHistoryRepository.save(new MemberMembershipHistoryDto().builder()
    				.gym(gymInfo)
    				.member(memberInfo)
    				.memberMembership(membership)
    				.schedule(classInfo)
    				.attend(0)
    				.class_count(classInfo.getDecrease())
    				.insert_date(LocalDateTime.now())
    				.build()
    				.toEntity());	
            
            return "예약이 완료되었습니다.";
        }
    }


    @Transactional
    public String cancelClass(Long memberId, Long gymId, Long membershipId, Long classId) {
        // 1.예약 취소 가능 시간인지 확인
        // 2-1 예약 회원
        // 2-1-1 예약 리스트 삭제
        // 2-1-2 회원권 복구
        // 2-1-2 수업 회원 수 복구
        // 2-1-3 대기 인원 번호 변경
        // 2-2 대기 회원
        // 2-2-1 회원권 복구
        // 2-2-2 대기 인원 번호 변경
        // 2-2-3 대기 리스트 삭제

        Gym gymInfo = gymRepository.findById(gymId);
        Member memberInfo = memberRepository.findMemberById(memberId);
        Schedule classInfo = classRepository.findById(classId);
        MemberOwnMembership membership = memberOwnMembershipRepository.findByIdWithMemberOwnMembership(membershipId);
        List<MemberClass> memberClassList = memberClassRepository.findByMemberIdWithClassId(memberId, classId);
        List<WaitingMember> waitingMemberList = waitingMemberRepository.findByMemberIdWithClassId(memberId, classId);
        LocalDateTime ableCancelTime = LocalDateTime
                .of(classInfo.getStartDay(), classInfo.getStartTime())
                .minusMinutes(gymInfo.getCancelReserveTime());
        ClassHistory classHistory = new ClassHistory(gymInfo, classInfo, memberInfo, 4);
        classHistoryRepository.save(classHistory);
        if (LocalDateTime.now().isAfter(ableCancelTime)) { // 현재 시간이 예약 가능 시간 보다 지났을 때 예약 불가능
            throw new TimeoutCancelException();
        }else if(!memberClassList.isEmpty()) { //예약 회원일 때
            membership.decreaseMembership(classInfo.getDecrease());
            classInfo.decreaseCount();
            memberClassRepository.deleteById(memberClassList.get(0));
            
            // 회원권 히스토리 저장
    		memberMembershipHistoryRepository.save(new MemberMembershipHistoryDto().builder()
    				.gym(gymInfo)
    				.member(memberInfo)
    				.memberMembership(membership)
    				.schedule(classInfo)
    				.attend(4)
    				.class_count(-classInfo.getDecrease())
    				.insert_date(LocalDateTime.now())
    				.build()
    				.toEntity());	
    		
            List<WaitingMember> waitingList = waitingMemberRepository.findByClassId(classId);
            for (WaitingMember waitingMember : waitingList) {
                if (waitingMember.getWaitingNumber() == 1) {
                    classInfo.increaseCount();
                    MemberClass memberClass = new MemberClass(waitingMember.getGym(), waitingMember.getSchedule(),
                            waitingMember.getMember(), waitingMember.getMemberOwnMembership());
                    ClassHistory classWaitingHistory = new ClassHistory(waitingMember.getGym(), waitingMember.getSchedule(),
                            waitingMember.getMember(), 0);
                    waitingMemberRepository.deleteById(waitingMember);
                    classHistoryRepository.save(classWaitingHistory);
                    memberClassRepository.save(memberClass);
                    
                    Member waitMemberInfo = memberRepository.findMemberById(waitingMember.getMember().getId());
                    
                    Long wait_member_membership_id = waitingMember.getMemberOwnMembership().getId();
                    MemberOwnMembership waitMembership = memberOwnMembershipRepository
                    		.findByIdWithMemberOwnMembership(wait_member_membership_id);
                    
                    // 회원권 히스토리 저장
            		memberMembershipHistoryRepository.save(new MemberMembershipHistoryDto().builder()
            				.gym(gymInfo)
            				.member(waitMemberInfo)
            				.memberMembership(waitMembership)
            				.schedule(classInfo)
            				.attend(0)
            				.class_count(classInfo.getDecrease())
            				.insert_date(LocalDateTime.now())
            				.build()
            				.toEntity());
            		
            		try {
						firebaseMessageService.sendToToken(gymInfo.getId(), waitMemberInfo.getId(), " 센터 수업 예약", "수업이 예약되었습니다.");
					} catch (FirebaseMessagingException e) {
						e.printStackTrace();
					}
            		
                }else{
                    waitingMember.changeWaitingNumber();
                }
            }
            membership.decreaseAttend();
            return "예약 취소가 완료되었습니다.";
        }else if (!waitingMemberList.isEmpty()) { //대기 회원일 때
            membership.decreaseMembership(classInfo.getDecrease());
            waitingMemberRepository.deleteById(waitingMemberList.get(0));
            List<WaitingMember> waitingList =
                    waitingMemberRepository.findByBehindWaitingMember(classId, waitingMemberList.get(0).getWaitingNumber());
            for (WaitingMember waitingMember : waitingList) {
                waitingMember.changeWaitingNumber();
            }
            membership.decreaseAttend();
            
            // 회원권 히스토리 저장
    		memberMembershipHistoryRepository.save(new MemberMembershipHistoryDto().builder()
    				.gym(gymInfo)
    				.member(memberInfo)
    				.memberMembership(membership)
    				.schedule(classInfo)
    				.attend(4)
    				.class_count(-classInfo.getDecrease())
    				.insert_date(LocalDateTime.now())
    				.build()
    				.toEntity());
    		
            return "대기 취소가 완료되었습니다.";
        }else{
            throw new IllegalStateException("예약이 안되어있는 회원입니다.");
        }
    }

    public List<DayClassDto> memberReserveClass(Long gymId, Long memberId) {
        return classRepository.findByDateWithMemberIdAndGymID(gymId, memberId)
                .stream().map(classList -> new DayClassDto(classList))
                .collect(Collectors.toList());

    }

//    @Async
//    @Transactional
//    @Scheduled(fixedDelay = 60000)
//    public void confirmClass() {
//        /**
//         * 1. 센터 리스트 불러오기
//         * 2. 수업 예약 확정 상태의 대기 인원 조사
//         * 3. 대기 인원 대기 취소 후 회원권 회복
//         * 4. 대기 회원 목록 삭제
//         * 5. 수업 히스토리에 대기 취소 목록 추가
//         */
//
//        List<Gym> gymInfo = gymRepository.findAll();
//        for (Gym gym : gymInfo) {
//            List<WaitingMember> waitingMemberList = waitingMemberRepository.findByGymIdAndConfirmTime(gym.getId(), gym.getReserveConfirmTimeValue());
//            for (WaitingMember waitingMember : waitingMemberList) {
//                ClassHistory classHistory = new ClassHistory(gym, waitingMember.getSchedule(), waitingMember.getMember(), 5);
//                classHistoryRepository.save(classHistory);
//                waitingMember.getMemberOwnMembership().decreaseMembership(waitingMember.getSchedule().getDecrease());
//                waitingMemberRepository.deleteById(waitingMember);
//            }
//        }
//    }
}

