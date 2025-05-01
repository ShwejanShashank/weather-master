package com.example.sms.service;

import com.example.sms.entity.Student;

import java.util.List;
import java.util.Optional;


public interface StudentService {
        Student saveStudent(Student student);
        List<Student> getAllStudents();
        Optional<Student> getStudentById(Long id);
        Optional<Student> updateStudent(Long id, Student student);
        boolean deleteStudent(Long id);
}

