package todayHabit.todayHabitApp.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import todayHabit.todayHabitApp.dto.response.DefaultRes;
import todayHabit.todayHabitApp.dto.response.ListResponse;
import todayHabit.todayHabitApp.dto.response.SingleResponse;

import java.util.List;

@Service
public class ResponseService {
    public DefaultRes createDefaultResponse(String message) {
        DefaultRes defaultRes = new DefaultRes();
        defaultRes.setMessage(message);
        defaultRes.setStatusCode(HttpStatus.OK.value());
        return defaultRes;
    }

    public <T> SingleResponse<T> createSingleResponse(T data) {
        SingleResponse singleResponse = new SingleResponse();
        singleResponse.setData(data);
        setSuccessResponse(singleResponse);

        return singleResponse;
    }

    public <T> ListResponse<T> createListResponse(List<T> data) {
        ListResponse listResponse = new ListResponse();
        listResponse.setData(data);
        setSuccessResponse(listResponse);

        return listResponse;
    }

    void setSuccessResponse(DefaultRes defaultRes) {
        defaultRes.setStatusCode(HttpStatus.OK.value());
        defaultRes.setMessage("success");
    }
}
