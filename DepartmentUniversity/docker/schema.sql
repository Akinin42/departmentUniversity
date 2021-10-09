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
DROP TABLE IF EXISTS teachers CASCADE;
CREATE TABLE teachers(
teacher_id SERIAL PRIMARY KEY,
teacher_sex VARCHAR(10) NOT NULL,
teacher_name VARCHAR(100) NOT NULL, 
teacher_email VARCHAR(40) NOT NULL, 
teacher_phone VARCHAR(20) NOT NULL,
teacher_password VARCHAR(100) NOT NULL,
teacher_degree VARCHAR(50) NOT NULL 
);
DROP TABLE IF EXISTS students CASCADE;
CREATE TABLE students(
student_id SERIAL PRIMARY KEY,
student_sex VARCHAR(10) NOT NULL,
student_name VARCHAR(100) NOT NULL, 
student_email VARCHAR(40) NOT NULL, 
student_phone VARCHAR(20) NOT NULL,
student_password VARCHAR(100) NOT NULL
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
INSERT INTO groups VALUES(1, 'AB-22');
INSERT INTO groups VALUES(2, 'FR-33');
INSERT INTO courses VALUES(1, 'Law', 'test-courses');
INSERT INTO courses VALUES(2, 'Math','test-courses');
INSERT INTO courses VALUES(3, 'Art','test-courses');
INSERT INTO classrooms VALUES(1, 1, 'Test-address', 10);
INSERT INTO classrooms VALUES(2, 2, 'Test-address', 15);
INSERT INTO teachers VALUES(1, 'Sex.MALE', 'Bob Moren', 'Bob@mail.ru', '89758657788', 'test-password', 'professor');
INSERT INTO teachers VALUES(2, 'Sex.FEMALE', 'Ann Moren', 'Ann@mail.ru', '89758651122', 'test-password','doctor');
INSERT INTO students VALUES(1, 'Sex.FEMALE', 'Jane Wood', 'Wood@email.ru', 'test-phone', 'test-password');
INSERT INTO students VALUES(2, 'Sex.FEMALE', 'Ann Lee', 'Lee@email.ru', 'test-phone', 'test-password');
INSERT INTO students VALUES(3, 'Sex.FEMALE', 'Mary Born', 'Born@email.ru', 'test-phone', 'test-password');
INSERT INTO students VALUES(4, 'Sex.MALE', 'Rob Melon', 'Melon@email.ru', 'test-phone','test-password');
INSERT INTO students VALUES(5, 'Sex.MALE', 'John Brown', 'Brown@email.ru', 'test-phone', 'test-password');
INSERT INTO students VALUES(6, 'Sex.MALE', 'Pol Hardy', 'Hardy@email.ru', 'test-phone', 'test-password');
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,2);
INSERT INTO students_to_courses (student_id, course_id) VALUES(1,3);
INSERT INTO students_to_courses (student_id, course_id) VALUES(2,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(3,1);
INSERT INTO students_to_courses (student_id, course_id) VALUES(4,1);
INSERT INTO students_to_groups (student_id, group_id) VALUES(1,1);
INSERT INTO students_to_groups (student_id, group_id) VALUES(2,1);
INSERT INTO students_to_groups (student_id, group_id) VALUES(3,1);
INSERT INTO students_to_groups (student_id, group_id) VALUES(4,1);
INSERT INTO students_to_groups (student_id, group_id) VALUES(5,2);
INSERT INTO students_to_groups (student_id, group_id) VALUES(6,2);
INSERT INTO lessons VALUES(1, '2021-10-19 10:00:00', '2021-10-19 12:00:00', false, null, 1,1,1,1); 
INSERT INTO lessons VALUES(2, '2021-10-19 15:00:00', '2021-10-19 17:00:00', true, 'test-link', 2,2,2,2); 
INSERT INTO lessons VALUES(3, '2021-10-19 21:00:00', '2021-10-19 22:00:00', true, 'test-link', 2,2,2,2);