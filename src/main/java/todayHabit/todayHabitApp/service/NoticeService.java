package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import todayHabit.todayHabitApp.domain.Notice;
import todayHabit.todayHabitApp.dto.NoticeDto;
import todayHabit.todayHabitApp.repository.NoticeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public List<NoticeDto> selectAllNotice(Long gymId, int index) {
        List<Notice> noticeList = noticeRepository.findByGymIdWithPaging(gymId, index);
        return noticeList.stream()
                .map(notice -> new NoticeDto(notice))
                .collect(Collectors.toList());
    }

    @Transactional
    public NoticeDto selectNotice(Long noticeId) {
        Notice noticeInfo = noticeRepository.findById(noticeId);
        noticeInfo.increaseCount();
        return new NoticeDto(noticeInfo);
    }

    public Integer selectNoticeCount(Long gymId) {
        List<Notice> noticeList = noticeRepository.findByGymId(gymId);
        return noticeList.size();
    }
}
