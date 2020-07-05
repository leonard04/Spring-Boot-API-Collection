package com.igaraleon.app.ws.controller;

import com.igaraleon.app.ws.dto.ResponseDto;
import com.igaraleon.app.ws.dto.StudentDto;
import com.igaraleon.app.ws.service.IStudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/api/student")
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    @Qualifier("StudentService")
    private IStudentService student;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Object>> postRegister (@RequestParam("file") MultipartFile file,
                                                             @RequestParam("nim") String nim, @RequestParam("firstName") String firstName,
                                                             @RequestParam("midName") String midName, @RequestParam("lastName") String lastName,
                                                             @RequestParam("address") String address, @RequestParam("gender") String gender,
                                                             @RequestParam("dob") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dob){

        String fileName = student.storeFile(file,nim);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/student/downloadFile/")
                .path(fileName)
                .toUriString();
        StudentDto input = new StudentDto();
        input.setNim(nim);
        input.setFirstName(firstName);
        input.setMidName(midName);
        input.setLastName(lastName);
        input.setAddress(address);
        input.setGender(gender);
        input.setDob(dob);
        input.setFileDownloadUri(fileDownloadUri);
        input.setFileName(fileName);

        Boolean status = student.registerNewStudent(input);
        ResponseDto<Object> res= new ResponseDto<>();
        res.setSuccess(status);
        if (status){
            res.setResult(input);
            res.setMessage("Registrasi Berhasil");
        } else {
            res.setMessage("Registrasi Gagal");
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = student.loadFileAsResource(fileName);
        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto<StudentDto>> updateStudent(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file,
                                                                 @RequestParam("nim") String nim, @RequestParam("firstName") String firstName,
                                                                 @RequestParam("midName") String midName, @RequestParam("lastName") String lastName,
                                                                 @RequestParam("address") String address, @RequestParam("gender") String gender,
                                                                 @RequestParam("dob") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dob){
        String fileName = student.storeFile(file,nim);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/student/downloadFile/")
                .path(fileName)
                .toUriString();

        StudentDto response = student.findStudentById(id);
        if (response == null){
            return ResponseEntity.notFound().build();
        } else {
            response.setNim(nim);
            response.setFirstName(firstName);
            response.setMidName(midName);
            response.setLastName(lastName);
            response.setAddress(address);
            response.setGender(gender);
            response.setDob(dob);
            response.setFileDownloadUri(fileDownloadUri);
            response.setFileName(fileName);
            response = student.updatestudent(response,id);

            ResponseDto<StudentDto> responseDto = new ResponseDto<>();
            responseDto.setMessage("Data Telah Diupdate");
            responseDto.setSuccess(true);
            responseDto.setResult(response);
            return ResponseEntity.ok(responseDto);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto<Object>> deleteStudent (@PathVariable("id") Long id){
        Boolean status = student.deleteStudentById(id);
        ResponseDto<Object> delStat = new ResponseDto<>();
        delStat.setSuccess(status);
        if (status){
            delStat.setMessage("Data Berhasil Dihapus");
        } else {
            delStat.setMessage("Data Gagal Dihapus");
        }
        return ResponseEntity.ok(delStat);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<ResponseDto<StudentDto>> findStudent (@PathVariable("id") Long id){
        StudentDto studentDto = student.findStudentById(id);
        ResponseDto<StudentDto> responseDto = new ResponseDto<>();
        responseDto.setSuccess(true);
        responseDto.setMessage("Hasil pencarian dengan id student: "+id);
        responseDto.setResult(studentDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/showall")
    public ResponseEntity<ResponseDto<List<StudentDto>>> getAllStudent(){
        List<StudentDto> studentDtos = student.getAllStudent();
        ResponseDto<List<StudentDto>> responseDto = new ResponseDto<>();
        responseDto.setSuccess(true);
        responseDto.setMessage("Data semua student");
        responseDto.setResult(studentDtos);
        return ResponseEntity.ok(responseDto);
    }
}
