package todayHabit.todayHabitApp.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import todayHabit.todayHabitApp.service.FirebaseMessageService;

@RestController
@RequiredArgsConstructor
public class TestPushController {
    private final FirebaseMessageService firebaseMessageService;

    @PostMapping("/push/send")
    public String sendPush(@RequestBody sendToken request) throws FirebaseMessagingException {
        firebaseMessageService.sendToToken(request.getGymId(), request.getMemberId(), request.getTitle(), request.getContent());
        return "메시지 완료";
    }

    @Data
    static class sendToken {

        private Long gymId;
        private Long memberId;
        private String title;
        private String content;
    }
}
