package dao;

import model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ICourseDAO {
    List<Course> findAll() throws SQLException;
    Optional<Course> findById(int id) throws SQLException;
    Course save(Course course) throws SQLException;
    void update(Course course) throws SQLException;
    void delete(int id) throws SQLException;
}
