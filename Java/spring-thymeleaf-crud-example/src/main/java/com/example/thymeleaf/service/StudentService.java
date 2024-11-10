package com.example.thymeleaf.service;

import com.example.thymeleaf.entity.Student;
import com.example.thymeleaf.repository.AddressRepository;
import com.example.thymeleaf.repository.StudentRepository;

import com.example.thymeleaf.util.SanitizerUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StudentService {

    private AddressRepository addressRepository;
    private StudentRepository studentRepository;

    public Student findById(String id) {
        return this.studentRepository.findById(id).orElseThrow();
    }

    public Student save(Student student) {
        // Sanitize fields in Student and Address
        sanitizeStudent(student);

        // Save sanitized student and address
        this.studentRepository.save(student);
        this.addressRepository.save(student.getAddress());
        return student;
    }

    public Student update(String id, Student student) {
        Student studentDatabase = this.findById(id);

        // Sanitize fields in Student and Address
        sanitizeStudent(student);

        // Copy sanitized properties
        BeanUtils.copyProperties(student, studentDatabase, "id", "createdAt", "updatedAt", "address");
        BeanUtils.copyProperties(student.getAddress(), studentDatabase.getAddress(), "id", "createdAt", "updatedAt", "student");

        return this.studentRepository.save(studentDatabase);
    }

    public void deleteById(String id) {
        this.studentRepository.delete(this.findById(id));
    }

    // Method to sanitize fields in Student and Address entities
    private void sanitizeStudent(Student student) {
        if (student != null) {
            student.setName(SanitizerUtil.sanitize(student.getName()));
            student.setEmail(SanitizerUtil.sanitize(student.getEmail()));

            if (student.getAddress() != null) {
                student.getAddress().setStreet(SanitizerUtil.sanitize(student.getAddress().getStreet()));
                student.getAddress().setCity(SanitizerUtil.sanitize(student.getAddress().getCity()));
            }
        }
    }
}
