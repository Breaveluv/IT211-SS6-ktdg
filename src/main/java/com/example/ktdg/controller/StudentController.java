package com.example.ktdg.controller;

import com.example.ktdg.model.entity.Student;
import com.example.ktdg.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/students", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    // 1. GET ALL: Lấy danh sách sinh viên
    // Link: GET http://localhost:8080/api/students
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    // 2. GET BY ID: Lấy sinh viên theo ID (Trả về 404 nếu không tìm thấy)
    // Link: GET http://localhost:8080/api/students/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // 3. POST: Thêm sinh viên mới (ID tự sinh)
    // Link: POST http://localhost:8080/api/students
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        student.setId(null); // Đảm bảo ID luôn tự tăng
        Student savedStudent = studentRepository.save(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED); // 201 Created
    }

    // 4. PUT: Cập nhật toàn bộ thông tin
    // Link: PUT http://localhost:8080/api/students/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student studentDetails) {
        return studentRepository.findById(id).map(existingStudent -> {
            existingStudent.setFullName(studentDetails.getFullName());
            existingStudent.setEmail(studentDetails.getEmail());
            existingStudent.setGpa(studentDetails.getGpa());

            Student updatedStudent = studentRepository.save(existingStudent);
            return ResponseEntity.ok(updatedStudent); // 200 OK
        }).orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // 5. PATCH: Cập nhật một phần (Dùng Map để check trường nào gửi lên)
    // Link: PATCH http://localhost:8080/api/students/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<Student> patchStudent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return studentRepository.findById(id).map(existingStudent -> {
            if (updates.containsKey("fullName")) {
                existingStudent.setFullName((String) updates.get("fullName"));
            }
            if (updates.containsKey("email")) {
                existingStudent.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("gpa")) {
                existingStudent.setGpa(Double.valueOf(updates.get("gpa").toString()));
            }

            Student updatedStudent = studentRepository.save(existingStudent);
            return ResponseEntity.ok(updatedStudent); // 200 OK
        }).orElse(ResponseEntity.notFound().build()); // 404 Not Found
    }

    // 6. DELETE: Xóa sinh viên theo ID
    // Link: DELETE http://localhost:8080/api/students/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content (Không có body)
    }
}