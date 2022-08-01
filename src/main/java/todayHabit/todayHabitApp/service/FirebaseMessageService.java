package todayHabit.todayHabitApp.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import todayHabit.todayHabitApp.domain.gym.Gym;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.repository.gym.GymRepository;
import todayHabit.todayHabitApp.repository.member.MemberRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FirebaseMessageService {

    private final GymRepository gymRepository;
    private final MemberRepository memberRepository;
    /*
    * 특정 기기 1개에 전송
    * */
    public String sendToToken(Long gymId, Long memberId, String title, String content) throws FirebaseMessagingException {
        Member findMemberInfo = memberRepository.findMemberById(memberId);
        Gym findGymInfo = gymRepository.findById(gymId);
        String registrationToken = findMemberInfo.getToken();

        Notification notification = Notification.builder()
                .setTitle(findGymInfo.getName()+title)
                .setBody(content)
                .build();

        Map<String, String> pushAlarmData = findGymInfo.createPushAlarmData();

        Message message = Message.builder()
                .setNotification(notification)
                .putAllData(pushAlarmData)
                .setToken(registrationToken)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("response = " + response);
        return response;
    }
}