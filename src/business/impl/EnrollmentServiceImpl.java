package business.impl;

import business.IEnrollmentService;
import dao.ICourseDAO;
import dao.IEnrollmentDAO;
import dao.impl.CourseDAOImpl;
import dao.impl.EnrollmentDAOImpl;
import model.Course;
import model.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements IEnrollmentService {
    private final IEnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    @Override
    public void registerCourse(int studentId, int courseId) throws SQLException {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Khóa học không tồn tại");
        }

        Optional<Enrollment> existing = enrollmentDAO.findByStudentIdAndCourseId(studentId, courseId);
        if (existing.isPresent()) {
            String status = existing.get().getStatus();
            if ("WAITING".equals(status) || "CONFIRM".equals(status)) {
                throw new IllegalArgumentException("Bạn đã đăng ký khóa học này rồi!");
            }
            enrollmentDAO.updateStatus(studentId, courseId, "WAITING");
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setRegisteredAt(LocalDateTime.now());
        enrollment.setStatus("WAITING");
        enrollmentDAO.save(enrollment);
    }

    @Override
    public List<Course> getRegisteredCourses(int studentId) throws SQLException {
        return enrollmentDAO.findRegisteredCoursesByStudentId(studentId);
    }

    @Override
    public List<Course> getRegisteredCoursesSorted(int studentId, String field, boolean ascending) throws SQLException {
        List<Course> courses = enrollmentDAO.findRegisteredCoursesByStudentId(studentId);
        Comparator<Course> comparator;
        switch (field) {
            case "name" -> comparator = (c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName());
            default -> comparator = Comparator.comparingInt(Course::getId);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return courses.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public void cancelRegistration(int studentId, int courseId) throws SQLException {
        Optional<Enrollment> enrollmentOpt = enrollmentDAO.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Bạn chưa đăng ký khóa học này");
        }

        Enrollment enrollment = enrollmentOpt.get();
        if (!"WAITING".equals(enrollment.getStatus())) {
            throw new IllegalArgumentException("Không thể hủy khóa học đã được xác nhận");
        }

        enrollmentDAO.updateStatus(studentId, courseId, "CANCEL");
    }

    @Override
    public List<Course> getRegisteredCoursesByStatus(int studentId, String status) throws SQLException {
        return enrollmentDAO.findCoursesByStudentIdAndStatus(studentId, status);
    }

    @Override
    public void updateEnrollmentStatus(int studentId, int courseId, String status) throws SQLException {
        Optional<Enrollment> enrollmentOpt = enrollmentDAO.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollmentOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy đăng ký");
        }
        enrollmentDAO.updateStatus(studentId, courseId, status);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourse(int courseId) throws SQLException {
        Optional<Course> courseOpt = courseDAO.findById(courseId);
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("Khóa học không tồn tại");
        }
        return enrollmentDAO.findByCourseId(courseId);
    }
}
