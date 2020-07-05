package com.igaraleon.app.ws.service;

import com.igaraleon.app.ws.dto.StudentDto;
import com.igaraleon.app.ws.entity.Student;
import com.igaraleon.app.ws.exception.FileStorageException;
import com.igaraleon.app.ws.exception.MyFileNotFoundException;
import com.igaraleon.app.ws.property.FileStorageProperties;
import com.igaraleon.app.ws.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("StudentService")
public class StudentService implements IStudentService {
    private final Path fileStorageLocation;

    @Autowired
    private StudentRepository studentRepository;


    @Autowired
    public StudentService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String nim) {
        String extentsion="";
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            extentsion = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = nim+extentsion;
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;

        } catch (IOException ex){
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);

        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public Boolean registerNewStudent(StudentDto studentDto){
        Student student = mapStudentDtoToEntity(studentDto);
        Student regStudent = studentRepository.save(student);
        if (regStudent != null){
            return true;
        }

        return  false;
    }

    public StudentDto updatestudent(StudentDto studentDto, Long id){
        Student saveResult = null;
        Student expectedUpdate;

        Optional<Student> findStudent = studentRepository.findById(id);
        if (findStudent.isPresent()){
            expectedUpdate = mapStudentDtoToEntity(studentDto);
            expectedUpdate.setId(id);
            saveResult = studentRepository.save(expectedUpdate);
        }

        return mapStudentEntityToDto(saveResult);
    }

    @Override
    public Boolean deleteStudentById(Long id){
        studentRepository.deleteById(id);
        return true;
    }

    @Override
    public StudentDto findStudentById(Long id){
        Optional<Student> studentOptional = studentRepository.findById(id);
        StudentDto result = new StudentDto();
        if (studentOptional.isPresent()){
            result = mapStudentEntityToDto(studentOptional.get());
        }

        return  result;
    }

    @Override
    public List<StudentDto> getAllStudent(){

        List<Student> students = studentRepository.findAll();
        List<StudentDto> studentDtos = new ArrayList<>();

        for (Student student : students) {
            StudentDto studentDto = mapStudentEntityToDto(student);
            studentDtos.add(studentDto);
        }

        return studentDtos;
    }

    private Student mapStudentDtoToEntity(StudentDto studentDto){
        Student student = new Student();
        student.setId(studentDto.getId());
        student.setNim(studentDto.getNim());
        student.setFirstName(studentDto.getFirstName());
        student.setMidName(studentDto.getMidName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(studentDto.getAddress());
        student.setGender(studentDto.getGender());
        student.setDob(studentDto.getDob());
        student.setFile_name(studentDto.getFileName());
        student.setFileDownloadUri(studentDto.getFileDownloadUri());

        return student;
    }

    private StudentDto mapStudentEntityToDto(Student student){
        StudentDto studentDto = new StudentDto();
        studentDto.setId(student.getId());
        studentDto.setNim(student.getNim());
        studentDto.setFirstName(student.getFirstName());
        studentDto.setMidName(student.getMidName());
        studentDto.setLastName(student.getLastName());
        studentDto.setAddress(student.getAddress());
        studentDto.setGender(student.getGender());
        studentDto.setDob(student.getDob());
        studentDto.setFileName(student.getFile_name());
        studentDto.setFileDownloadUri(student.getFileDownloadUri());

        return studentDto;
    }

}
