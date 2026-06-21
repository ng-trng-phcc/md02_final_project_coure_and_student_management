package dao.impl;

import dao.ICourseDAO;
import model.Course;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements ICourseDAO {

    @Override
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT id, name, duration, instructor, created_at FROM courses ORDER BY id";
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(mapRow(rs));
            }
        }
        return courses;
    }

    @Override
    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT id, name, duration, instructor, created_at FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Course save(Course course) throws SQLException {
        String sql = "INSERT INTO courses (name, duration, instructor, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getDuration());
            stmt.setString(3, course.getInstructor());
            stmt.setDate(4, Date.valueOf(course.getCreatedAt()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    course.setId(rs.getInt("id"));
                }
            }
        }
        return course;
    }

    @Override
    public void update(Course course) throws SQLException {
        String sql = "UPDATE courses SET name = ?, duration = ?, instructor = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, course.getName());
            stmt.setInt(2, course.getDuration());
            stmt.setString(3, course.getInstructor());
            stmt.setInt(4, course.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setName(rs.getString("name"));
        course.setDuration(rs.getInt("duration"));
        course.setInstructor(rs.getString("instructor"));
        course.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return course;
    }
}
