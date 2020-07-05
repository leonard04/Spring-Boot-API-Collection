package com.igaraleon.app.ws.service;

import com.igaraleon.app.ws.dto.StudentDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

public interface IStudentService {
    Boolean registerNewStudent(StudentDto studentDto);
    StudentDto updatestudent(StudentDto studentDto, Long id);
    Boolean deleteStudentById(Long id);
    StudentDto findStudentById(Long id);
    List<StudentDto> getAllStudent();
    String storeFile(MultipartFile file, String nim);
    Resource loadFileAsResource(String filename);
}
