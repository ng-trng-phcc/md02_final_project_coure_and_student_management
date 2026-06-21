package model;

import java.time.LocalDate;

public class Course {
    private Integer id;
    private String name;
    private Integer duration;
    private String instructor;
    private LocalDate createdAt;

    public Course() {}

    public Course(Integer id, String name, Integer duration, String instructor, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.instructor = instructor;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("%-5s | %-30s | %-10s | %-20s | %s",
                id != null ? id : "",
                name != null ? name : "",
                duration != null ? duration + " buổi" : "",
                instructor != null ? instructor : "",
                createdAt != null ? createdAt : "");
    }
}
