package com.igaraleon.app.ws.service;

import com.igaraleon.app.ws.dto.CourseDto;
import com.igaraleon.app.ws.entity.Course;
import com.igaraleon.app.ws.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Qualifier("CourseService")
public class CourseService implements ICourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Boolean registerNewCourse(CourseDto courseDto) {
        Course course = mapCourseDtoToEntity(courseDto);
        Course regCourse = courseRepository.save(course);
        if (regCourse != null){
            return true;
        }

        return  false;
    }

    @Override
    public CourseDto updatecourse(CourseDto courseDto, Long id) {
        Course saveResult = null;
        Course expectedUpdate;

        Optional<Course> findCourse = courseRepository.findById(id);
        if (findCourse.isPresent()){
            expectedUpdate = mapCourseDtoToEntity(courseDto);
            expectedUpdate.setId(id);
            saveResult = courseRepository.save(expectedUpdate);
        }

        return mapCourseEntityToDto(saveResult);
    }

    @Override
    public Boolean deleteCourseById(Long id) {
        courseRepository.deleteById(id);
        return true;
    }

    @Override
    public CourseDto findCourseById(Long id) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        CourseDto result = new CourseDto();
        if (courseOptional.isPresent()){
            result = mapCourseEntityToDto(courseOptional.get());
        }

        return  result;
    }

    @Override
    public List<CourseDto> getAllCourse() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDto> courseDtos = new ArrayList<>();

        for (Course course: courses) {
            CourseDto courseDto = mapCourseEntityToDto(course);
            courseDtos.add(courseDto);
        }
        return courseDtos;
    }

    private Course mapCourseDtoToEntity(CourseDto courseDto){
        Course course = new Course();
        course.setId(courseDto.getId());
        course.setName(courseDto.getName());
        course.setSemester(courseDto.getSemester());
        course.setSks(courseDto.getSks());
        return course;
    }
    private CourseDto mapCourseEntityToDto(Course course){
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setName(course.getName());
        courseDto.setSemester(course.getSemester());
        courseDto.setSks(course.getSks());
        return courseDto;
    }

}
