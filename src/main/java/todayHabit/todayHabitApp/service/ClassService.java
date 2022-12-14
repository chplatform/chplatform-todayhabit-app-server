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
        // 1. ?????? ????????? ???????????? ??????
        // 1.1 ?????? ????????? ???????????? ??????
        // 2. ?????? ????????? ?????? ?????? -> ????????? ??????
        // 2-1. ?????? ????????? ????????? ??????
        // 2-2-1 ????????? ??????
        // 2-2-2 ?????? ?????? ??????
        // 2-2-3 ?????? ?????? ????????? ??????
        // 2-2. ?????? ????????? ????????? ?????? ???????????? ??????
        // 2-2-0 ?????? ?????? ?????? ?????? ??????
        // 2-2-1 ????????? ??????
        // 2-2-2 ?????? ????????? ??????
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
        
        if(openTime.isAfter(LocalDateTime.now())){ // ?????? ?????? ?????? ????????? ?????? ???
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
        }else if (LocalDateTime.now().isAfter(reservableTime)) { // ?????? ????????? ?????? ?????? ?????? ?????? ????????? ??? ?????? ?????????
            throw new TimeoutReserveException();
        }else if (membership.getMaxCountClass() < (membership.getCountClass()+classInfo.getDecrease())) { // ???????????? ????????? ???
            throw new NotEnoughMembershipException();
        }else if(membership.getMembership().getMaxDayAttend() < dayClassSize + classInfo.getDecrease()) {
            throw new OverDayAttendException();
        }else if(membership.getMembership().getMaxWeekAttend() < weekClassSize + classInfo.getDecrease()) {
            throw new OverWeekAttendException();
        }else if(!memberClassList.isEmpty() || !waitingMemberList.isEmpty()) { // ?????? ????????? ????????? ???
            throw new AlreadyReserveClassException();
        }else if (classInfo.getTotalReservation() <= classInfo.getReserveNumber()) { //?????? ????????? ????????? ??? -> ??????
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
            
            // ????????? ???????????? ??????
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
            
            return classInfo.getStartTime().minusMinutes(gymInfo.getReserveConfirmTimeValue()) + "?????? ????????? ???????????? ????????? ????????? ???????????? ???????????????." + classInfo.getStartTime().minusMinutes(gymInfo.getReserveConfirmTimeValue()) + "????????? ????????? ???????????? ??????????????? ???????????????.";
        } else { // ?????? ??????
            ClassHistory classHistory = new ClassHistory(gymInfo, classInfo, memberInfo, 0);
            classHistoryRepository.save(classHistory);
            classInfo.increaseCount();
            MemberClass memberClass = new MemberClass(gymInfo, classInfo, memberInfo, membership);
            memberClassRepository.save(memberClass);
            membership.increaseMembership(classInfo.getDecrease());
            membership.increaseAttend();
            
            // ????????? ???????????? ??????
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
            
            return "????????? ?????????????????????.";
        }
    }


    @Transactional
    public String cancelClass(Long memberId, Long gymId, Long membershipId, Long classId) {
        // 1.?????? ?????? ?????? ???????????? ??????
        // 2-1 ?????? ??????
        // 2-1-1 ?????? ????????? ??????
        // 2-1-2 ????????? ??????
        // 2-1-2 ?????? ?????? ??? ??????
        // 2-1-3 ?????? ?????? ?????? ??????
        // 2-2 ?????? ??????
        // 2-2-1 ????????? ??????
        // 2-2-2 ?????? ?????? ?????? ??????
        // 2-2-3 ?????? ????????? ??????

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
        if (LocalDateTime.now().isAfter(ableCancelTime)) { // ?????? ????????? ?????? ?????? ?????? ?????? ????????? ??? ?????? ?????????
            throw new TimeoutCancelException();
        }else if(!memberClassList.isEmpty()) { //?????? ????????? ???
            membership.decreaseMembership(classInfo.getDecrease());
            classInfo.decreaseCount();
            memberClassRepository.deleteById(memberClassList.get(0));
            
            // ????????? ???????????? ??????
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
                    
                    // ????????? ???????????? ??????
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
						firebaseMessageService.sendToToken(gymInfo.getId(), waitMemberInfo.getId(), " ?????? ?????? ??????", "????????? ?????????????????????.");
					} catch (FirebaseMessagingException e) {
						e.printStackTrace();
					}
            		
                }else{
                    waitingMember.changeWaitingNumber();
                }
            }
            membership.decreaseAttend();
            return "?????? ????????? ?????????????????????.";
        }else if (!waitingMemberList.isEmpty()) { //?????? ????????? ???
            membership.decreaseMembership(classInfo.getDecrease());
            waitingMemberRepository.deleteById(waitingMemberList.get(0));
            List<WaitingMember> waitingList =
                    waitingMemberRepository.findByBehindWaitingMember(classId, waitingMemberList.get(0).getWaitingNumber());
            for (WaitingMember waitingMember : waitingList) {
                waitingMember.changeWaitingNumber();
            }
            membership.decreaseAttend();
            
            // ????????? ???????????? ??????
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
    		
            return "?????? ????????? ?????????????????????.";
        }else{
            throw new IllegalStateException("????????? ??????????????? ???????????????.");
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
//         * 1. ?????? ????????? ????????????
//         * 2. ?????? ?????? ?????? ????????? ?????? ?????? ??????
//         * 3. ?????? ?????? ?????? ?????? ??? ????????? ??????
//         * 4. ?????? ?????? ?????? ??????
//         * 5. ?????? ??????????????? ?????? ?????? ?????? ??????
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

