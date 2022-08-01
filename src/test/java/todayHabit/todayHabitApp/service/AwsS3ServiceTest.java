package todayHabit.todayHabitApp.service;

import com.amazonaws.util.IOUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AwsS3ServiceTest {

    @Autowired
    AwsS3Service awsS3Service;

    @Test
    public void 회원이미지업로드() throws Exception {

        //given
        File file = new File("C:\\GitHub\\TH-App\\bookingHabit-CRM-APP\\server\\todayHabitApp\\todayHabitApp\\src\\test\\java\\todayHabit\\todayHabitApp\\service\\good.jpg");
        FileItem fileItem = new DiskFileItem("file", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
        InputStream input = new FileInputStream(file);
        OutputStream os = fileItem.getOutputStream();
        IOUtils.copy(input, os);

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

        //when
        String s = awsS3Service.updateMemberImage(334l, multipartFile);
       // System.out.println("s = " + s);

        //then

    }
}