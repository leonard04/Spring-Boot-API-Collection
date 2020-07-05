package com.igaraleon.app.ws.controller;

import com.igaraleon.app.ws.dto.CourseDto;
import com.igaraleon.app.ws.dto.ResponseDto;
import com.igaraleon.app.ws.service.ICourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/course")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    @Qualifier("CourseService")
    private ICourseService course;


    @GetMapping("/showall")
    public ResponseEntity<ResponseDto<List<CourseDto>>> getAllCourse(){
        List<CourseDto> courseDtoList = course.getAllCourse();
        ResponseDto<List<CourseDto>> responseDto = new ResponseDto<>();
        responseDto.setSuccess(true);
        responseDto.setMessage("View All Courses");
        responseDto.setResult(courseDtoList);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<ResponseDto<CourseDto>> findCourse (@PathVariable("id") Long id){
        CourseDto courseDto = course.findCourseById(id);
        ResponseDto<CourseDto> responseDto = new ResponseDto<>();
        responseDto.setSuccess(true);
        responseDto.setMessage("Course with id: "+id);
        responseDto.setResult(courseDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseDto<Object>> deleteCourse(@PathVariable("id") Long id){
        ResponseDto<Object> responseDto = new ResponseDto<>();
        Boolean status = course.deleteCourseById(id);
        Map<String, String> map = new HashMap<>();
        List<Map<String, String>> data = new ArrayList<>();

        if(status){
            map.put("id",String.valueOf(id));
            data.add(0,map);
            responseDto.setSuccess(true);
            responseDto.setMessage("Data has been deleted");
            responseDto.setResult(data);
        } else {
            responseDto.setSuccess(false);
            responseDto.setMessage("No data");
        }

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Object>> inputCourse (@RequestBody CourseDto bodyInput){
        ResponseDto<Object> response = new ResponseDto<>();
        Boolean status = course.registerNewCourse(bodyInput);
        response.setSuccess(status);
        if(status){
            response.setMessage("Success");
            response.setResult(bodyInput);
        } else {
            response.setMessage("Failed");
            response.setResult(null);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseDto<Object>> updateCourse(@PathVariable("id") Long id,
                                                            @RequestBody CourseDto bodyInput){
        ResponseDto<Object> response = new ResponseDto<>();
        CourseDto cek = course.findCourseById(id);
        if (cek == null){
            return ResponseEntity.notFound().build();
        }
        cek = course.updatecourse(bodyInput,id);
        response.setSuccess(true);
        response.setMessage("Update Successful");
        response.setResult(cek);
        return ResponseEntity.ok(response);
    }
}
