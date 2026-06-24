package business;

import model.Course;
import model.Enrollment;

import java.sql.SQLException;
import java.util.List;

public interface IEnrollmentService {
    void registerCourse(int studentId, int courseId) throws SQLException;
    List<Course> getRegisteredCourses(int studentId) throws SQLException;
    List<Course> getRegisteredCoursesSorted(int studentId, String field, boolean ascending) throws SQLException;
    void cancelRegistration(int studentId, int courseId) throws SQLException;
    List<Course> getRegisteredCoursesByStatus(int studentId, String status) throws SQLException;
    void updateEnrollmentStatus(int studentId, int courseId, String status) throws SQLException;
    List<Enrollment> getEnrollmentsByCourse(int courseId) throws SQLException;
}
