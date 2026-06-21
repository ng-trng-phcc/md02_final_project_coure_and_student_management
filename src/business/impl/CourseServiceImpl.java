package business.impl;

import business.ICourseService;
import dao.ICourseDAO;
import dao.impl.CourseDAOImpl;
import model.Course;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CourseServiceImpl implements ICourseService {
    private final ICourseDAO courseDAO = new CourseDAOImpl();

    @Override
    public List<Course> findAll() throws SQLException {
        return courseDAO.findAll();
    }

    @Override
    public Optional<Course> findById(int id) throws SQLException {
        return courseDAO.findById(id);
    }

    @Override
    public Course addCourse(Course course) throws SQLException {
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khóa học không được để trống");
        }
        if (course.getDuration() == null || course.getDuration() <= 0) {
            throw new IllegalArgumentException("Thời lượng phải lớn hơn 0");
        }
        if (course.getInstructor() == null || course.getInstructor().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên giảng viên không được để trống");
        }
        return courseDAO.save(course);
    }

    @Override
    public void updateCourse(Course course) throws SQLException {
        Optional<Course> existing = courseDAO.findById(course.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("ID khóa học không tồn tại, vui lòng kiểm tra lại");
        }
        courseDAO.update(course);
    }

    @Override
    public void deleteCourse(int id) throws SQLException {
        Optional<Course> existing = courseDAO.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("ID khóa học không tồn tại, vui lòng kiểm tra lại");
        }
        courseDAO.delete(id);
    }

    @Override
    public List<Course> search(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return courseDAO.findAll();
        }
        String lowerKeyword = keyword.trim().toLowerCase();
        return courseDAO.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerKeyword)
                        || c.getInstructor().toLowerCase().contains(lowerKeyword)
                        || String.valueOf(c.getId()).equals(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> sort(String field, boolean ascending) throws SQLException {
        List<Course> courses = courseDAO.findAll();
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
}
