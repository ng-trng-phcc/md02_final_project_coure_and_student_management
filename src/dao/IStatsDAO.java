package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IStatsDAO {
    Map<String, Long> getTotals() throws SQLException;
    long countStudentsByCourse(int courseId) throws SQLException;
    List<Map<String, Object>> getTop5Courses() throws SQLException;
    List<Map<String, Object>> getCoursesOver10Students() throws SQLException;
}
