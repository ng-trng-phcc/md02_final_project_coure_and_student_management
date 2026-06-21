package business;

import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IStudentService {
    Optional<Student> login(String email, String password) throws SQLException;
    Student register(Student student) throws SQLException;
    List<Student> findAll() throws SQLException;
    Optional<Student> findById(int id) throws SQLException;
    void updateStudent(Student student) throws SQLException;
    void deleteStudent(int id) throws SQLException;
    List<Student> search(String keyword) throws SQLException;
    List<Student> sort(String field, boolean ascending) throws SQLException;
    void changePassword(int studentId, String emailOrPhone, String oldPassword, String newPassword) throws SQLException;
}
