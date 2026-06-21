package business;

import model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ICourseService {
    List<Course> findAll() throws SQLException;
    Optional<Course> findById(int id) throws SQLException;
    Course addCourse(Course course) throws SQLException;
    void updateCourse(Course course) throws SQLException;
    void deleteCourse(int id) throws SQLException;
    List<Course> search(String keyword) throws SQLException;
    List<Course> sort(String field, boolean ascending) throws SQLException;
}
