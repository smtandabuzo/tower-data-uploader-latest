package com.emailattachment.service;

import com.emailattachment.dto.UserDto;
import com.emailattachment.entity.UploadDetails;
import com.emailattachment.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public interface UploadService {

    List<UploadDetails> getByKeyword(String keyword);
    void saveUploadsToDatabase(MultipartFile file);
    void saveAttachToDatabase(InputStream inputStream);
}
