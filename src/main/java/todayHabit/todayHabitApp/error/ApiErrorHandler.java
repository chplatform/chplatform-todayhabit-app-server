package todayHabit.todayHabitApp.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@ControllerAdvice
public class ApiErrorHandler extends Exception {

    /*
    * ClassService 에러
    * */
    @ExceptionHandler(value = ReserveBlockException.class)
    public ResponseEntity<Object> ReserveBlockException(ReserveBlockException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("예약이 차단된 날짜입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    /*
     * MemberService 에러
     * */
    @ExceptionHandler(value = NonExistGymException.class)
    public ResponseEntity<Object> NonExistGymException(NonExistGymException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("다니고 계신 센터에 문의 후 재입력해주세요. ", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = AlreadyRegisterException.class)
    public ResponseEntity<Object> AlreadyRegisterException(AlreadyRegisterException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("이미 등록된 센터입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = NotExistMemberException.class)
    public ResponseEntity<Object> NotExistMemberException(NotExistMemberException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("존재하지 않는 회원입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = ExpireMemberException.class)
    public ResponseEntity<Object> ExpireMemberException(ExpireMemberException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("탈퇴한 계정입니다. 센터에 문의해주시기 바랍니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }


    @ExceptionHandler(value = NotCorrectPasswdException.class)
    public ResponseEntity<Object> NotCorrectPasswdException(NotCorrectPasswdException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("Email 혹은 비밀번호가 틀렸습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }
    
    @ExceptionHandler(value = AlreadyExistMemberException.class)
    public ResponseEntity<Object> AlreadyExistMemberException(AlreadyExistMemberException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("이미 존재하는 회원입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException,httpStatus);
    }

    @ExceptionHandler(value = EmptyFileException.class)
    public ResponseEntity<Object> EmptyFileException(EmptyFileException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("사진이 없습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }
    
    /*
    * ClassService 에러 핸들러
    * */
    @ExceptionHandler(value = TimeoutOpenReserveException.class)
    public ResponseEntity<Object> TimeoutOpenReserveException(TimeoutOpenReserveException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("수업 오픈 시점이 아닙니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = HoldingException.class)
    public ResponseEntity<Object> HoldingException(HoldingException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("홀딩되어있는 기간입니다." + e.getStartDay()+"-"+e.getEndDay() + "-" + e.getHoldingId(), httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = TimeoutReserveException.class)
    public ResponseEntity<Object> TimeoutReserveException(TimeoutReserveException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("수업 예약 가능시간이 아닙니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = OutOfRangeMembershipException.class)
    public ResponseEntity<Object> OutOfRangeMembershipException(OutOfRangeMembershipException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("회원권 이용 기간이 아닙니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = MaxClassException.class)
    public ResponseEntity<Object> MaxClassException(MaxClassException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("예약 정원이 다찼습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = AlreadyReserveClassException.class)
    public ResponseEntity<Object> AlreadyReserveClassException(AlreadyReserveClassException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("이미 예약 중인 회원입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = MaxWaitingMemberException.class)
    public ResponseEntity<Object> MaxWaitingMemberException(MaxWaitingMemberException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("대기정원이 마감되었습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = NotEnoughMembershipException.class)
    public ResponseEntity<Object> NotEnoughMembershipException(NotEnoughMembershipException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("회원권을 모두 소진하셨습니다. 센터에 문의해주세요.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = TimeoutCancelException.class)
    public ResponseEntity<Object> TimeoutCancelException(TimeoutCancelException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("수업 취소 가능시간이 아닙니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = OverDayAttendException.class)
    public ResponseEntity<Object> OverDayAttendException(OverDayAttendException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("일일최대수강 횟수를 초과했습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = OverWeekAttendException.class)
    public ResponseEntity<Object> OverWeekAttendException(OverWeekAttendException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("주간최대수강 횟수를 초과했습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    /*
    * holdingController
    * */

    @ExceptionHandler(value = OutOfStartDayMembershipPeriodException.class)
    public ResponseEntity<Object> OutOfMembershipPeriodException(OutOfStartDayMembershipPeriodException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("시작일이 회원권 기간내에 있지 않습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }
    @ExceptionHandler(value = OutOfEndDayMembershipPeriodException.class)
    public ResponseEntity<Object> OutOfEndDayMembershipPeriodException(OutOfEndDayMembershipPeriodException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("종료일이 회원권 기간내에 있지 않습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = AlreadyUsingHoldingException.class)
    public ResponseEntity<Object> AlreadyUsingHoldingException(AlreadyUsingHoldingException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("이미 사용된 홀딩권입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = AlreadyHoldingException.class)
    public ResponseEntity<Object> AlreadyHoldingException(AlreadyHoldingException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("이미 홀딩되어있는 기간입니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = WebHoldingException.class)
    public ResponseEntity<Object> WebHoldingException(WebHoldingException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("홀딩 기간에는 예약이 불가합니다. 예약을 원하시면 센터에 문의해주세요.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }
    
    @ExceptionHandler(value = ExpiredHoldingException.class)
    public ResponseEntity<Object> ExpiredHoldingException(ExpiredHoldingException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException("사용할 수 있는 홀딩 일수를 초과하였습니다.", httpStatus, LocalDateTime.now());
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @Data
    @AllArgsConstructor
    static class ApiException {

        private final String message;
        private final HttpStatus httpStatus;
        private final LocalDateTime time;

    }

}


