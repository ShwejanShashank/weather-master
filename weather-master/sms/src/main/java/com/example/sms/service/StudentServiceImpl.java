package com.example.sms.service;


import com.example.sms.entity.Student;
import com.example.sms.repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {


    @Autowired
    private StudentRepo studentRepo;

    @Override
    public Student saveStudent(Student student) {
        return studentRepo.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepo.findById(id);
    }

    @Override
    public Optional<Student> updateStudent(Long id, Student newStudent) {
        Optional<Student> existingStudent = studentRepo.findById(id);
        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();
            student.setStudentName(newStudent.getStudentName());
            student.setStudentEmail(newStudent.getStudentEmail());
            student.setStudentAddress(newStudent.getStudentAddress());
            return Optional.of(studentRepo.save(student));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteStudent(Long id) {
        if (studentRepo.existsById(id)) {
            studentRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
