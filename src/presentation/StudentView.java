package presentation;

import business.ICourseService;
import business.IEnrollmentService;
import business.IStudentService;
import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import dao.IStudentDAO;
import dao.impl.StudentDAOImpl;
import model.Course;
import model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class StudentView {
    private final IStudentService studentService = new StudentServiceImpl();
    private final IStudentDAO studentDAO = new StudentDAOImpl();
    private final ICourseService courseService = new CourseServiceImpl();
    private final IEnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void showMainMenu() {
        while (true) {
            System.out.println("\n========== HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN ==========");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký");
            System.out.println("3. Thoát");
            System.out.print("Chọn chức năng: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> {
                    System.out.println("Bạn đã thoát khỏi chương trình!");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ, vui lòng chọn lại!");
            }
        }
    }

    private void login() {
        System.out.println("\n---------- ĐĂNG NHẬP ----------");
        System.out.print("Nhập username/email: ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("Lỗi: Username/email không được để trống!");
            return;
        }

        try {
            boolean isStudent = input.contains("@gmail.com");
            Student.Role role = isStudent ? Student.Role.STUDENT : Student.Role.ADMIN;
            String roleLabel = role == Student.Role.ADMIN ? "ADMIN" : "HỌC VIÊN";

            Optional<Student> studentOpt = isStudent
                    ? studentDAO.findByEmail(input)
                    : studentDAO.findByName(input);
            if (studentOpt.isEmpty()) {
                System.out.println("LỖI: TÀI KHOẢN KHÔNG TỒN TẠI !");
                return;
            }

            Student student = studentOpt.get();
            System.out.print("Nhập mật khẩu cho " + roleLabel + ": ");
            String password = scanner.nextLine();

            if (!student.getPassword().equals(password)) {
                System.out.println("Lỗi: Mật khẩu không chính xác!");
                return;
            }

            System.out.println("Đăng nhập thành công! Xin chào " + student.getName() + " (" + roleLabel + ")");
            if (role == Student.Role.ADMIN) {
                showAdminMenu();
            } else {
                showStudentMenu(student);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void register() {
        java.util.List<String> errors = new java.util.ArrayList<>();

        System.out.println("\n---------- ĐĂNG KÝ ----------");

        System.out.print("Họ tên: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Mật khẩu: ");
        String password = scanner.nextLine();

        System.out.print("Xác nhận mật khẩu: ");
        String confirmPassword = scanner.nextLine();

        System.out.print("Ngày sinh (yyyy-MM-dd): ");
        String dobStr = scanner.nextLine().trim();

        System.out.print("Giới tính (1-Nam / 0-Nữ): ");
        String sexStr = scanner.nextLine().trim();

        System.out.print("Số điện thoại (+84): ");
        String phone = scanner.nextLine().trim();


        if (name.isEmpty()) errors.add("Họ tên không được để trống");

        if (email.isEmpty()) {
            errors.add("Email không được để trống");
        } else if (!email.endsWith("@gmail.com")) {
            errors.add("Email cần có @gmail.com");
        }

        if (password.isEmpty()) {
            errors.add("Mật khẩu không được để trống");
        } else if (!password.equals(confirmPassword)) {
            errors.add("Mật khẩu xác nhận không khớp");
        }

        if (dobStr.isEmpty()) {
            errors.add("Ngày sinh không được để trống");
        } else {
            try {
                java.time.LocalDate.parse(dobStr);
            } catch (java.time.format.DateTimeParseException e) {
                errors.add("Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)");
            }
        }

        Boolean sex = null;
        if (sexStr.isEmpty()) {
            errors.add("Giới tính không được để trống");
        } else if (!sexStr.equals("0") && !sexStr.equals("1")) {
            errors.add("Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)");
        } else {
            sex = sexStr.equals("1");
        }

        if (phone.isEmpty()) {
            errors.add("Số điện thoại không được để trống");
        } else if (!phone.matches("0\\d{9}")) {
            errors.add("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0");
        }

        if (!errors.isEmpty()) {
            System.out.println("\n==================== LỖI ====================");
            for (String err : errors) {
                System.out.println("| " + err);
            }
            return;
        }

        try {
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setPassword(password);
            student.setDob(java.time.LocalDate.parse(dobStr));
            student.setSex(sex);
            student.setPhone(phone.isEmpty() ? null : phone);
            student.setRole(Student.Role.STUDENT);
            student.setCreatedAt(java.time.LocalDate.now());

            Student saved = studentService.register(student);
            System.out.println("Đăng ký thành công! Mã học viên: " + saved.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void showAdminMenu() {
        while (true) {
            System.out.println("\n---------- MENU ADMIN ----------");
            System.out.println("1. Quản lý khóa học");
            System.out.println("2. Quản lý học viên");
            System.out.println("3. Đăng xuất");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { if (showCourseMenu()) return; }
                case "2" -> { if (showStudentManagementMenu()) return; }
                case "3" -> {
                    System.out.println("Đăng xuất thành công!");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private boolean showCourseMenu() {
        while (true) {
            System.out.println("\n---------- QUẢN LÝ KHÓA HỌC ----------");
            System.out.println("1. Xem danh sách khóa học");
            System.out.println("2. Thêm mới");
            System.out.println("3. Chỉnh sửa");
            System.out.println("4. Xóa");
            System.out.println("5. Tìm kiếm");
            System.out.println("6. Sắp xếp");
            System.out.println("7. Quay lại");
            System.out.println("8. Đăng xuất");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listCourses();
                case "2" -> addCourse();
                case "3" -> editCourse();
                case "4" -> deleteCourse();
                case "5" -> searchCourses();
                case "6" -> sortCourses();
                case "7" -> { return false; }
                case "8" -> {
                    System.out.println("Đăng xuất thành công!");
                    return true;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listCourses() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                System.out.println("Danh sách khóa học trống.");
                return;
            }
            System.out.println("\n---------- DANH SÁCH KHÓA HỌC ----------");
            System.out.println("ID    | Tên                             | Thời lượng | Giảng viên           | Ngày tạo");
            System.out.println("------+---------------------------------+------------+----------------------+------------");
            for (Course c : courses) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void addCourse() {
        System.out.println("\n---------- THÊM KHÓA HỌC ----------");
        System.out.print("Tên khóa học: ");
        String name = scanner.nextLine().trim();
        System.out.print("Thời lượng (số buổi): ");
        String durationStr = scanner.nextLine().trim();
        System.out.print("Tên giảng viên: ");
        String instructor = scanner.nextLine().trim();

        java.util.List<String> errors = new java.util.ArrayList<>();
        if (name.isEmpty()) errors.add("Tên khóa học không được để trống");

        Integer duration = null;
        if (durationStr.isEmpty()) {
            errors.add("Thời lượng không được để trống");
        } else {
            try {
                duration = Integer.parseInt(durationStr);
                if (duration <= 0) errors.add("Thời lượng phải lớn hơn 0");
            } catch (NumberFormatException e) {
                errors.add("Thời lượng phải là số nguyên");
            }
        }

        if (instructor.isEmpty()) errors.add("Tên giảng viên không được để trống");

        if (!errors.isEmpty()) {
            System.out.println("\n===== LỖI =====");
            for (String err : errors) System.out.println("| " + err);
            return;
        }

        try {
            Course course = new Course();
            course.setName(name);
            course.setDuration(duration);
            course.setInstructor(instructor);
            course.setCreatedAt(java.time.LocalDate.now());
            Course saved = courseService.addCourse(course);
            System.out.println("Thêm khóa học thành công! Mã khóa học: " + saved.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void editCourse() {
        System.out.println("\n---------- CHỈNH SỬA KHÓA HỌC ----------");
        System.out.print("Nhập ID khóa học cần sửa: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(id);
            if (courseOpt.isEmpty()) {
                System.out.println("Lỗi: ID khóa học không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Course course = courseOpt.get();
            System.out.println("Thông tin hiện tại:");
            System.out.println("1. Tên: " + course.getName());
            System.out.println("2. Thời lượng: " + course.getDuration());
            System.out.println("3. Giảng viên: " + course.getInstructor());
            System.out.println("Chọn thuộc tính cần sửa (1-3): ");
            String fieldChoice = scanner.nextLine().trim();

            switch (fieldChoice) {
                case "1" -> {
                    System.out.print("Tên mới: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.println("Lỗi: Tên không được để trống"); return; }
                    course.setName(newName);
                }
                case "2" -> {
                    System.out.print("Thời lượng mới: ");
                    String newDur = scanner.nextLine().trim();
                    if (newDur.isEmpty()) { System.out.println("Lỗi: Thời lượng không được để trống"); return; }
                    try {
                        int d = Integer.parseInt(newDur);
                        if (d <= 0) { System.out.println("Lỗi: Thời lượng phải lớn hơn 0"); return; }
                        course.setDuration(d);
                    } catch (NumberFormatException e) {
                        System.out.println("Lỗi: Thời lượng phải là số nguyên"); return;
                    }
                }
                case "3" -> {
                    System.out.print("Giảng viên mới: ");
                    String newInst = scanner.nextLine().trim();
                    if (newInst.isEmpty()) { System.out.println("Lỗi: Tên giảng viên không được để trống"); return; }
                    course.setInstructor(newInst);
                }
                default -> {
                    System.out.println("Lựa chọn không hợp lệ");
                    return;
                }
            }

            courseService.updateCourse(course);
            System.out.println("Cập nhật khóa học thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        System.out.println("\n---------- XÓA KHÓA HỌC ----------");
        System.out.print("Nhập ID khóa học cần xóa: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(id);
            if (courseOpt.isEmpty()) {
                System.out.println("Lỗi: ID khóa học không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Course course = courseOpt.get();
            System.out.println("Bạn sắp xóa: " + course.getName());
            System.out.print("Bạn có chắc chắn muốn xóa (Y/N)? ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                System.out.println("Hủy xóa khóa học.");
                return;
            }

            courseService.deleteCourse(id);
            System.out.println("Xóa khóa học thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchCourses() {
        System.out.println("\n---------- TÌM KIẾM KHÓA HỌC ----------");
        System.out.print("Nhập từ khóa (ID, tên, giảng viên): ");
        String keyword = scanner.nextLine().trim();

        try {
            List<Course> results = courseService.search(keyword);
            if (results.isEmpty()) {
                System.out.println("Không tìm thấy khóa học nào phù hợp.");
                return;
            }
            System.out.println("\nKết quả tìm kiếm (" + results.size() + " khóa học):");
            System.out.println("ID    | Tên                             | Thời lượng | Giảng viên           | Ngày tạo");
            System.out.println("------+---------------------------------+------------+----------------------+------------");
            for (Course c : results) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void sortCourses() {
        System.out.println("\n---------- SẮP XẾP KHÓA HỌC ----------");
        System.out.println("Sắp xếp theo:");
        System.out.println("1. ID");
        System.out.println("2. Tên");
        System.out.print("Chọn: ");
        String fieldChoice = scanner.nextLine().trim();
        String field;
        switch (fieldChoice) {
            case "1" -> field = "id";
            case "2" -> field = "name";
            default -> { System.out.println("Lựa chọn không hợp lệ"); return; }
        }

        System.out.println("Thứ tự:");
        System.out.println("1. Tăng dần");
        System.out.println("2. Giảm dần");
        System.out.print("Chọn: ");
        String orderChoice = scanner.nextLine().trim();
        boolean ascending;
        switch (orderChoice) {
            case "1" -> ascending = true;
            case "2" -> ascending = false;
            default -> { System.out.println("Lựa chọn không hợp lệ"); return; }
        }

        try {
            List<Course> sorted = courseService.sort(field, ascending);
            if (sorted.isEmpty()) {
                System.out.println("Danh sách khóa học trống.");
                return;
            }
            System.out.println("\nDanh sách sau khi sắp xếp:");
            System.out.println("ID    | Tên                             | Thời lượng | Giảng viên           | Ngày tạo");
            System.out.println("------+---------------------------------+------------+----------------------+------------");
            for (Course c : sorted) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private boolean showStudentManagementMenu() {
        while (true) {
            System.out.println("\n---------- QUẢN LÝ HỌC VIÊN ----------");
            System.out.println("1. Hiển thị danh sách học viên");
            System.out.println("2. Thêm học viên mới");
            System.out.println("3. Chỉnh sửa thông tin học viên");
            System.out.println("4. Xóa học viên");
            System.out.println("5. Tìm kiếm học viên");
            System.out.println("6. Sắp xếp học viên");
            System.out.println("7. Quay lại");
            System.out.println("8. Đăng xuất");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listStudents();
                case "2" -> addStudent();
                case "3" -> editStudent();
                case "4" -> deleteStudent();
                case "5" -> searchStudents();
                case "6" -> sortStudents();
                case "7" -> { return false; }
                case "8" -> {
                    System.out.println("Đăng xuất thành công!");
                    return true;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listStudents() {
        try {
            List<Student> students = studentService.findAll();
            if (students.isEmpty()) {
                System.out.println("Danh sách học viên trống.");
                return;
            }
            System.out.println("\n---------- DANH SÁCH HỌC VIÊN ----------");
            System.out.println("ID    | Họ tên              | Ngày sinh   | Email                          | Giới tính | SĐT              | Vai trò  | Ngày tạo");
            System.out.println("------+---------------------+-------------+--------------------------------+-----------+------------------+----------+------------");
            for (Student s : students) {
                System.out.println(s);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void addStudent() {
        System.out.println("\n---------- THÊM HỌC VIÊN ----------");
        System.out.print("Họ tên: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Mật khẩu: ");
        String password = scanner.nextLine();
        System.out.print("Xác nhận mật khẩu: ");
        String confirmPassword = scanner.nextLine();
        System.out.print("Ngày sinh (yyyy-MM-dd): ");
        String dobStr = scanner.nextLine().trim();
        System.out.print("Giới tính (1-Nam / 0-Nữ): ");
        String sexStr = scanner.nextLine().trim();
        System.out.print("Số điện thoại: ");
        String phone = scanner.nextLine().trim();

        java.util.List<String> errors = new java.util.ArrayList<>();
        if (name.isEmpty()) errors.add("Họ tên không được để trống");

        if (email.isEmpty()) {
            errors.add("Email không được để trống");
        } else if (!email.endsWith("@gmail.com")) {
            errors.add("Email phải có đuôi @gmail.com");
        }

        if (password.isEmpty()) {
            errors.add("Mật khẩu không được để trống");
        } else if (!password.equals(confirmPassword)) {
            errors.add("Mật khẩu xác nhận không khớp");
        }

        if (dobStr.isEmpty()) {
            errors.add("Ngày sinh không được để trống");
        } else {
            try {
                java.time.LocalDate.parse(dobStr);
            } catch (java.time.format.DateTimeParseException e) {
                errors.add("Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)");
            }
        }

        Boolean sex = null;
        if (sexStr.isEmpty()) {
            errors.add("Giới tính không được để trống");
        } else if (!sexStr.equals("0") && !sexStr.equals("1")) {
            errors.add("Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)");
        } else {
            sex = sexStr.equals("1");
        }

        if (phone.isEmpty()) {
            errors.add("Số điện thoại không được để trống");
        } else if (!phone.matches("0\\d{9}")) {
            errors.add("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0");
        }

        if (!errors.isEmpty()) {
            System.out.println("\n===== LỖI =====");
            for (String err : errors) System.out.println("| " + err);
            return;
        }

        try {
            Student student = new Student();
            student.setName(name);
            student.setEmail(email);
            student.setPassword(password);
            student.setDob(java.time.LocalDate.parse(dobStr));
            student.setSex(sex);
            student.setPhone(phone);
            student.setRole(Student.Role.STUDENT);
            student.setCreatedAt(java.time.LocalDate.now());
            Student saved = studentService.register(student);
            System.out.println("Thêm học viên thành công! Mã học viên: " + saved.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void editStudent() {
        System.out.println("\n---------- CHỈNH SỬA HỌC VIÊN ----------");
        System.out.print("Nhập ID học viên cần sửa: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            Optional<Student> studentOpt = studentService.findById(id);
            if (studentOpt.isEmpty()) {
                System.out.println("Lỗi: ID học viên không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Student student = studentOpt.get();
            System.out.println("Thông tin hiện tại:");
            System.out.println("1. Họ tên: " + student.getName());
            System.out.println("2. Email: " + student.getEmail());
            System.out.println("3. Số điện thoại: " + student.getPhone());
            System.out.println("4. Ngày sinh: " + student.getDob());
            System.out.println("5. Giới tính: " + (student.getSex() ? "Nam" : "Nữ"));
            System.out.println("6. Mật khẩu");
            System.out.println("Chọn thuộc tính cần sửa (1-6): ");
            String fieldChoice = scanner.nextLine().trim();

            switch (fieldChoice) {
                case "1" -> {
                    System.out.print("Họ tên mới: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.println("Lỗi: Họ tên không được để trống"); return; }
                    student.setName(newName);
                }
                case "2" -> {
                    System.out.print("Email mới: ");
                    String newEmail = scanner.nextLine().trim();
                    if (newEmail.isEmpty()) { System.out.println("Lỗi: Email không được để trống"); return; }
                    if (!newEmail.endsWith("@gmail.com")) { System.out.println("Lỗi: Email phải có đuôi @gmail.com"); return; }
                    student.setEmail(newEmail);
                }
                case "3" -> {
                    System.out.print("Số điện thoại mới: ");
                    String newPhone = scanner.nextLine().trim();
                    if (newPhone.isEmpty()) { System.out.println("Lỗi: SĐT không được để trống"); return; }
                    if (!newPhone.matches("0\\d{9}")) { System.out.println("Lỗi: SĐT phải gồm 10 chữ số, bắt đầu bằng số 0"); return; }
                    student.setPhone(newPhone);
                }
                case "4" -> {
                    System.out.print("Ngày sinh mới (yyyy-MM-dd): ");
                    String newDob = scanner.nextLine().trim();
                    if (newDob.isEmpty()) { System.out.println("Lỗi: Ngày sinh không được để trống"); return; }
                    try {
                        student.setDob(java.time.LocalDate.parse(newDob));
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println("Lỗi: Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)"); return;
                    }
                }
                case "5" -> {
                    System.out.print("Giới tính mới (1-Nam / 0-Nữ): ");
                    String newSex = scanner.nextLine().trim();
                    if (newSex.isEmpty()) { System.out.println("Lỗi: Giới tính không được để trống"); return; }
                    if (!newSex.equals("0") && !newSex.equals("1")) {
                        System.out.println("Lỗi: Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)"); return;
                    }
                    student.setSex(newSex.equals("1"));
                }
                case "6" -> {
                    System.out.print("Mật khẩu mới: ");
                    String newPass = scanner.nextLine();
                    if (newPass.isEmpty()) { System.out.println("Lỗi: Mật khẩu không được để trống"); return; }
                    System.out.print("Xác nhận mật khẩu: ");
                    String confirmPass = scanner.nextLine();
                    if (!newPass.equals(confirmPass)) {
                        System.out.println("Lỗi: Mật khẩu xác nhận không khớp"); return;
                    }
                    student.setPassword(newPass);
                }
                default -> {
                    System.out.println("Lựa chọn không hợp lệ");
                    return;
                }
            }

            studentService.updateStudent(student);
            System.out.println("Cập nhật học viên thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.println("\n---------- XÓA HỌC VIÊN ----------");
        System.out.print("Nhập ID học viên cần xóa: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            Optional<Student> studentOpt = studentService.findById(id);
            if (studentOpt.isEmpty()) {
                System.out.println("Lỗi: ID học viên không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Student student = studentOpt.get();
            System.out.println("Bạn đang thực hiện xóa sinh viên: " + student.getName() + " (" + student.getEmail() + ")");
            System.out.print("Bạn có chắc chắn muốn xóa (Y/N)? ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                System.out.println("Đã hủy xóa học viên.");
                return;
            }

            studentService.deleteStudent(id);
            System.out.println("Xóa học viên thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchStudents() {
        System.out.println("\n---------- TÌM KIẾM HỌC VIÊN ----------");
        System.out.print("Nhập từ khóa (ID, tên, email): ");
        String keyword = scanner.nextLine().trim();

        try {
            List<Student> results = studentService.search(keyword);
            if (results.isEmpty()) {
                System.out.println("Không tìm thấy học viên nào phù hợp.");
                return;
            }
            System.out.println("\nKết quả tìm kiếm (" + results.size() + " học viên):");
            System.out.println("ID    | Họ tên              | Email                          | SĐT              ");
            System.out.println("------+---------------------+--------------------------------+------------------");
            for (Student s : results) {
                System.out.println(String.format("%-5s | %-20s | %-30s | %-15s",
                        s.getId(), s.getName(), s.getEmail(), s.getPhone()));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void sortStudents() {
        System.out.println("\n---------- SẮP XẾP HỌC VIÊN ----------");
        System.out.println("Sắp xếp theo:");
        System.out.println("1. ID");
        System.out.println("2. Tên");
        System.out.print("Chọn: ");
        String fieldChoice = scanner.nextLine().trim();
        String field;
        switch (fieldChoice) {
            case "1" -> field = "id";
            case "2" -> field = "name";
            default -> { System.out.println("Lựa chọn không hợp lệ"); return; }
        }

        System.out.println("Thứ tự:");
        System.out.println("1. Tăng dần");
        System.out.println("2. Giảm dần");
        System.out.print("Chọn: ");
        String orderChoice = scanner.nextLine().trim();
        boolean ascending;
        switch (orderChoice) {
            case "1" -> ascending = true;
            case "2" -> ascending = false;
            default -> { System.out.println("Lựa chọn không hợp lệ"); return; }
        }

        try {
            List<Student> sorted = studentService.sort(field, ascending);
            if (sorted.isEmpty()) {
                System.out.println("Danh sách học viên trống.");
                return;
            }
            System.out.println("\nDanh sách sau khi sắp xếp:");
            System.out.println("ID    | Họ tên              | Email                          | SĐT              ");
            System.out.println("------+---------------------+--------------------------------+------------------");
            for (Student s : sorted) {
                System.out.println(String.format("%-5s | %-20s | %-30s | %-15s",
                        s.getId(), s.getName(), s.getEmail(), s.getPhone()));
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void showStudentMenu(Student student) {
        while (true) {
            System.out.println("\n---------- MENU HỌC VIÊN ----------");
            System.out.println("1. Xem khóa học");
            System.out.println("2. Đăng ký khóa học");
            System.out.println("3. Xem khóa học đã đăng ký");
            System.out.println("4. Hủy đăng ký");
            System.out.println("5. Đổi mật khẩu");
            System.out.println("6. Đăng xuất");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showStudentCourseMenu();
                case "2" -> registerCourse(student);
                case "3" -> viewRegisteredCourses(student);
                case "4" -> cancelRegistration(student);
                case "5" -> { if (changePassword(student)) return; }
                case "6" -> {
                    System.out.println("Đăng xuất thành công!");
                    return;
                }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showStudentCourseMenu() {
        while (true) {
            System.out.println("\n---------- DANH SÁCH KHÓA HỌC ----------");
            System.out.println("1. Xem tất cả khóa học");
            System.out.println("2. Tìm kiếm khóa học");
            System.out.println("3. Quay lại");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllCourses();
                case "2" -> searchAllCourses();
                case "3" -> { return; }
                default -> System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void viewAllCourses() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                System.out.println("Danh sách khóa học trống.");
                return;
            }
            System.out.println("\nID    | Tên                             | Thời lượng | Giảng viên");
            System.out.println("------+---------------------------------+------------+----------------------");
            for (Course c : courses) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchAllCourses() {
        System.out.print("Nhập từ khóa (ID, tên, giảng viên): ");
        String keyword = scanner.nextLine().trim();
        try {
            List<Course> results = courseService.search(keyword);
            if (results.isEmpty()) {
                System.out.println("Không tìm thấy khóa học nào.");
                return;
            }
            System.out.println("\nKết quả tìm kiếm (" + results.size() + " khóa học):");
            System.out.println("ID    | Tên                             | Thời lượng | Giảng viên");
            System.out.println("------+---------------------------------+------------+----------------------");
            for (Course c : results) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void registerCourse(Student student) {
        System.out.println("\n---------- ĐĂNG KÝ KHÓA HỌC ----------");
        System.out.print("Nhập ID khóa học muốn đăng ký: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            enrollmentService.registerCourse(student.getId(), courseId);
            System.out.println("Đăng ký khóa học thành công! Vui lòng chờ xác nhận.");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void viewRegisteredCourses(Student student) {
        try {
            List<Course> courses = enrollmentService.getRegisteredCourses(student.getId());
            if (courses.isEmpty()) {
                System.out.println("Bạn chưa đăng ký khóa học nào.");
                return;
            }

            System.out.println("\nCó muốn sắp xếp không?");
            System.out.println("1. Không (mặc định)");
            System.out.println("2. Theo tên (A-Z)");
            System.out.println("3. Theo tên (Z-A)");
            System.out.print("Chọn: ");
            String sortChoice = scanner.nextLine().trim();

            switch (sortChoice) {
                case "2" -> courses = enrollmentService.getRegisteredCoursesSorted(student.getId(), "name", true);
                case "3" -> courses = enrollmentService.getRegisteredCoursesSorted(student.getId(), "name", false);
            }

            System.out.println("\n---------- KHÓA HỌC ĐÃ ĐĂNG KÝ ----------");
            System.out.println("ID    | Tên                             | Thời lượng | Giảng viên");
            System.out.println("------+---------------------------------+------------+----------------------");
            for (Course c : courses) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void cancelRegistration(Student student) {
        System.out.println("\n---------- HỦY ĐĂNG KÝ KHÓA HỌC ----------");
        System.out.print("Nhập ID khóa học muốn hủy: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Lỗi: ID không được để trống"); return; }

        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: ID phải là số nguyên");
            return;
        }

        try {
            enrollmentService.cancelRegistration(student.getId(), courseId);
            System.out.println("Hủy đăng ký khóa học thành công!");
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private boolean changePassword(Student student) {
        System.out.println("\n---------- ĐỔI MẬT KHẨU ----------");
        System.out.print("Nhập email hoặc số điện thoại: ");
        String emailOrPhone = scanner.nextLine().trim();
        if (emailOrPhone.isEmpty()) { System.out.println("Lỗi: Không được để trống"); return false; }

        System.out.print("Mật khẩu cũ: ");
        String oldPassword = scanner.nextLine();
        if (oldPassword.isEmpty()) { System.out.println("Lỗi: Mật khẩu cũ không được để trống"); return false; }

        System.out.print("Mật khẩu mới: ");
        String newPassword = scanner.nextLine();
        if (newPassword.isEmpty()) { System.out.println("Lỗi: Mật khẩu mới không được để trống"); return false; }

        System.out.print("Xác nhận mật khẩu mới: ");
        String confirmPassword = scanner.nextLine();
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Lỗi: Mật khẩu xác nhận không khớp");
            return false;
        }

        try {
            studentService.changePassword(student.getId(), emailOrPhone, oldPassword, newPassword);
            System.out.println("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.out.println("Lỗi hệ thống: " + e.getMessage());
            return false;
        }
    }
}
