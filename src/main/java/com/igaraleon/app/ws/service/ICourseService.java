package com.igaraleon.app.ws.service;

import com.igaraleon.app.ws.dto.CourseDto;

import java.util.List;

public interface ICourseService {
    Boolean registerNewCourse(CourseDto courseDto);
    CourseDto updatecourse(CourseDto courseDto, Long id);
    Boolean deleteCourseById(Long id);
    CourseDto findCourseById(Long id);
    List<CourseDto> getAllCourse();
}
