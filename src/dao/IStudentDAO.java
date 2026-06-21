package dao;

import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IStudentDAO {
    List<Student> findAll() throws SQLException;
    Optional<Student> findById(int id) throws SQLException;
    Optional<Student> findByEmail(String email) throws SQLException;
    Optional<Student> findByPhone(String phone) throws SQLException;
    Optional<Student> findByName(String name) throws SQLException;
    Student save(Student student) throws SQLException;
    void update(Student student) throws SQLException;
    void delete(int id) throws SQLException;
}
