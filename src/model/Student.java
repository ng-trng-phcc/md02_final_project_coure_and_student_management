package model;

import java.time.LocalDate;

public class Student {
    private Integer id;
    private String name;
    private LocalDate dob;
    private String email;
    private Boolean sex;
    private String phone;
    private Role role;
    private String password;
    private LocalDate createdAt;

    // Tạo enum cho role
    public enum Role {
        ADMIN, STUDENT
    }

    // Constructor không param
    public Student() {}

    // Constructor có param
    public Student(Integer id, String name, LocalDate dob, String email, Boolean sex, String phone, Role role, String password, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.sex = sex;
        this.phone = phone;
        this.role = role;
        this.password = password;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSex() {
        return sex;
    }
    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    // Ở phần hiển thị, nếu field nào null thì hiện ""
    @Override
    public String toString() {
        return String.format("%-5s | %-20s | %-12s | %-30s | %-5s | %-15s | %-10s | %s",
                id != null ? id : "",
                name != null ? name : "",
                dob != null ? dob : "",
                email != null ? email : "",
                sex != null ? (sex ? "Nam" : "Nữ") : "",
                phone != null ? phone : "",
                role != null ? role : "",
                createdAt != null ? createdAt : "");
    }
}
