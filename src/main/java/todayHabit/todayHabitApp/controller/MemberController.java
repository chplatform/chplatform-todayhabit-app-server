package todayHabit.todayHabitApp.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import todayHabit.todayHabitApp.domain.member.Male;
import todayHabit.todayHabitApp.domain.member.Member;
import todayHabit.todayHabitApp.dto.response.DefaultRes;
import todayHabit.todayHabitApp.dto.member.CreateMemberDto;
import todayHabit.todayHabitApp.dto.member.LoginMemberDto;
import todayHabit.todayHabitApp.dto.member.MemberOwnMembershipsDto;
import todayHabit.todayHabitApp.dto.response.ListResponse;
import todayHabit.todayHabitApp.dto.response.SingleResponse;
import todayHabit.todayHabitApp.dto.schedule.MembershipClassListDto;
import todayHabit.todayHabitApp.service.AwsS3Service;
import todayHabit.todayHabitApp.service.GymService;
import todayHabit.todayHabitApp.service.MemberService;
import todayHabit.todayHabitApp.service.ResponseService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class  MemberController {

    private final MemberService memberService;
    private final GymService gymService;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final ResponseService responseService;

    @PostMapping("/member/checkAvailable")
    public DefaultRes checkMemberEmail(@RequestBody CheckEmailDto checkEmailDto) {
        return responseService.createDefaultResponse(memberService.checkMemberEmailAvailable(checkEmailDto.getEmail()));
    }

    @PostMapping("/member/save")
    public DefaultRes saveMember(@RequestBody @Valid CreateMemberDto request) throws Exception {
        if (!request.matchesPasswd()) {
            throw new ValidationException("비밀번호가 형식에 맞지 않습니다.");
        }

        String encodingPasswd = passwordEncoder.encode(request.getPasswd());
        Member member = new Member(
                request.getName(), request.getEmail(),
                request.getBirth(), Male.RequestToEnum(request.getMale()), request.getPhone(), encodingPasswd, "sample");
        return responseService.createSingleResponse(memberService.joinMember(member));
    }

    @PutMapping("/member/check/serialNumber/{id}")
    public SingleResponse<String> checkGymSerialNumber(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberGymInfo request) {
        return responseService.createSingleResponse(gymService.checkGymSerialNUmber(id, request.getSerialNumber()));
    }

    @PutMapping("/member/update/gym/{id}")
    public DefaultRes updateMemberGymCode(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberGymInfo request) throws Exception{
        memberService.updateCenterInfo(request.getSerialNumber(), id);
        return responseService.createDefaultResponse("센터 등록이 완료되었습니다");

    }

    @PostMapping("/member/login")
    public SingleResponse<Optional<LoginMemberDto>> loginMember(@RequestBody @Valid LoginInfo request) throws Exception {
        Long memberId = memberService.logIn(request.getEmail(), request.getPasswd(), request.getToken());
        return responseService.createSingleResponse(Optional.ofNullable(memberService.getMemberInfo(memberId)));
    }

    @PostMapping("/member/getData")
    public SingleResponse<Optional<LoginMemberDto>> getMemberData(@RequestBody @NotNull GetMemberDataDto request){
        return responseService.createSingleResponse(Optional.ofNullable(memberService.getMemberInfo(request.getMemberId())));
    }

    @PutMapping("/member/update/image/{id}")
    public DefaultRes updateMemberImage(@PathVariable("id") Long id,
                                   @RequestPart(value = "image") MultipartFile multipartFile) throws Exception{
        String imagePath = awsS3Service.updateMemberImage(id, multipartFile);
        return responseService.createDefaultResponse(memberService.updateMemberImage(id, imagePath));
    }

    @PutMapping("/member/update/info/{id}")
    public DefaultRes updateMemberInfo(@PathVariable("id") Long id,
                                   @RequestBody updateMemberInfoDto request) throws Exception{
        return responseService.createDefaultResponse(
                memberService.updateMemberInfo(id, request.getName(), Male.RequestToEnum(request.getMale()), request.getPhone())
        );
    }

    // 로그인 되지 않은 상태에서 비밀번호 재설정
    @PostMapping("/member/update/passwd")
    public DefaultRes updateMemberPasswd(@RequestBody @Valid UpdateMemberPasswd request) throws Exception {
        if (!request.matchesPasswd()) {
            throw new ValidationException("비밀번호가 형식에 맞지 않습니다.");
        }
        memberService.updatePasswd(request.getEmail(),request.getNewPasswd(), request.getApiKey());
        return responseService.createDefaultResponse("비밀번호 변경이 완료되었습니다");
    }

    // 로그인 된 상태에서 비밀번호 재설정
    @PostMapping("/member/change/passwd")
    public DefaultRes changeMemberPasswd(@RequestBody @Valid ChangeMemberPasswd request) throws Exception {
        if (!request.matchesPasswd()) {
            throw new ValidationException("비밀번호가 형식에 맞지 않습니다.");
        }
        memberService.changePasswd(request.getMemberId(), request.getOldPasswd(), request.getNewPasswd());
        return responseService.createDefaultResponse("비밀번호 변경이 완료되었습니다");
    }

    @GetMapping("/member/changeGym/{gymId}/{memberId}")
    public ListResponse<MemberOwnMembershipsDto> changeMembershipByGymId(
            @PathVariable("memberId") Long memberId,
            @PathVariable("gymId") Long gymId) throws Exception{
        return responseService.createListResponse(memberService.changeMemberOwnMembership(memberId, gymId));
    }

    @PostMapping("/member/change/bookmark")
    public DefaultRes bookmarkGym(@RequestBody BookmarkInfo request ) throws Exception {
        memberService.bookmarkGym(request.getOldGymId(), request.getNewGymId(), request.getMemberId());
        return responseService.createDefaultResponse("즐겨찾기 되었습니다");
    }

    @GetMapping("/member/membership/classList/{membershipId}/{memberId}")
    public SingleResponse<MembershipClassListDto> membershipUsingClassList(
            @PathVariable("membershipId") Long membershipId,
            @PathVariable("memberId") Long memberId) {
        return responseService.createSingleResponse(memberService.getMembershipClassList(membershipId, memberId));
    }

    @PostMapping("/member/logout")
    public DefaultRes logoutMember(@RequestBody MemberLogoutDto request) {
        return responseService.createDefaultResponse(memberService.memberLogout(request.getMemberId()));
    }

    @PostMapping("/member/expire")
    public DefaultRes expireMember(@RequestBody MemberExpireDto request){
        return responseService.createDefaultResponse(memberService.memberExpire(request.getMemberId()));
    }

    /*
     * data
     * */
    @Data
    static class UpdateMemberPasswd {
        private String apiKey;
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @NotEmpty(message = "새로운 비밀번호를 입력해주세요.")
        private String newPasswd;

        public boolean matchesPasswd() {
            String pattern = "^(?=.*?[a-z]|[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";

            return Pattern.matches(pattern, this.newPasswd);
        }
    }

    @Data
    static class ChangeMemberPasswd {
        private Long memberId;
        @NotEmpty(message = "기존 비밀번호를 입력해주세요.")
        private String oldPasswd;
        @NotEmpty(message = "새로운 비밀번호를 입력해주세요.")
        private String newPasswd;

        public boolean matchesPasswd() {
            String pattern = "^(?=.*?[a-z]|[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,16}$";
            return Pattern.matches(pattern, this.newPasswd);
        }
    }

    @Data
    static class UpdateMemberGymInfo {
        @NotEmpty(message = "센터 번호를 입력해주세요.")
        private String serialNumber;
    }

    @Data
    static class LoginInfo {
        @Email
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;
        @NotBlank
        @NotEmpty(message = "비밀번호를 입력해주세요.")
        private String passwd;
        private String token;
    }

    @Data
    static class BookmarkInfo {
        @NotEmpty
        private Long oldGymId;
        @NotEmpty
        private Long newGymId;
        @NotEmpty
        private Long memberId;
    }

    @Data
    static class CheckEmailDto {
        @NotEmpty
        private String email;
    }

    @Data
    static class updateMemberInfoDto {
        @NotEmpty
        private String phone;
        @NotEmpty
        private String name;
        @NotEmpty
        private String male;
    }

    @Data
    static class GetMemberDataDto {
        @NotEmpty
        private Long memberId;
    }

    @Data
    static class MemberLogoutDto {
        @NotEmpty
        private Long memberId;
    }
    @Data
    static class MemberExpireDto {
        @NotEmpty
        private Long memberId;
    }
}
