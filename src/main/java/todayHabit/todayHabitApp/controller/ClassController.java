package todayHabit.todayHabitApp.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import todayHabit.todayHabitApp.dto.response.DefaultRes;
import todayHabit.todayHabitApp.dto.response.ListResponse;
import todayHabit.todayHabitApp.dto.response.SingleResponse;
import todayHabit.todayHabitApp.dto.schedule.ClassListDto;
import todayHabit.todayHabitApp.dto.schedule.DayClassDto;
import todayHabit.todayHabitApp.service.ClassService;
import todayHabit.todayHabitApp.service.FirebaseMessageService;
import todayHabit.todayHabitApp.service.ResponseService;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;
    private final FirebaseMessageService firebaseMessageService;
    private final ResponseService responseService;

    @PostMapping("/class/day/classList")
    public SingleResponse<ClassListDto> getClassList(@RequestBody getClassListDto request) throws Exception{
        return responseService.createSingleResponse(classService.getClassList(request.getDate(),
                request.getGymId(), request.getMembershipId()));
    }

    @PostMapping("/class/reserve")
    public DefaultRes reserveClass(@RequestBody reserveClassDto request) throws Exception{
        String result = classService.reserveClass(request.getMemberId(), request.getGymId(),
                request.getMembershipId(), request.getClassId());
        firebaseMessageService.sendToToken(request.getGymId(), request.getMemberId(), " 센터 수업 예약", "수업이 예약되었습니다.");
        return responseService.createDefaultResponse(result);
    }

    @PostMapping("/class/cancel")
    public DefaultRes cancelClass(@RequestBody reserveClassDto request) throws Exception{
        String result = classService.cancelClass(request.getMemberId(), request.getGymId(),
                request.getMembershipId(), request.getClassId());

        firebaseMessageService.sendToToken(request.getGymId(), request.getMemberId(), " 센터 예약 취소", "수업이 취소되었습니다.");
        return responseService.createDefaultResponse(result);
    }

    @PostMapping("/class/memberReserve")
    public ListResponse<DayClassDto> memberReserveClass(@RequestBody memberReserveClassDto request) throws Exception{
        return responseService.createListResponse(classService.memberReserveClass(request.getGymId(), request.getMemberId()));
    }

    @Data
    static class memberReserveClassDto{
        @NotNull
        private Long gymId;
        @NotNull
        private Long memberId;
    }

    @Data
    static class AlarmClassDto {
        @NotNull
        private Long classId;
    }

    @Data
    static class getClassListDto {
        @NotNull
        private LocalDate date;
        @NotNull
        private Long gymId;
        @NotNull
        private Long membershipId;
    }

    @Data
    static class BeforeDayDto {
        @NotNull
        private LocalDate date;
        @NotNull
        private Long gymId;
        @NotNull
        private Long membershipId;
    }

    @Data
    static class reserveClassDto{
        @NotNull
        private Long gymId;
        @NotNull
        private Long memberId;
        @NotNull
        private Long membershipId;
        @NotNull
        private Long classId;
    }
}
