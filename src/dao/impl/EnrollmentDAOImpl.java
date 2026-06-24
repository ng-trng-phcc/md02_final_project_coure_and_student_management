package dao.impl;

import dao.IEnrollmentDAO;
import model.Course;
import model.Enrollment;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDAOImpl implements IEnrollmentDAO {

    @Override
    public Optional<Enrollment> findByStudentIdAndCourseId(int studentId, int courseId) throws SQLException {
        String sql = "SELECT id, student_id, course_id, registered_at, status FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setId(rs.getInt("id"));
                    e.setStudentId(rs.getInt("student_id"));
                    e.setCourseId(rs.getInt("course_id"));
                    e.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                    e.setStatus(rs.getString("status"));
                    return Optional.of(e);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Enrollment save(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, registered_at, status) VALUES (?, ?, ?, CAST(? AS enr_status)) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setTimestamp(3, Timestamp.valueOf(enrollment.getRegisteredAt()));
            stmt.setString(4, enrollment.getStatus());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    enrollment.setId(rs.getInt("id"));
                }
            }
        }
        return enrollment;
    }

    @Override
    public void delete(int studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Course> findRegisteredCoursesByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT c.id, c.name, c.duration, c.instructor, c.created_at
                FROM enrollments e
                JOIN courses c ON c.id = e.course_id
                WHERE e.student_id = ?
                ORDER BY e.registered_at
                """;
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDuration(rs.getInt("duration"));
                    c.setInstructor(rs.getString("instructor"));
                    c.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    courses.add(c);
                }
            }
        }
        return courses;
    }

    @Override
    public List<Course> findCoursesByStudentIdAndStatus(int studentId, String status) throws SQLException {
        String sql = """
                SELECT c.id, c.name, c.duration, c.instructor, c.created_at
                FROM enrollments e
                JOIN courses c ON c.id = e.course_id
                WHERE e.student_id = ? AND e.status = CAST(? AS enr_status)
                ORDER BY e.registered_at
                """;
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setDuration(rs.getInt("duration"));
                    c.setInstructor(rs.getString("instructor"));
                    c.setCreatedAt(rs.getDate("created_at").toLocalDate());
                    courses.add(c);
                }
            }
        }
        return courses;
    }

    @Override
    public void updateStatus(int studentId, int courseId, String status) throws SQLException {
        String sql = "UPDATE enrollments SET status = CAST(? AS enr_status) WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, studentId);
            stmt.setInt(3, courseId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Enrollment> findByCourseId(int courseId) throws SQLException {
        String sql = "SELECT id, student_id, course_id, registered_at, status FROM enrollments WHERE course_id = ? ORDER BY registered_at";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setId(rs.getInt("id"));
                    e.setStudentId(rs.getInt("student_id"));
                    e.setCourseId(rs.getInt("course_id"));
                    e.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                    e.setStatus(rs.getString("status"));
                    enrollments.add(e);
                }
            }
        }
        return enrollments;
    }
}
