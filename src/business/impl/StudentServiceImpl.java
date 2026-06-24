package business.impl;

import business.IStudentService;
import dao.IStudentDAO;
import dao.impl.StudentDAOImpl;
import model.Student;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StudentServiceImpl implements IStudentService {
    private final IStudentDAO studentDAO = new StudentDAOImpl();

    @Override
    public Optional<Student> login(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        Optional<Student> studentOpt = studentDAO.findByEmail(email.trim());
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Tài khoản không tồn tại");
        }

        Student student = studentOpt.get();
        if (!student.getPassword().equals(password)) {
            throw new IllegalArgumentException("Mật khẩu không chính xác");
        }

        return studentOpt;
    }

    @Override
    public Student register(Student student) throws SQLException {
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        Optional<Student> existing = studentDAO.findByEmail(student.getEmail().trim());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        return studentDAO.save(student);
    }

    @Override
    public List<Student> findAll() throws SQLException {
        return studentDAO.findAll();
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    @Override
    public void updateStudent(Student student) throws SQLException {
        Optional<Student> existing = studentDAO.findById(student.getId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("ID học viên không tồn tại, vui lòng kiểm tra lại");
        }
        studentDAO.update(student);
    }

    @Override
    public void deleteStudent(int id) throws SQLException {
        Optional<Student> existing = studentDAO.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("ID học viên không tồn tại, vui lòng kiểm tra lại");
        }
        if (existing.get().getRole() == Student.Role.ADMIN) {
            throw new IllegalArgumentException("Không thể xóa tài khoản admin");
        }
        studentDAO.softDelete(id);
    }

    @Override
    public List<Student> search(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentDAO.findAll();
        }
        String lower = keyword.trim().toLowerCase();
        return studentDAO.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(lower)
                        || s.getEmail().toLowerCase().contains(lower)
                        || String.valueOf(s.getId()).equals(lower))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> sort(String field, boolean ascending) throws SQLException {
        List<Student> students = studentDAO.findAll();
        Comparator<Student> comparator;
        switch (field) {
            case "name" -> comparator = (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName());
            default -> comparator = Comparator.comparingInt(Student::getId);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public void changePassword(int studentId, String emailOrPhone, String oldPassword, String newPassword) throws SQLException {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("Email hoặc số điện thoại không được để trống");
        }
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu cũ không được để trống");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (newPassword.length() < 3) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 3 ký tự");
        }

        Optional<Student> studentOpt = studentDAO.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy tài khoản");
        }

        Student student = studentOpt.get();
        boolean match = student.getEmail().equals(emailOrPhone.trim())
                || (student.getPhone() != null && student.getPhone().equals(emailOrPhone.trim()));
        if (!match) {
            throw new IllegalArgumentException("Email hoặc số điện thoại không chính xác");
        }

        if (!student.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        student.setPassword(newPassword);
        studentDAO.update(student);
    }

    @Override
    public boolean verifyEmailOrPhone(int studentId, String emailOrPhone) throws SQLException {
        Optional<Student> studentOpt = studentDAO.findById(studentId);
        if (studentOpt.isEmpty()) return false;
        Student student = studentOpt.get();
        return student.getEmail().equals(emailOrPhone.trim())
                || (student.getPhone() != null && student.getPhone().equals(emailOrPhone.trim()));
    }

    @Override
    public List<Student> findDeletedStudents() throws SQLException {
        return studentDAO.findDeleted();
    }

    @Override
    public void restoreStudent(int id) throws SQLException {
        boolean isDeleted = studentDAO.findDeleted().stream().anyMatch(s -> s.getId() == id);
        if (!isDeleted) {
            throw new IllegalArgumentException("ID học viên không tồn tại hoặc chưa bị xóa");
        }
        studentDAO.restore(id);
    }
}
