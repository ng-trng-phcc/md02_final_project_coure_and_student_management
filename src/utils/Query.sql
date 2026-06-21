create database course_and_student_db;

-- Tạo enum cho student
create type student_role as ENUM (
    'ADMIN',
    'STUDENT'
);

-- Tạo enum cho enrollment
create type enr_status as ENUM (
    'WAITING',
    'DENIED',
    'CANCEL',
    'CONFIRM'
);

create table students
(
    id         serial primary key,
    name       varchar(100) not null,
    dob        date         not null,
    email      varchar(100) not null unique,
    sex        boolean      not null, --Nam = true, Nữ = false
    phone      varchar(20),
    role       student_role default 'STUDENT',
    password   varchar      not null,
    created_at date         default now()
);

create table courses
(
    id         serial primary key,
    name       varchar(100) not null,
    duration   int          not null,
    instructor varchar(100) not null,
    created_at date default now()
);

create table enrollments
(
    id            serial primary key,
    student_id    int references students (id) not null,
    course_id     int references courses (id)  not null,
    registered_at timestamp  default current_timestamp,
    status        enr_status default 'WAITING'
);

insert into students (name, dob, email, sex, phone, role, password)
values ('admin', '2005-06-04', 'phuoc@admin.com', true, '0123456789', 'ADMIN', '123');

select *
from students;

select *
from courses;

select *
from enrollments;