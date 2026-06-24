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
    created_at date         default now(),
    deleted    boolean      default false
);

ALTER TABLE students ADD COLUMN deleted boolean DEFAULT false;

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

-- ========== THỐNG KÊ ==========

-- 1. Tổng số khóa học và tổng số học viên
create or replace function fn_get_totals()
returns table(total_courses bigint, total_students bigint)
language plpgsql
as $$
begin
    return query
    select
        (select count(*) from courses)::bigint,
        (select count(*) from students where role = 'STUDENT' and deleted = false)::bigint;
end;
$$;

-- 2. Số học viên theo khóa
create or replace function fn_count_students_by_course(p_course_id int)
returns bigint
language plpgsql
as $$
declare
    v_count bigint;
begin
    select count(*) into v_count
    from enrollments
    where course_id = p_course_id and status = 'CONFIRM';
    return v_count;
end;
$$;

-- 3. Top 5 khóa học có đông sinh viên nhất
create or replace function fn_top_5_courses()
returns table(id int, name varchar, duration int, instructor varchar, student_count bigint)
language plpgsql
as $$
begin
    return query
    select c.id, c.name, c.duration, c.instructor, count(e.id)::bigint as student_count
    from courses c
    left join enrollments e on e.course_id = c.id and e.status = 'CONFIRM'
    group by c.id, c.name, c.duration, c.instructor
    order by student_count desc
    limit 5;
end;
$$;

-- 4. Khóa học có trên 10 sinh viên
create or replace function fn_courses_over_10_students()
returns table(id int, name varchar, duration int, instructor varchar, student_count bigint)
language plpgsql
as $$
begin
    return query
    select c.id, c.name, c.duration, c.instructor, count(e.id)::bigint as student_count
    from courses c
    left join enrollments e on e.course_id = c.id and e.status = 'CONFIRM'
    group by c.id, c.name, c.duration, c.instructor
    having count(e.id) > 10
    order by student_count desc;
end;
$$;