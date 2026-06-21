package dao;

import model.Course;
import model.Enrollment;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IEnrollmentDAO {
    Optional<Enrollment> findByStudentIdAndCourseId(int studentId, int courseId) throws SQLException;
    Enrollment save(Enrollment enrollment) throws SQLException;
    void delete(int studentId, int courseId) throws SQLException;
    List<Course> findRegisteredCoursesByStudentId(int studentId) throws SQLException;
}
