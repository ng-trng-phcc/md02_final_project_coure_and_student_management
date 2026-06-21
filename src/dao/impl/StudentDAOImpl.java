package dao.impl;

import dao.IStudentDAO;
import model.Student;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements IStudentDAO {
    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at FROM students WHERE id = ?";
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
    public Optional<Student> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at FROM students WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByPhone(String phone) throws SQLException {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at FROM students WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at FROM students WHERE name = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Student save(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, dob, email, sex, phone, role, password, created_at) VALUES (?, ?, ?, ?, ?, CAST(? AS student_role), ?, ?) RETURNING id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setDate(2, Date.valueOf(student.getDob()));
            stmt.setString(3, student.getEmail());
            stmt.setBoolean(4, student.getSex());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getRole().name());
            stmt.setString(7, student.getPassword());
            stmt.setDate(8, Date.valueOf(student.getCreatedAt()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    student.setId(rs.getInt("id"));
                }
            }
        }
        return student;
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, dob = ?, email = ?, sex = ?, phone = ?, role = CAST(? AS student_role), password = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.getName());
            stmt.setDate(2, Date.valueOf(student.getDob()));
            stmt.setString(3, student.getEmail());
            stmt.setBoolean(4, student.getSex());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getRole().name());
            stmt.setString(7, student.getPassword());
            stmt.setInt(8, student.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setDob(rs.getDate("dob").toLocalDate());
        student.setEmail(rs.getString("email"));
        student.setSex(rs.getBoolean("sex"));
        student.setPhone(rs.getString("phone"));
        student.setRole(Student.Role.valueOf(rs.getString("role")));
        student.setPassword(rs.getString("password"));
        student.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return student;
    }
}
