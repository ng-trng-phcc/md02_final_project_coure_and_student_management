package dao.impl;

import dao.IStatsDAO;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsDAOImpl implements IStatsDAO {

    @Override
    public Map<String, Long> getTotals() throws SQLException {
        String sql = "SELECT * FROM fn_get_totals()";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Map<String, Long> result = new HashMap<>();
                result.put("total_courses", rs.getLong("total_courses"));
                result.put("total_students", rs.getLong("total_students"));
                return result;
            }
        }
        return Map.of("total_courses", 0L, "total_students", 0L);
    }

    @Override
    public long countStudentsByCourse(int courseId) throws SQLException {
        String sql = "{ ? = call fn_count_students_by_course(?) }";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.registerOutParameter(1, Types.BIGINT);
            stmt.setInt(2, courseId);
            stmt.execute();
            return stmt.getLong(1);
        }
    }

    private List<Map<String, Object>> executeCourseStatsQuery(String sql) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             CallableStatement stmt = conn.prepareCall(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("name", rs.getString("name"));
                row.put("duration", rs.getInt("duration"));
                row.put("instructor", rs.getString("instructor"));
                row.put("student_count", rs.getLong("student_count"));
                list.add(row);
            }
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getTop5Courses() throws SQLException {
        return executeCourseStatsQuery("SELECT * FROM fn_top_5_courses()");
    }

    @Override
    public List<Map<String, Object>> getCoursesOver10Students() throws SQLException {
        return executeCourseStatsQuery("SELECT * FROM fn_courses_over_10_students()");
    }
}
