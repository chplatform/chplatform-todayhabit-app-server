package todayHabit.todayHabitApp.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import todayHabit.todayHabitApp.dto.response.DefaultRes;
import todayHabit.todayHabitApp.dto.response.SingleResponse;
import todayHabit.todayHabitApp.service.HoldingService;
import todayHabit.todayHabitApp.service.ResponseService;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class HoldingController {

    private final HoldingService holdingService;
    private final ResponseService responseService;

    @PostMapping("/hold/membership/check")
    public SingleResponse<Integer> checkHoldingMembership(@RequestBody HoldingDto request) throws Exception {
        int classListCount = holdingService.checkHoldingMembership(request.getGymId(),
                request.getMembershipId(), request.getStartDay(), request.getEndDay());
        return responseService.createSingleResponse(classListCount);
    }

    @PostMapping("/hold/membership")
    public DefaultRes holdingMembership(@RequestBody HoldingDto request) throws Exception {
        return responseService.createDefaultResponse(holdingService.holdingMembership(
                request.getMembershipId(), request.getHoldingMembershipId(), request.getStartDay(), request.getEndDay()));
    }

    @PostMapping("/hold/membership/cancel")
    public DefaultRes cancelHoldingMembership(@RequestBody HoldingCancelDto request) throws Exception {
        return responseService.createDefaultResponse(holdingService.cancelHoldingMembership(request.getMembershipId(),
                request.getHoldingMembershipId()));
    }

    @Data
    static class HoldingDto {
        @NotNull
        private Long gymId;
        @NotNull
        private Long memberId;
        @NotNull
        private Long membershipId;
        @NotNull
        private Long holdingMembershipId;
        @NotNull
        private LocalDate startDay;
        @NotNull
        private LocalDate endDay;
    }

    @Data
    static class HoldingCancelDto {
        @NotNull
        private Long membershipId;
        @NotNull
        private Long holdingMembershipId;
    }
}
