package todayHabit.todayHabitApp.controller;

import com.amazonaws.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import todayHabit.todayHabitApp.dto.NoticeDto;
import todayHabit.todayHabitApp.dto.response.ListResponse;
import todayHabit.todayHabitApp.dto.response.SingleResponse;
import todayHabit.todayHabitApp.service.NoticeService;
import todayHabit.todayHabitApp.service.ResponseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final ResponseService responseService;

    @GetMapping("noticeSize/{gymId}")
    public SingleResponse<Integer> getNoticeCount(@PathVariable("gymId") Long gymId) {
        return responseService.createSingleResponse(noticeService.selectNoticeCount(gymId));
    }

    @GetMapping("/notice/{gymId}")
    public ListResponse<NoticeDto> getNoticeList(@PathVariable("gymId") Long gymId, @RequestParam("index")int index) {
        return responseService.createListResponse(noticeService.selectAllNotice(gymId, index));
    }

    @GetMapping("/notice/info/{noticeId}")
    public SingleResponse<NoticeDto> getNoticeInfo(@PathVariable("noticeId") Long noticeId) {
        return responseService.createSingleResponse(noticeService.selectNotice(noticeId));
    }
}
