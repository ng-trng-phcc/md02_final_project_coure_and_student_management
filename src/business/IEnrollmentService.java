package business;

import model.Course;

import java.sql.SQLException;
import java.util.List;

public interface IEnrollmentService {
    void registerCourse(int studentId, int courseId) throws SQLException;
    List<Course> getRegisteredCourses(int studentId) throws SQLException;
    List<Course> getRegisteredCoursesSorted(int studentId, String field, boolean ascending) throws SQLException;
    void cancelRegistration(int studentId, int courseId) throws SQLException;
}
