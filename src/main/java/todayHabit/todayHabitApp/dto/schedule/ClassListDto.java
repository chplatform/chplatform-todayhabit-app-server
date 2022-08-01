package todayHabit.todayHabitApp.dto.schedule;

import lombok.Data;
import todayHabit.todayHabitApp.domain.ReserveBlock;
import todayHabit.todayHabitApp.domain.WaitingMember;
import todayHabit.todayHabitApp.domain.member.MemberClass;
import todayHabit.todayHabitApp.domain.schedule.Schedule;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ClassListDto {
    List<ReserveBlockDto> reserveBlockList;
    List<ReserveClassList> reserveClassList;
    List<DayClassDto> classList;
    List<WaitingClassList> waitingClassLists;

    public ClassListDto(List<ReserveBlock> reserveBlockList, List<MemberClass> reserveClassLists, List<Schedule> classList, List<WaitingMember> waitingMemberList) {
        this.reserveBlockList = reserveBlockList
                .stream()
                .map(reserveBlock -> new ReserveBlockDto(reserveBlock))
                .collect(Collectors.toList());
        this.reserveClassList = reserveClassLists
                .stream().map(reserveClassList -> new ReserveClassList(reserveClassList))
                .collect(Collectors.toList());
        this.classList = classList
                .stream()
                .map(classLists -> new DayClassDto(classLists))
                .collect(Collectors.toList());
        this.waitingClassLists = waitingMemberList
                .stream()
                .map(waitingList -> new WaitingClassList(waitingList))
                .collect(Collectors.toList());
    }
}
