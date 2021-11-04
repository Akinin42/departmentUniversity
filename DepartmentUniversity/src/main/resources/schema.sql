DROP TABLE IF EXISTS courses CASCADE;
CREATE TABLE courses(
course_id SERIAL PRIMARY KEY,
course_name VARCHAR(50) NOT NULL,
course_description TEXT NOT NULL
);
DROP TABLE IF EXISTS classrooms CASCADE;
CREATE TABLE classrooms(
classroom_id SERIAL PRIMARY KEY,
classroom_number INT NOT NULL,
classroom_address VARCHAR(100) NOT NULL,
classroom_capacity INT NOT NULL
);
DROP TABLE IF EXISTS user_requests CASCADE;
CREATE TABLE user_requests(
id SERIAL PRIMARY KEY,
sex VARCHAR(10) NOT NULL,
name VARCHAR(100) NOT NULL, 
email VARCHAR(40) NOT NULL, 
phone VARCHAR(20) NOT NULL,
password VARCHAR(100) NOT NULL,
photo TEXT,
role INT REFERENCES roles(role_id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS teachers CASCADE;
CREATE TABLE teachers(
teacher_id SERIAL PRIMARY KEY,
teacher_sex VARCHAR(10) NOT NULL,
teacher_name VARCHAR(100) NOT NULL, 
teacher_email VARCHAR(40) NOT NULL, 
teacher_phone VARCHAR(20) NOT NULL,
teacher_password VARCHAR(100) NOT NULL,
teacher_degree VARCHAR(50) NOT NULL,
teacher_photo TEXT,
teacher_enabled BOOLEAN,
role INT REFERENCES roles(role_id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS students CASCADE;
CREATE TABLE students(
student_id SERIAL PRIMARY KEY,
student_sex VARCHAR(10) NOT NULL,
student_name VARCHAR(100) NOT NULL, 
student_email VARCHAR(40) NOT NULL, 
student_phone VARCHAR(20) NOT NULL,
student_password VARCHAR(100) NOT NULL,
student_photo TEXT,
student_enabled BOOLEAN,
role INT REFERENCES roles(role_id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS students_to_courses CASCADE;
CREATE TABLE students_to_courses(
student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
course_id INT REFERENCES courses(course_id) ON DELETE CASCADE,
PRIMARY KEY(student_id,course_id)
);
DROP TABLE IF EXISTS groups CASCADE;
CREATE TABLE groups(
group_id SERIAL PRIMARY KEY,
group_name VARCHAR(5) NOT NULL
);
DROP TABLE IF EXISTS students_to_groups CASCADE;
CREATE TABLE students_to_groups(
student_id INT REFERENCES students(student_id) ON DELETE CASCADE,
group_id INT REFERENCES groups(group_id) ON DELETE CASCADE,
PRIMARY KEY(student_id,group_id)
);
DROP TABLE IF EXISTS lessons CASCADE;
CREATE TABLE lessons(
lesson_id SERIAL PRIMARY KEY,
lesson_start TIMESTAMP NOT NULL,
lesson_end TIMESTAMP NOT NULL,
lesson_online BOOLEAN NOT NULL,
lesson_link TEXT,
lesson_classroom INT REFERENCES classrooms(classroom_id) ON DELETE CASCADE,
lesson_course INT REFERENCES courses(course_id) ON DELETE CASCADE,
lesson_teacher INT REFERENCES teachers(teacher_id) ON DELETE CASCADE,
lesson_group INT REFERENCES groups(group_id) ON DELETE CASCADE
);
DROP TABLE IF EXISTS roles CASCADE;
CREATE TABLE roles(
role_id SERIAL PRIMARY KEY,
name VARCHAR(50) NOT NULL
);
INSERT INTO roles VALUES(1,'STUDENT');
INSERT INTO roles VALUES(2,'TEACHER');
INSERT INTO roles VALUES(3,'ADMIN');
insert into roles values(4,'USER');