package presentation;

import business.ICourseService;
import business.IEnrollmentService;
import business.IStudentService;
import business.impl.CourseServiceImpl;
import business.impl.EnrollmentServiceImpl;
import business.impl.StudentServiceImpl;
import dao.IStatsDAO;
import dao.IStudentDAO;
import dao.impl.StatsDAOImpl;
import dao.impl.StudentDAOImpl;
import model.Course;
import model.Enrollment;
import model.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import utils.ConsoleUI;
import static utils.ConsoleUI.*;

public class StudentView {
    private final IStudentService studentService = new StudentServiceImpl();
    private final IStudentDAO studentDAO = new StudentDAOImpl();
    private final ICourseService courseService = new CourseServiceImpl();
    private final IEnrollmentService enrollmentService = new EnrollmentServiceImpl();
    private final IStatsDAO statsDAO = new StatsDAOImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void showMainMenu() {
        while (true) {
            printMenuTitle("HỆ THỐNG QUẢN LÝ KHÓA HỌC VÀ HỌC VIÊN");
            printLine("  " + LIGHT_GRAY + "1. Đăng nhập" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Đăng ký" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "0. Thoát" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> {
                    System.out.println();
                    printCentered(RED + BOLD + "TẠM BIỆT!" + RESET);
                    printBottomBar();
                    System.out.println();
                    return;
                }
                default -> error("Lựa chọn không hợp lệ, vui lòng chọn lại!");
            }
        }
    }

    private void login() {
        while (true) {
            printHeader("ĐĂNG NHẬP");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Username/email" + RESET + " (nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            String input = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + input);
            if ("0".equals(input)) { printEmptyLine(); printBottomBar(); return; }
            if (input.isEmpty()) {
                printLine(RED + "[ERROR] Username/email không được để trống!" + RESET);
                printBottomBar();
                continue;
            }

            printLine(LIGHT_GRAY + "Mật khẩu" + RESET);
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            String password = scanner.nextLine();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + repeat("*", password.length()));
            if (password.isEmpty()) {
                printLine(RED + "[ERROR] Mật khẩu không được để trống!" + RESET);
                printBottomBar();
                continue;
            }

            try {
                boolean isStudent = input.contains("@gmail.com");
                Optional<Student> studentOpt = isStudent
                        ? studentDAO.findByEmailIncludingDeleted(input)
                        : studentDAO.findByNameIncludingDeleted(input);
                if (studentOpt.isEmpty()) {
                    printLine(RED + "[ERROR] Tài khoản hoặc mật khẩu không chính xác!" + RESET);
                    printBottomBar();
                    continue;
                }

                Student student = studentOpt.get();
                if (student.isDeleted()) {
                    printLine(RED + "[ERROR] Tài khoản này đã bị xóa, hãy liên hệ admin!" + RESET);
                    printBottomBar();
                    continue;
                }

                boolean expectedAdmin = !input.contains("@gmail.com");
                if (expectedAdmin && student.getRole() != Student.Role.ADMIN) {
                    printLine(RED + "[ERROR] Tài khoản hoặc mật khẩu không chính xác!" + RESET);
                    printBottomBar();
                    continue;
                }
                if (!expectedAdmin && student.getRole() != Student.Role.STUDENT) {
                    printLine(RED + "[ERROR] Tài khoản hoặc mật khẩu không chính xác!" + RESET);
                    printBottomBar();
                    continue;
                }

                if (!student.getPassword().equals(password)) {
                    printLine(RED + "[ERROR] Tài khoản hoặc mật khẩu không chính xác!" + RESET);
                    printBottomBar();
                    continue;
                }
                Student.Role role = student.getRole();
                String roleLabel = role == Student.Role.ADMIN ? "ADMIN" : "HỌC VIÊN";
                printEmptyLine();
                printBottomBar();
                System.out.println();
                success("Đăng nhập thành công !");
                System.out.println();
                if (role == Student.Role.ADMIN) {
                    showAdminMenu();
                } else {
                    showStudentMenu(student);
                }
                return;
            } catch (SQLException e) {
                printLine(RED + "[ERROR] Lỗi hệ thống: " + e.getMessage() + RESET);
                printBottomBar();
            }
        }
    }

    private void register() {
        String name;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Họ tên" + RESET + " (nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            name = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + name);
            if ("0".equals(name)) { printEmptyLine(); printBottomBar(); return; }
            if (name.isEmpty()) {
                printLine(RED + "[ERROR] Họ tên không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            break;
        }

        String email;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Email" + RESET + " (nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            email = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + email);
            if ("0".equals(email)) { printEmptyLine(); printBottomBar(); return; }
            if (email.isEmpty()) {
                printLine(RED + "[ERROR] Email không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            if (!email.endsWith("@gmail.com")) {
                printLine(RED + "[ERROR] Email phải có đuôi @gmail.com!" + RESET);
                printBottomBar();
                continue;
            }
            break;
        }

        String password;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Mật khẩu" + RESET + " (nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            password = scanner.nextLine();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + repeat("*", password.length()));
            if ("0".equals(password)) { printEmptyLine(); printBottomBar(); return; }
            if (password.isEmpty()) {
                printLine(RED + "[ERROR] Mật khẩu không được để trống!" + RESET);
                printBottomBar();
                continue;
            }

            printLine(LIGHT_GRAY + "Xác nhận mật khẩu" + RESET);
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            String confirmPassword = scanner.nextLine();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + repeat("*", confirmPassword.length()));
            if (!password.equals(confirmPassword)) {
                printLine(RED + "[ERROR] Mật khẩu xác nhận không khớp!" + RESET);
                printBottomBar();
                continue;
            }
            break;
        }

        String dobStr;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Ngày sinh" + RESET + " (yyyy-MM-dd, nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            dobStr = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + dobStr);
            if ("0".equals(dobStr)) { printEmptyLine(); printBottomBar(); return; }
            if (dobStr.isEmpty()) {
                printLine(RED + "[ERROR] Ngày sinh không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            try {
                java.time.LocalDate.parse(dobStr);
            } catch (java.time.format.DateTimeParseException e) {
                printLine(RED + "[ERROR] Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)!" + RESET);
                printBottomBar();
                continue;
            }
            break;
        }

        Boolean sex;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Giới tính" + RESET + " (1-Nam / 0-Nữ, nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            String sexStr = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + sexStr);
            if ("0".equals(sexStr)) { printEmptyLine(); printBottomBar(); return; }
            if (sexStr.isEmpty()) {
                printLine(RED + "[ERROR] Giới tính không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            if (!sexStr.equals("0") && !sexStr.equals("1")) {
                printLine(RED + "[ERROR] Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)!" + RESET);
                printBottomBar();
                continue;
            }
            sex = sexStr.equals("1");
            break;
        }

        String phone;
        while (true) {
            printHeader("ĐĂNG KÝ TÀI KHOẢN");
            printEmptyLine();
            printLine(LIGHT_GRAY + "Số điện thoại" + RESET + " (nhập 0 để quay lại) ");
            System.out.print(GRAY + VERTICAL + RESET + "  " + CYAN + "> " + RESET);
            phone = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(CYAN + "> " + RESET + phone);
            if ("0".equals(phone)) { printEmptyLine(); printBottomBar(); return; }
            if (phone.isEmpty()) {
                printLine(RED + "[ERROR] Số điện thoại không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            if (!phone.matches("0\\d{9}")) {
                printLine(RED + "[ERROR] Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!" + RESET);
                printBottomBar();
                continue;
            }
            break;
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
            success("Đăng ký thành công! Mã học viên: " + saved.getId());
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void showAdminMenu() {
        while (true) {
            printMenuTitle("MENU ADMIN");
            printLine("  " + LIGHT_GRAY + "1. Quản lý khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Quản lý học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "3. Quản lý đăng ký khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Thống kê" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "5. Đăng xuất" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { if (showCourseMenu()) return; }
                case "2" -> { if (showStudentManagementMenu()) return; }
                case "3" -> { if (showEnrollmentManagementMenu()) return; }
                case "4" -> showStatsMenu();
                case "5" -> {
                    System.out.println();
                    success("Đăng xuất thành công!");
                    return;
                }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private boolean showCourseMenu() {
        while (true) {
            printMenuTitle("QUẢN LÝ KHÓA HỌC");
            printLine("  " + LIGHT_GRAY + "1. Xem danh sách khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Thêm mới" + RESET);
            printLine("  " + LIGHT_GRAY + "3. Chỉnh sửa" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Xóa" + RESET);
            printLine("  " + LIGHT_GRAY + "5. Tìm kiếm" + RESET);
            printLine("  " + LIGHT_GRAY + "6. Sắp xếp" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "7. Quay lại" + RESET);
            printLine("  " + LIGHT_GRAY + "8. Đăng xuất" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
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
                    System.out.println();
                    success("Đăng xuất thành công!");
                    return true;
                }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listCourses() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                warning("Danh sách khóa học trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : courses) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor(),
                        c.getCreatedAt().toString()
                });
            }
            printTable("DANH SÁCH CÁC KHÓA HỌC",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên", "Ngày tạo"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void addCourse() {
        String name;
        while (true) {
            printHeader("THÊM KHÓA HỌC");
            System.out.print("  Tên khóa học (nhập 0 để quay lại): ");
            name = scanner.nextLine().trim();
            if ("0".equals(name)) { printBottomBar(); return; }
            if (name.isEmpty()) { printLine(RED + "[ERROR] Tên khóa học không được để trống!" + RESET); printBottomBar(); continue; }
            printBottomBar();
            break;
        }

        int duration;
        while (true) {
            printHeader("THÊM KHÓA HỌC");
            System.out.print("  Thời lượng (số buổi) (nhập 0 để quay lại): ");
            String durationStr = scanner.nextLine().trim();
            if ("0".equals(durationStr)) { printBottomBar(); return; }
            if (durationStr.isEmpty()) { printLine(RED + "[ERROR] Thời lượng không được để trống!" + RESET); printBottomBar(); continue; }
            try {
                duration = Integer.parseInt(durationStr);
                if (duration <= 0) { printLine(RED + "[ERROR] Thời lượng phải lớn hơn 0!" + RESET); printBottomBar(); continue; }
            } catch (NumberFormatException e) {
                printLine(RED + "[ERROR] Thời lượng phải là số nguyên!" + RESET);
                printBottomBar();
                continue;
            }
            printBottomBar();
            break;
        }

        String instructor;
        while (true) {
            printHeader("THÊM KHÓA HỌC");
            System.out.print("  Tên giảng viên (nhập 0 để quay lại): ");
            instructor = scanner.nextLine().trim();
            if ("0".equals(instructor)) { printBottomBar(); return; }
            if (instructor.isEmpty()) { printLine(RED + "[ERROR] Tên giảng viên không được để trống!" + RESET); printBottomBar(); continue; }
            printBottomBar();
            break;
        }

        try {
            Course course = new Course();
            course.setName(name);
            course.setDuration(duration);
            course.setInstructor(instructor);
            course.setCreatedAt(java.time.LocalDate.now());
            Course saved = courseService.addCourse(course);
            success("Thêm khóa học thành công! Mã khóa học: " + saved.getId());
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void editCourse() {
        printHeader("CHỈNH SỬA KHÓA HỌC");
        listCoursesInline();
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học cần sửa" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(id);
            if (courseOpt.isEmpty()) {
                printBottomBar();
                error("ID khóa học không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Course course = courseOpt.get();
            printLine(LIGHT_GRAY + "Thông tin hiện tại:" + RESET);
            printLine(CYAN + "1" + RESET + "  " + LIGHT_GRAY + "Tên:" + RESET + " " + course.getName());
            printLine(CYAN + "2" + RESET + "  " + LIGHT_GRAY + "Thời lượng:" + RESET + " " + course.getDuration());
            printLine(CYAN + "3" + RESET + "  " + LIGHT_GRAY + "Giảng viên:" + RESET + " " + course.getInstructor());
            printLine(CYAN + "4" + RESET + "  " + LIGHT_GRAY + "Quay lại" + RESET);
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Chọn thuộc tính cần sửa" + RESET + " (1-4): ");
            String fieldChoice = scanner.nextLine().trim();

            switch (fieldChoice) {
                case "1" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Tên mới" + RESET + ": ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Tên không được để trống"); return; }
                    if (newName.equals(course.getName())) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Tên của khóa học đang là '" + course.getName() + "' rồi");
                        return;
                    }
                    course.setName(newName);
                }
                case "2" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Thời lượng mới" + RESET + ": ");
                    String newDur = scanner.nextLine().trim();
                    if (newDur.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Thời lượng không được để trống"); return; }
                    try {
                        int d = Integer.parseInt(newDur);
                        if (d <= 0) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Thời lượng phải lớn hơn 0"); return; }
                        if (d == course.getDuration()) {
                            System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Thời lượng của khóa học đang là " + course.getDuration() + " buổi rồi");
                            return;
                        }
                        course.setDuration(d);
                    } catch (NumberFormatException e) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); error("Thời lượng phải là số nguyên"); return;
                    }
                }
                case "3" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Giảng viên mới" + RESET + ": ");
                    String newInst = scanner.nextLine().trim();
                    if (newInst.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Tên giảng viên không được để trống"); return; }
                    if (newInst.equals(course.getInstructor())) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Giảng viên của khóa học đang là '" + course.getInstructor() + "' rồi");
                        return;
                    }
                    course.setInstructor(newInst);
                }
                case "4" -> { return; }
                default -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  ");
                    error("Lựa chọn không hợp lệ");
                    return;
                }
            }

            courseService.updateCourse(course);
            success("Cập nhật khóa học thành công!");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void listCoursesInline() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                printLine(LIGHT_GRAY + "  (Danh sách trống)" + RESET);
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : courses) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable(null,
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        printHeader("XÓA KHÓA HỌC");
        listCoursesInline();
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học cần xóa" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(id);
            if (courseOpt.isEmpty()) {
                printBottomBar();
                error("ID khóa học không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Course course = courseOpt.get();
            printLine("  " + LIGHT_GRAY + "Bạn có chắc chắn muốn xóa khóa học " + RESET + course.getName() + LIGHT_GRAY + "?" + RESET);
            printEmptyLine();
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + YELLOW + "Nhập Y để xác nhận, phím bất kỳ để hủy" + RESET + ": ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                printBottomBar();
                warning("Đã hủy xóa khóa học.");
                return;
            }

            courseService.deleteCourse(id);
            printBottomBar();
            success("Xóa khóa học thành công!");
        } catch (IllegalArgumentException e) {
            printBottomBar();
            error(e.getMessage());
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchCourses() {
        printHeader("TÌM KIẾM KHÓA HỌC");
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập từ khóa" + RESET + " (ID, tên, giảng viên): ");
        String keyword = scanner.nextLine().trim();

        try {
            List<Course> results = courseService.search(keyword);
            if (results.isEmpty()) {
                printBottomBar();
                warning("Không tìm thấy khóa học nào phù hợp.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : results) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor(),
                        c.getCreatedAt().toString()
                });
            }
            printTable("KẾT QUẢ TÌM KIẾM (" + results.size() + " khóa học)",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên", "Ngày tạo"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void sortCourses() {
        printHeader("SẮP XẾP KHÓA HỌC");
        printLine(LIGHT_GRAY + "  Sắp xếp theo:" + RESET);
        printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "ID" + RESET);
        printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Tên" + RESET);
        printEmptyLine();
        printBottomBar();
        printPrompt();
        String fieldChoice = scanner.nextLine().trim();
        String field;
        switch (fieldChoice) {
            case "1" -> field = "id";
            case "2" -> field = "name";
            default -> { error("Lựa chọn không hợp lệ"); return; }
        }

        printHeader("SẮP XẾP KHÓA HỌC");
        printLine(LIGHT_GRAY + "  Thứ tự:" + RESET);
        printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "Tăng dần" + RESET);
        printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Giảm dần" + RESET);
        printEmptyLine();
        printBottomBar();
        printPrompt();
        String orderChoice = scanner.nextLine().trim();
        boolean ascending;
        switch (orderChoice) {
            case "1" -> ascending = true;
            case "2" -> ascending = false;
            default -> { error("Lựa chọn không hợp lệ"); return; }
        }

        try {
            List<Course> sorted = courseService.sort(field, ascending);
            if (sorted.isEmpty()) {
                warning("Danh sách khóa học trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : sorted) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor(),
                        c.getCreatedAt().toString()
                });
            }
            printTable("KẾT QUẢ SẮP XẾP",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên", "Ngày tạo"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private boolean showStudentManagementMenu() {
        while (true) {
            printMenuTitle("QUẢN LÝ HỌC VIÊN");
            printLine("  " + LIGHT_GRAY + "1. Hiển thị danh sách học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Thêm học viên mới" + RESET);
            printLine("  " + LIGHT_GRAY + "3. Chỉnh sửa thông tin học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Xóa học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "5. Tìm kiếm học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "6. Sắp xếp học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "7. Khôi phục tài khoản đã xóa" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "8. Quay lại" + RESET);
            printLine("  " + LIGHT_GRAY + "9. Đăng xuất" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> listStudents();
                case "2" -> addStudent();
                case "3" -> editStudent();
                case "4" -> deleteStudent();
                case "5" -> searchStudents();
                case "6" -> sortStudents();
                case "7" -> restoreStudent();
                case "8" -> { return false; }
                case "9" -> {
                    System.out.println();
                    success("Đăng xuất thành công!");
                    return true;
                }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void listStudents() {
        try {
            List<Student> students = studentService.findAll();
            if (students.isEmpty()) {
                warning("Danh sách học viên trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Student s : students) {
                data.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getDob() != null ? s.getDob().toString() : "",
                        s.getEmail(),
                        s.getSex() != null ? (s.getSex() ? "Nam" : "Nữ") : "",
                        s.getPhone(),
                        s.getRole() != null ? s.getRole().toString() : "",
                        s.getCreatedAt() != null ? s.getCreatedAt().toString() : ""
                });
            }
            printTable("DANH SÁCH CÁC HỌC VIÊN",
                    new String[]{"ID", "Họ tên", "Ngày sinh", "Email", "Giới tính", "SĐT", "Vai trò", "Ngày tạo"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void addStudent() {
        String name;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Họ tên (nhập 0 để quay lại): ");
            name = scanner.nextLine().trim();
            if ("0".equals(name)) { printBottomBar(); return; }
            if (name.isEmpty()) { printLine(RED + "[ERROR] Họ tên không được để trống!" + RESET); printBottomBar(); continue; }
            printBottomBar();
            break;
        }

        String email;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Email (nhập 0 để quay lại): ");
            email = scanner.nextLine().trim();
            if ("0".equals(email)) { printBottomBar(); return; }
            if (email.isEmpty()) { printLine(RED + "[ERROR] Email không được để trống!" + RESET); printBottomBar(); continue; }
            if (!email.endsWith("@gmail.com")) {
                printLine(RED + "[ERROR] Email phải có đuôi @gmail.com!" + RESET);
                printBottomBar();
                continue;
            }
            printBottomBar();
            break;
        }

        String password;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Mật khẩu (nhập 0 để quay lại): ");
            password = scanner.nextLine();
            if ("0".equals(password)) { printBottomBar(); return; }
            if (password.isEmpty()) { printLine(RED + "[ERROR] Mật khẩu không được để trống!" + RESET); printBottomBar(); continue; }

            System.out.print("  Xác nhận mật khẩu: ");
            String confirmPassword = scanner.nextLine();
            if (!password.equals(confirmPassword)) {
                printLine(RED + "[ERROR] Mật khẩu xác nhận không khớp!" + RESET);
                printBottomBar();
                continue;
            }
            printBottomBar();
            break;
        }

        String dobStr;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Ngày sinh (yyyy-MM-dd) (nhập 0 để quay lại): ");
            dobStr = scanner.nextLine().trim();
            if ("0".equals(dobStr)) { printBottomBar(); return; }
            if (dobStr.isEmpty()) { printLine(RED + "[ERROR] Ngày sinh không được để trống!" + RESET); printBottomBar(); continue; }
            try {
                java.time.LocalDate.parse(dobStr);
            } catch (java.time.format.DateTimeParseException e) {
                printLine(RED + "[ERROR] Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)!" + RESET);
                printBottomBar();
                continue;
            }
            printBottomBar();
            break;
        }

        Boolean sex;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Giới tính (1-Nam / 0-Nữ) (nhập 0 để quay lại): ");
            String sexStr = scanner.nextLine().trim();
            if ("0".equals(sexStr)) { printBottomBar(); return; }
            if (sexStr.isEmpty()) { printLine(RED + "[ERROR] Giới tính không được để trống!" + RESET); printBottomBar(); continue; }
            if (!sexStr.equals("0") && !sexStr.equals("1")) {
                printLine(RED + "[ERROR] Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)!" + RESET);
                printBottomBar();
                continue;
            }
            sex = sexStr.equals("1");
            printBottomBar();
            break;
        }

        String phone;
        while (true) {
            printHeader("THÊM HỌC VIÊN");
            System.out.print("  Số điện thoại (nhập 0 để quay lại): ");
            phone = scanner.nextLine().trim();
            if ("0".equals(phone)) { printBottomBar(); return; }
            if (phone.isEmpty()) { printLine(RED + "[ERROR] Số điện thoại không được để trống!" + RESET); printBottomBar(); continue; }
            if (!phone.matches("0\\d{9}")) {
                printLine(RED + "[ERROR] Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0!" + RESET);
                printBottomBar();
                continue;
            }
            printBottomBar();
            break;
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
            success("Thêm học viên thành công! Mã học viên: " + saved.getId());
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void editStudent() {
        printHeader("CHỈNH SỬA HỌC VIÊN");
        listStudentsInline();
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID học viên cần sửa" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Student> studentOpt = studentService.findById(id);
            if (studentOpt.isEmpty()) {
                printBottomBar();
                error("ID học viên không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Student student = studentOpt.get();
            printLine(LIGHT_GRAY + "  Thông tin hiện tại:" + RESET);
            printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "Họ tên:" + RESET + " " + student.getName());
            printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Email:" + RESET + " " + student.getEmail());
            printLine(CYAN + "  3" + RESET + "  " + LIGHT_GRAY + "Số điện thoại:" + RESET + " " + student.getPhone());
            printLine(CYAN + "  4" + RESET + "  " + LIGHT_GRAY + "Ngày sinh:" + RESET + " " + student.getDob());
            printLine(CYAN + "  5" + RESET + "  " + LIGHT_GRAY + "Giới tính:" + RESET + " " + (student.getSex() ? "Nam" : "Nữ"));
            printLine(CYAN + "  6" + RESET + "  " + LIGHT_GRAY + "Mật khẩu" + RESET);
            printLine(CYAN + "  7" + RESET + "  " + LIGHT_GRAY + "Quay lại" + RESET);
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Chọn thuộc tính cần sửa" + RESET + " (1-7): ");
            String fieldChoice = scanner.nextLine().trim();

            switch (fieldChoice) {
                case "1" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Họ tên mới" + RESET + ": ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Họ tên không được để trống"); return; }
                    if (newName.equals(student.getName())) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Họ tên của học viên đang là '" + student.getName() + "' rồi");
                        return;
                    }
                    student.setName(newName);
                }
                case "2" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Email mới" + RESET + ": ");
                    String newEmail = scanner.nextLine().trim();
                    if (newEmail.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Email không được để trống"); return; }
                    if (!newEmail.endsWith("@gmail.com")) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Email phải có đuôi @gmail.com"); return; }
                    if (newEmail.equals(student.getEmail())) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Email của học viên '" + student.getName() + "' đang là " + student.getEmail() + " rồi");
                        return;
                    }
                    student.setEmail(newEmail);
                }
                case "3" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Số điện thoại mới" + RESET + ": ");
                    String newPhone = scanner.nextLine().trim();
                    if (newPhone.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("SĐT không được để trống"); return; }
                    if (!newPhone.matches("0\\d{9}")) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("SĐT phải gồm 10 chữ số, bắt đầu bằng số 0"); return; }
                    if (newPhone.equals(student.getPhone())) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Số điện thoại của học viên '" + student.getName() + "' đang là " + student.getPhone() + " rồi");
                        return;
                    }
                    student.setPhone(newPhone);
                }
                case "4" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Ngày sinh mới" + RESET + " (yyyy-MM-dd): ");
                    String newDob = scanner.nextLine().trim();
                    if (newDob.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Ngày sinh không được để trống"); return; }
                    try {
                        java.time.LocalDate parsedDob = java.time.LocalDate.parse(newDob);
                        if (parsedDob.equals(student.getDob())) {
                            System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Ngày sinh của học viên '" + student.getName() + "' đang là " + student.getDob() + " rồi");
                            return;
                        }
                        student.setDob(parsedDob);
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); error("Định dạng ngày sinh không hợp lệ (yyyy-MM-dd)"); return;
                    }
                }
                case "5" -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Giới tính mới" + RESET + " (1-Nam / 0-Nữ): ");
                    String newSex = scanner.nextLine().trim();
                    if (newSex.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Giới tính không được để trống"); return; }
                    if (!newSex.equals("0") && !newSex.equals("1")) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); error("Giới tính chỉ được nhập 1 (Nam) hoặc 0 (Nữ)"); return;
                    }
                    String currentSex = student.getSex() ? "Nam" : "Nữ";
                    String newSexLabel = newSex.equals("1") ? "Nam" : "Nữ";
                    if (newSexLabel.equals(currentSex)) {
                        System.out.print(GRAY + VERTICAL + RESET + "  "); warning("Giới tính của học viên '" + student.getName() + "' đang là " + currentSex + " rồi");
                        return;
                    }
                    student.setSex(newSex.equals("1"));
                }
                case "6" -> {
                    String newPass;
                    while (true) {
                        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Mật khẩu mới" + RESET + " (nhập 0 để quay lại): ");
                        newPass = scanner.nextLine();
                        if ("0".equals(newPass)) { return; }
                        if (newPass.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Mật khẩu không được để trống"); continue; }
                        if (newPass.equals(student.getPassword())) {
                            System.out.print(GRAY + VERTICAL + RESET + "  ");
                            error("Mật khẩu bạn vừa nhập đang trùng với mật khẩu cũ, vui lòng nhập mật khẩu không trùng với mật khẩu cũ");
                            continue;
                        }

                        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Xác nhận mật khẩu" + RESET + ": ");
                        String confirmPass = scanner.nextLine();
                        if (!newPass.equals(confirmPass)) {
                            System.out.print(GRAY + VERTICAL + RESET + "  ");
                            error("Mật khẩu xác nhận không khớp, vui lòng nhập lại");
                            continue;
                        }
                        break;
                    }
                    student.setPassword(newPass);
                }
                case "7" -> { return; }
                default -> {
                    System.out.print(GRAY + VERTICAL + RESET + "  ");
                    error("Lựa chọn không hợp lệ");
                    return;
                }
            }

            studentService.updateStudent(student);
            success("Cập nhật học viên thành công!");
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        printHeader("XÓA HỌC VIÊN");
        listStudentsInline();
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID học viên cần xóa" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Student> studentOpt = studentService.findById(id);
            if (studentOpt.isEmpty()) {
                printBottomBar();
                error("ID học viên không tồn tại, vui lòng kiểm tra lại");
                return;
            }

            Student student = studentOpt.get();
            printLine("  " + LIGHT_GRAY + "Bạn có chắc chắn muốn xóa học viên " + RESET + student.getName() + " (" + student.getEmail() + ")" + LIGHT_GRAY + "?" + RESET);
            printEmptyLine();
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + YELLOW + "Nhập Y để xác nhận, phím bất kỳ để hủy" + RESET + ": ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                printBottomBar();
                warning("Đã hủy xóa học viên.");
                return;
            }

            studentService.deleteStudent(id);
            printBottomBar();
            success("Xóa học viên thành công!");
        } catch (IllegalArgumentException e) {
            printBottomBar();
            error(e.getMessage());
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void restoreStudent() {
        printHeader("KHÔI PHỤC TÀI KHOẢN");
        try {
            List<Student> deletedStudents = studentService.findDeletedStudents();
            if (deletedStudents.isEmpty()) {
                printBottomBar();
                warning("Không có tài khoản nào đang bị xóa.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Student s : deletedStudents) {
                data.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getEmail()
                });
            }
            printTable("TÀI KHOẢN ĐÃ XÓA",
                    new String[]{"ID", "Họ tên", "Email"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID tài khoản muốn khôi phục" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty() || "0".equals(idStr)) { printBottomBar(); return; }
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            List<Student> deletedStudents = studentService.findDeletedStudents();
            Optional<Student> target = deletedStudents.stream().filter(s -> s.getId() == id).findFirst();
            if (target.isEmpty()) {
                printBottomBar();
                error("ID không nằm trong danh sách tài khoản đã xóa");
                return;
            }
            printLine("  " + LIGHT_GRAY + "Bạn có chắc chắn muốn khôi phục tài khoản " + RESET + target.get().getName() + " (" + target.get().getEmail() + ")" + LIGHT_GRAY + "?" + RESET);
            printEmptyLine();
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + YELLOW + "Nhập Y để xác nhận, phím bất kỳ để hủy" + RESET + ": ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                printBottomBar();
                warning("Hủy khôi phục tài khoản.");
                return;
            }
            studentService.restoreStudent(id);
            printBottomBar();
            success("Khôi phục tài khoản thành công!");
        } catch (IllegalArgumentException e) {
            printBottomBar();
            error(e.getMessage());
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void listStudentsInline() {
        try {
            List<Student> students = studentService.findAll();
            if (students.isEmpty()) {
                printLine(LIGHT_GRAY + "  (Danh sách trống)" + RESET);
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Student s : students) {
                data.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getEmail(),
                        s.getPhone()
                });
            }
            printTable(null,
                    new String[]{"ID", "Họ tên", "Email", "SĐT"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchStudents() {
        printHeader("TÌM KIẾM HỌC VIÊN");
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập từ khóa" + RESET + " (ID, tên, email): ");
        String keyword = scanner.nextLine().trim();

        try {
            List<Student> results = studentService.search(keyword);
            if (results.isEmpty()) {
                printBottomBar();
                warning("Không tìm thấy học viên nào phù hợp.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Student s : results) {
                data.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getEmail(),
                        s.getPhone()
                });
            }
            printTable("KẾT QUẢ TÌM KIẾM (" + results.size() + " học viên)",
                    new String[]{"ID", "Họ tên", "Email", "SĐT"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void sortStudents() {
        printHeader("SẮP XẾP HỌC VIÊN");
        printLine(LIGHT_GRAY + "  Sắp xếp theo:" + RESET);
        printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "ID" + RESET);
        printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Tên" + RESET);
        printEmptyLine();
        printBottomBar();
        printPrompt();
        String fieldChoice = scanner.nextLine().trim();
        String field;
        switch (fieldChoice) {
            case "1" -> field = "id";
            case "2" -> field = "name";
            default -> { error("Lựa chọn không hợp lệ"); return; }
        }

        printHeader("SẮP XẾP HỌC VIÊN");
        printLine(LIGHT_GRAY + "  Thứ tự:" + RESET);
        printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "Tăng dần" + RESET);
        printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Giảm dần" + RESET);
        printEmptyLine();
        printBottomBar();
        printPrompt();
        String orderChoice = scanner.nextLine().trim();
        boolean ascending;
        switch (orderChoice) {
            case "1" -> ascending = true;
            case "2" -> ascending = false;
            default -> { error("Lựa chọn không hợp lệ"); return; }
        }

        try {
            List<Student> sorted = studentService.sort(field, ascending);
            if (sorted.isEmpty()) {
                warning("Danh sách học viên trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Student s : sorted) {
                data.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getEmail(),
                        s.getPhone()
                });
            }
            printTable("KẾT QUẢ SẮP XẾP",
                    new String[]{"ID", "Họ tên", "Email", "SĐT"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void showStudentMenu(Student student) {
        while (true) {
            printMenuTitle("MENU HỌC VIÊN");
            printLine("  " + LIGHT_GRAY + "Xin chào, " + RESET + student.getName() + LIGHT_GRAY + "!" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "1. Xem khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Đăng ký khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "3. Xem khóa học đã đăng ký" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Hủy đăng ký" + RESET);
            printLine("  " + LIGHT_GRAY + "5. Đổi mật khẩu" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "6. Đăng xuất" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showStudentCourseMenu();
                case "2" -> registerCourse(student);
                case "3" -> viewRegisteredCourses(student);
                case "4" -> cancelRegistration(student);
                case "5" -> { if (changePassword(student)) return; }
                case "6" -> {
                    System.out.println();
                    success("Đăng xuất thành công!");
                    return;
                }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showStudentCourseMenu() {
        while (true) {
            printMenuTitle("DANH SÁCH KHÓA HỌC");
            printLine("  " + LIGHT_GRAY + "1. Xem tất cả khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Tìm kiếm khóa học" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "3. Quay lại" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> viewAllCourses();
                case "2" -> searchAllCourses();
                case "3" -> { return; }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void viewAllCourses() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                warning("Danh sách khóa học trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : courses) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("TẤT CẢ KHÓA HỌC",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void searchAllCourses() {
        printHeader("TÌM KIẾM KHÓA HỌC");
        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập từ khóa" + RESET + " (ID, tên, giảng viên): ");
        String keyword = scanner.nextLine().trim();
        try {
            List<Course> results = courseService.search(keyword);
            if (results.isEmpty()) {
                printBottomBar();
                warning("Không tìm thấy khóa học nào.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : results) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("KẾT QUẢ TÌM KIẾM (" + results.size() + " khóa học)",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void registerCourse(Student student) {
        printHeader("ĐĂNG KÝ KHÓA HỌC");
        try {
            List<Course> allCourses = courseService.findAll();
            if (allCourses.isEmpty()) {
                printBottomBar();
                warning("Hiện chưa có khóa học nào.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : allCourses) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("DANH SÁCH KHÓA HỌC HIỆN CÓ",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học muốn đăng ký" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            enrollmentService.registerCourse(student.getId(), courseId);
            printBottomBar();
            success("Đăng ký khóa học thành công! Vui lòng chờ xác nhận.");
        } catch (IllegalArgumentException e) {
            printBottomBar();
            error(e.getMessage());
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void viewRegisteredCourses(Student student) {
        try {
            List<Course> confirmed = enrollmentService.getRegisteredCoursesByStatus(student.getId(), "CONFIRM");
            List<Course> waiting = enrollmentService.getRegisteredCoursesByStatus(student.getId(), "WAITING");
            List<Course> rejected = enrollmentService.getRegisteredCoursesByStatus(student.getId(), "DENIED");
            List<Course> cancelled = enrollmentService.getRegisteredCoursesByStatus(student.getId(), "CANCEL");

            if (!confirmed.isEmpty()) {
                printTable("ĐÃ ĐĂNG KÝ (ĐÃ DUYỆT)",
                        new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                        buildCourseRows(confirmed));
            } else {
                System.out.println("  " + YELLOW + "Bảng ĐÃ ĐĂNG KÝ (ĐÃ DUYỆT)" + RESET + LIGHT_GRAY + " đang trống" + RESET);
            }
            System.out.println();
            System.out.println(GRAY + repeat("-", WIDTH - 2) + RESET);
            System.out.println();

            if (!waiting.isEmpty()) {
                printTable("ĐANG CHỜ DUYỆT",
                        new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                        buildCourseRows(waiting));
            } else {
                System.out.println("  " + YELLOW + "Bảng ĐANG CHỜ DUYỆT" + RESET + LIGHT_GRAY + " đang trống" + RESET);
            }
            System.out.println();
            System.out.println(GRAY + repeat("-", WIDTH - 2) + RESET);
            System.out.println();

            List<Course> rejectedCancelled = new ArrayList<>(rejected);
            rejectedCancelled.addAll(cancelled);
            List<String[]> rcData = new ArrayList<>();
            for (Course c : rejected) rcData.add(new String[]{
                    String.valueOf(c.getId()), c.getName(),
                    c.getDuration() + " buổi", c.getInstructor(),
                    RED + "Bị từ chối" + RESET
            });
            for (Course c : cancelled) rcData.add(new String[]{
                    String.valueOf(c.getId()), c.getName(),
                    c.getDuration() + " buổi", c.getInstructor(),
                    RED + "Đã hủy" + RESET
            });
            if (!rejectedCancelled.isEmpty()) {
                printTable("BỊ TỪ CHỐI / ĐÃ HỦY",
                        new String[]{"ID", "Tên", "Thời lượng", "Giảng viên", "Trạng thái"},
                        rcData);
            } else {
                System.out.println("  " + YELLOW + "Bảng BỊ TỪ CHỐI / ĐÃ HỦY" + RESET + LIGHT_GRAY + " đang trống" + RESET);
            }
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private List<String[]> buildCourseRows(List<Course> courses) {
        List<String[]> rows = new ArrayList<>();
        for (Course c : courses) rows.add(new String[]{
                String.valueOf(c.getId()), c.getName(),
                c.getDuration() + " buổi", c.getInstructor()
        });
        return rows;
    }

    private void cancelRegistration(Student student) {
        printHeader("HỦY ĐĂNG KÝ KHÓA HỌC");
        try {
            List<Course> waiting = enrollmentService.getRegisteredCoursesByStatus(student.getId(), "WAITING");
            if (waiting.isEmpty()) {
                printBottomBar();
                warning("Không có khóa học nào đang chờ duyệt để hủy.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : waiting) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("CÁC KHÓA HỌC ĐANG CHỜ DUYỆT",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        System.out.print("\n" + GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học muốn hủy" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty()) { error("ID không được để trống"); printBottomBar(); return; }
        if ("0".equals(idStr)) { printBottomBar(); return; }

        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                printBottomBar();
                error("Khóa học không tồn tại");
                return;
            }
            Course course = courseOpt.get();
            printLine("  " + LIGHT_GRAY + "Bạn có chắc chắn muốn hủy đăng ký khóa học " + RESET + course.getName() + LIGHT_GRAY + "?" + RESET);
            printEmptyLine();
            printBottomBar();
            System.out.print(GRAY + VERTICAL + RESET + "  " + YELLOW + "Nhập Y để xác nhận, phím bất kỳ để hủy" + RESET + ": ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (!"Y".equals(confirm)) {
                printBottomBar();
                warning("Đã hủy thao tác.");
                return;
            }

            enrollmentService.cancelRegistration(student.getId(), courseId);
            printBottomBar();
            success("Hủy đăng ký khóa học thành công!");
        } catch (IllegalArgumentException e) {
            printBottomBar();
            error(e.getMessage());
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private boolean changePassword(Student student) {
        printHeader("ĐỔI MẬT KHẨU");
        String emailOrPhone;
        while (true) {
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập email hoặc số điện thoại" + RESET + " (nhập 0 để quay lại): ");
            emailOrPhone = scanner.nextLine().trim();
            if ("0".equals(emailOrPhone)) { printBottomBar(); return false; }
            if (emailOrPhone.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Không được để trống"); printBottomBar(); continue; }
            try {
                boolean found = studentService.verifyEmailOrPhone(student.getId(), emailOrPhone);
                if (!found) {
                    System.out.print(GRAY + VERTICAL + RESET + "  ");
                    error("Không tìm thấy tài khoản nào có email hoặc số điện thoại trên, vui lòng thử lại");
                    printBottomBar();
                    continue;
                }
                break;
            } catch (SQLException e) {
                printBottomBar();
                error("Lỗi hệ thống: " + e.getMessage());
                return false;
            }
        }

        String oldPassword;
        while (true) {
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Mật khẩu cũ" + RESET + " (nhập 0 để quay lại): ");
            oldPassword = scanner.nextLine();
            if ("0".equals(oldPassword)) { printBottomBar(); return false; }
            if (oldPassword.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Mật khẩu cũ không được để trống"); printBottomBar(); continue; }
            if (!oldPassword.equals(student.getPassword())) {
                System.out.print(GRAY + VERTICAL + RESET + "  ");
                error("Mật khẩu không chính xác, vui lòng thử lại");
                printBottomBar();
                continue;
            }
            break;
        }

        String newPassword;
        while (true) {
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Mật khẩu mới" + RESET + " (nhập 0 để quay lại): ");
            newPassword = scanner.nextLine();
            if ("0".equals(newPassword)) { printBottomBar(); return false; }
            if (newPassword.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("Mật khẩu mới không được để trống"); printBottomBar(); continue; }
            if (newPassword.equals(student.getPassword())) {
                System.out.print(GRAY + VERTICAL + RESET + "  ");
                error("Mật khẩu bạn vừa nhập đang trùng với mật khẩu cũ, vui lòng nhập mật khẩu không trùng với mật khẩu cũ");
                printBottomBar();
                continue;
            }

            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Xác nhận mật khẩu mới" + RESET + ": ");
            String confirmPassword = scanner.nextLine();
            if (!newPassword.equals(confirmPassword)) {
                System.out.print(GRAY + VERTICAL + RESET + "  ");
                error("Mật khẩu xác nhận không khớp, vui lòng nhập lại");
                printBottomBar();
                continue;
            }
            break;
        }

        try {
            student.setPassword(newPassword);
            studentService.updateStudent(student);
            printBottomBar();
            success("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
            return true;
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
            return false;
        }
    }

    private boolean showEnrollmentManagementMenu() {
        while (true) {
            printMenuTitle("QUẢN LÝ ĐĂNG KÝ KHÓA HỌC");
            printLine("  " + LIGHT_GRAY + "1. Xem sinh viên theo khóa học" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Duyệt khóa học" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "3. Quay lại" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Đăng xuất" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> showStudentsByCourse();
                case "2" -> approveEnrollments();
                case "3" -> { return false; }
                case "4" -> {
                    System.out.println();
                    success("Đăng xuất thành công!");
                    return true;
                }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void showStudentsByCourse() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                warning("Danh sách khóa học trống.");
                return;
            }
            List<String[]> courseData = new ArrayList<>();
            for (Course c : courses) {
                courseData.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("DANH SÁCH KHÓA HỌC",
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    courseData);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học muốn xem danh sách" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty() || "0".equals(idStr)) { printBottomBar(); return; }
        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                printBottomBar();
                error("ID khóa học không tồn tại");
                return;
            }

            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
            List<Enrollment> confirmed = enrollments.stream()
                    .filter(e -> "CONFIRM".equals(e.getStatus()))
                    .toList();
            if (confirmed.isEmpty()) {
                printBottomBar();
                warning("Khóa học '" + courseOpt.get().getName() + "' chưa có học viên đã đăng ký thành công.");
                return;
            }

            List<String[]> studentData = new ArrayList<>();
            for (Enrollment e : confirmed) {
                Optional<Student> studentOpt = studentService.findById(e.getStudentId());
                if (studentOpt.isPresent()) {
                    Student s = studentOpt.get();
                    studentData.add(new String[]{
                            String.valueOf(s.getId()),
                            s.getName(),
                            s.getEmail()
                    });
                }
            }
            printTable("DANH SÁCH HỌC VIÊN - " + courseOpt.get().getName(),
                    new String[]{"ID", "Họ tên", "Email"},
                    studentData);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void approveEnrollments() {
        try {
            List<Student> allStudents = studentService.findAll();
            List<Student> students = allStudents.stream()
                    .filter(s -> s.getRole() != Student.Role.ADMIN)
                    .toList();
            if (students.isEmpty()) {
                warning("Danh sách học viên trống.");
                return;
            }
            List<String[]> studentData = new ArrayList<>();
            for (Student s : students) {
                studentData.add(new String[]{
                        String.valueOf(s.getId()),
                        s.getName(),
                        s.getEmail()
                });
            }
            printTable("DANH SÁCH HỌC VIÊN",
                    new String[]{"ID", "Họ tên", "Email"},
                    studentData);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        int studentId;
        while (true) {
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID học viên muốn duyệt" + RESET + " (nhập 0 để quay lại): ");
            String idStr = scanner.nextLine().trim();
            if ("0".equals(idStr)) { printBottomBar(); return; }
            if (idStr.isEmpty()) { System.out.print(GRAY + VERTICAL + RESET + "  "); error("ID không được để trống"); printBottomBar(); continue; }
            try {
                studentId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                System.out.print(GRAY + VERTICAL + RESET + "  "); error("ID phải là số nguyên, vui lòng thử lại"); printBottomBar();
                continue;
            }
            try {
                if (studentService.findById(studentId).isEmpty()) {
                    System.out.print(GRAY + VERTICAL + RESET + "  "); error("ID học viên không tồn tại, vui lòng thử lại"); printBottomBar();
                    continue;
                }
                break;
            } catch (SQLException e) {
                printBottomBar();
                error("Lỗi hệ thống: " + e.getMessage());
                return;
            }
        }

        List<Course> waitingCourses;
        try {
            waitingCourses = enrollmentService.getRegisteredCoursesByStatus(studentId, "WAITING");
            if (waitingCourses.isEmpty()) {
                Optional<Student> s = studentService.findById(studentId);
                String name = s.map(Student::getName).orElse("");
                System.out.print(GRAY + VERTICAL + RESET + "  ");
                warning("Học viên '" + name + "' không có khóa học nào đang chờ duyệt.");
                printBottomBar();
                return;
            }

            Optional<Student> studentOpt = studentService.findById(studentId);
            List<String[]> courseData = new ArrayList<>();
            for (Course c : waitingCourses) {
                courseData.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName(),
                        c.getDuration() + " buổi",
                        c.getInstructor()
                });
            }
            printTable("KHÓA HỌC CHỜ DUYỆT - " + studentOpt.get().getName(),
                    new String[]{"ID", "Tên", "Thời lượng", "Giảng viên"},
                    courseData);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        int courseId;
        while (true) {
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học cần duyệt" + RESET + " (nhập 0 để quay lại): ");
            String courseIdStr = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(LIGHT_GRAY + "Nhập ID khóa học cần duyệt" + RESET + ": " + courseIdStr);
            if ("0".equals(courseIdStr)) { printEmptyLine(); printBottomBar(); return; }
            if (courseIdStr.isEmpty()) {
                printLine(RED + "[ERROR] ID không được để trống!" + RESET);
                printBottomBar();
                continue;
            }
            try {
                courseId = Integer.parseInt(courseIdStr);
            } catch (NumberFormatException e) {
                printLine(RED + "[ERROR] ID phải là số nguyên!" + RESET);
                printBottomBar();
                continue;
            }
            int finalCourseId = courseId;
            boolean inWaiting = waitingCourses.stream().anyMatch(c -> c.getId() == finalCourseId);
            if (!inWaiting) {
                printLine(RED + "[ERROR] Học viên không đăng ký khóa học với ID trên!" + RESET);
                printBottomBar();
                continue;
            }
            break;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(courseId);

            printLine(CYAN + "  1" + RESET + "  " + LIGHT_GRAY + "Duyệt (confirm)" + RESET);
            printLine(CYAN + "  2" + RESET + "  " + LIGHT_GRAY + "Từ chối (deny)" + RESET);
            printLine(CYAN + "  3" + RESET + "  " + LIGHT_GRAY + "Quay lại" + RESET);
            System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Chọn" + RESET + ": ");
            String action = scanner.nextLine().trim();
            System.out.print("\033[1A\r");
            printLine(LIGHT_GRAY + "Chọn" + RESET + ": " + action);
            switch (action) {
                case "1" -> {
                    enrollmentService.updateEnrollmentStatus(studentId, courseId, "CONFIRM");
                    printLine(GREEN + "Đã duyệt khóa học '" + courseOpt.get().getName() + "'!" + RESET);
                }
                case "2" -> {
                    enrollmentService.updateEnrollmentStatus(studentId, courseId, "DENIED");
                    printLine(RED + "Đã từ chối khóa học '" + courseOpt.get().getName() + "'!" + RESET);
                }
                case "3" -> {}
                default -> {
                    printLine(RED + "[ERROR] Lựa chọn không hợp lệ!" + RESET);
                }
            }
            printEmptyLine();
            printBottomBar();
        } catch (SQLException e) {
            printLine(RED + "[ERROR] Lỗi hệ thống: " + e.getMessage() + RESET);
            printBottomBar();
        }
    }

    private void showStatsMenu() {
        while (true) {
            printMenuTitle("THỐNG KÊ");
            printLine("  " + LIGHT_GRAY + "1. Tổng số khóa học và học viên" + RESET);
            printLine("  " + LIGHT_GRAY + "2. Số học viên theo khóa" + RESET);
            printLine("  " + LIGHT_GRAY + "3. Top 5 khóa học đông sinh viên nhất" + RESET);
            printLine("  " + LIGHT_GRAY + "4. Khóa học có trên 10 sinh viên" + RESET);
            printEmptyLine();
            printLine("  " + LIGHT_GRAY + "5. Quay lại" + RESET);
            printEmptyLine();
            printBottomBar();
            printPrompt();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> statsTotalCoursesAndStudents();
                case "2" -> statsStudentsByCourse();
                case "3" -> statsTop5Courses();
                case "4" -> statsCoursesOver10Students();
                case "5" -> { return; }
                default -> error("Lựa chọn không hợp lệ!");
            }
        }
    }

    private void statsTotalCoursesAndStudents() {
        try {
            Map<String, Long> totals = statsDAO.getTotals();
            printHeader("THỐNG KÊ TỔNG QUAN");
            printLine("  " + LIGHT_GRAY + "Tổng số khóa học:" + RESET + " " + totals.get("total_courses"));
            printLine("  " + LIGHT_GRAY + "Tổng số học viên:" + RESET + " " + totals.get("total_students"));
            printBottomBar();
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void statsStudentsByCourse() {
        try {
            List<Course> courses = courseService.findAll();
            if (courses.isEmpty()) {
                warning("Danh sách khóa học trống.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Course c : courses) {
                data.add(new String[]{
                        String.valueOf(c.getId()),
                        c.getName()
                });
            }
            printTable("DANH SÁCH KHÓA HỌC",
                    new String[]{"ID", "Tên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
            return;
        }

        System.out.print(GRAY + VERTICAL + RESET + "  " + LIGHT_GRAY + "Nhập ID khóa học" + RESET + " (nhập 0 để quay lại): ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isEmpty() || "0".equals(idStr)) { printBottomBar(); return; }
        int courseId;
        try {
            courseId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            printBottomBar();
            error("ID phải là số nguyên");
            return;
        }

        try {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                printBottomBar();
                error("ID khóa học không tồn tại");
                return;
            }
            long count = statsDAO.countStudentsByCourse(courseId);
            printLine("  " + LIGHT_GRAY + "Khóa học '" + RESET + courseOpt.get().getName() + LIGHT_GRAY + "' có " + RESET + count + LIGHT_GRAY + " học viên đã đăng ký." + RESET);
            printBottomBar();
        } catch (SQLException e) {
            printBottomBar();
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void statsTop5Courses() {
        try {
            List<Map<String, Object>> list = statsDAO.getTop5Courses();
            if (list.isEmpty()) {
                warning("Chưa có khóa học nào có học viên đăng ký.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            int rank = 1;
            for (Map<String, Object> row : list) {
                data.add(new String[]{
                        String.valueOf(rank++),
                        String.valueOf(row.get("id")),
                        String.valueOf(row.get("name")),
                        String.valueOf(row.get("student_count"))
                });
            }
            printTable("TOP 5 KHÓA HỌC ĐÔNG SINH VIÊN NHẤT",
                    new String[]{"Hạng", "ID", "Tên", "Số học viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void statsCoursesOver10Students() {
        try {
            List<Map<String, Object>> list = statsDAO.getCoursesOver10Students();
            if (list.isEmpty()) {
                warning("Không có khóa học nào có trên 10 học viên.");
                return;
            }
            List<String[]> data = new ArrayList<>();
            for (Map<String, Object> row : list) {
                data.add(new String[]{
                        String.valueOf(row.get("id")),
                        String.valueOf(row.get("name")),
                        String.valueOf(row.get("student_count"))
                });
            }
            printTable("KHÓA HỌC TRÊN 10 SINH VIÊN",
                    new String[]{"ID", "Tên", "Số học viên"},
                    data);
        } catch (SQLException e) {
            error("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
