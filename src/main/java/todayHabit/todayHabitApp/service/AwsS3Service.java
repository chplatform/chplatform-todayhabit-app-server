package todayHabit.todayHabitApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.repository.AwsS3Repository;
import todayHabit.todayHabitApp.repository.member.MemberRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AwsS3Service {
    private final MemberRepository memberRepository;
    private final AwsS3Repository awsS3Repository;

    public String updateMemberImage(Long id, MultipartFile multipartFile) throws IOException {
        String url;
        Member memberInfo = memberRepository.findMemberById(id);
        if(!memberInfo.getImage().equals("sample")){
            awsS3Repository.deleteMemberImage(memberInfo.getImage());
        }
        if (!multipartFile.isEmpty()) {
            url = awsS3Repository.uploadMemberImageFile(multipartFile);
        }else{
            url = "sample";
        }
        return url;
    }
}
