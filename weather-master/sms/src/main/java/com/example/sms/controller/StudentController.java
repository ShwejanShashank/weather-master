package com.example.sms.controller;

import com.example.sms.entity.Student;
import com.example.sms.repo.StudentRepo;
import com.example.sms.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class StudentController {
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    StudentService studentService;

    @PostMapping("/api/students")
    public ResponseEntity<Student> saveStudent(@RequestBody Student student) {
       return new ResponseEntity<>(studentService.saveStudent(student), HttpStatus.CREATED);
    }

    @GetMapping("api/students")
    public ResponseEntity<List<Student>> getStudents() {
        log.info("Fetching all students");
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @GetMapping("api/students/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        log.info("Fetching student with id {}", id);
        return studentService.getStudentById(id)
                .map(student -> new ResponseEntity<>(student, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("api/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable long id, @RequestBody Student stud) {
        return studentService.updateStudent(id, stud)
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("api/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable long id) {
        boolean deleted = studentService.deleteStudent(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
