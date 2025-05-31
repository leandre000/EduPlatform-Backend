-- Creating the users table
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')),
                       bio TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the courses table
CREATE TABLE courses (
                         id UUID PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         price DECIMAL(10, 2),
                         prerequisites TEXT,
                         instructor_id UUID REFERENCES users(id) ON DELETE SET NULL,
                         enrollment_count INTEGER DEFAULT 0,
                         average_rating DECIMAL(3, 1) DEFAULT 0.0,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the sections table
CREATE TABLE sections (
                          id UUID PRIMARY KEY,
                          course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                          title VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the lectures table
CREATE TABLE lectures (
                          id UUID PRIMARY KEY,
                          section_id UUID REFERENCES sections(id) ON DELETE CASCADE,
                          title VARCHAR(255) NOT NULL,
                          type VARCHAR(20) NOT NULL CHECK (type IN ('VIDEO', 'TEXT')),
                          content_url VARCHAR(255),
                          content_text TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the enrollments table
CREATE TABLE enrollments (
                             id UUID PRIMARY KEY,
                             student_id UUID REFERENCES users(id) ON DELETE CASCADE,
                             course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                             progress_percentage DECIMAL(5, 2) DEFAULT 0.0,
                             completed BOOLEAN DEFAULT FALSE,
                             last_lecture_viewed_id UUID REFERENCES lectures(id),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE(student_id, course_id)
);

-- Creating the reviews table
CREATE TABLE reviews (
                         id UUID PRIMARY KEY,
                         course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                         student_id UUID REFERENCES users(id) ON DELETE CASCADE,
                         rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the assignments table
CREATE TABLE assignments (
                             id UUID PRIMARY KEY,
                             course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                             title VARCHAR(255) NOT NULL,
                             description TEXT,
                             due_date TIMESTAMP,
                             max_marks INTEGER NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the submissions table
CREATE TABLE submissions (
                             id UUID PRIMARY KEY,
                             assignment_id UUID REFERENCES assignments(id) ON DELETE CASCADE,
                             student_id UUID REFERENCES users(id) ON DELETE CASCADE,
                             submission_text TEXT,
                             file_url VARCHAR(255),
                             status VARCHAR(20) NOT NULL CHECK (status IN ('SUBMITTED', 'GRADED')),
                             marks_obtained INTEGER,
                             feedback TEXT,
                             submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the marking_guides table
CREATE TABLE marking_guides (
                                id UUID PRIMARY KEY,
                                course_id UUID REFERENCES courses(id) ON DELETE CASCADE,
                                title VARCHAR(255) NOT NULL,
                                file_url VARCHAR(255) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the complaints table
CREATE TABLE complaints (
                            id UUID PRIMARY KEY,
                            student_id UUID REFERENCES users(id) ON DELETE CASCADE,
                            course_id UUID REFERENCES courses(id) ON DELETE SET NULL,
                            subject VARCHAR(255) NOT NULL,
                            description TEXT NOT NULL,
                            status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'RESOLVED')),
                            response_text TEXT,
                            submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creating the password_reset_tokens table
CREATE TABLE password_reset_tokens (
                                       id UUID PRIMARY KEY,
                                       user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                                       token VARCHAR(255) NOT NULL,
                                       expiry_date TIMESTAMP NOT NULL,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
CREATE INDEX idx_enrollments_student_course ON enrollments(student_id, course_id);
CREATE INDEX idx_reviews_course_id ON reviews(course_id);
CREATE INDEX idx_complaints_course_id ON complaints(course_id);

-- Trigger function to update course enrollment count
CREATE OR REPLACE FUNCTION update_enrollment_count()
RETURNS TRIGGER AS $$
BEGIN
UPDATE courses
SET enrollment_count = (
    SELECT COUNT(*)
    FROM enrollments
    WHERE course_id = NEW.course_id
)
WHERE id = NEW.course_id;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for after enrollment insert
CREATE TRIGGER after_enrollment_insert
    AFTER INSERT ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION update_enrollment_count();

-- Trigger function to update course progress percentage
CREATE OR REPLACE FUNCTION update_course_progress()
RETURNS TRIGGER AS $$
DECLARE
total_lectures INTEGER;
    completed_lectures INTEGER;
BEGIN
    -- Count total lectures in the course
SELECT COUNT(*) INTO total_lectures
FROM lectures l
         JOIN sections s ON l.section_id = s.id
WHERE s.course_id = (
    SELECT course_id
    FROM enrollments
    WHERE id = NEW.enrollment_id
);

-- Count completed lectures for the student
SELECT COUNT(*) INTO completed_lectures
FROM enrollment_lectures el
WHERE el.enrollment_id = NEW.enrollment_id;

-- Update progress percentage
UPDATE enrollments
SET progress_percentage = (completed_lectures::FLOAT / total_lectures) * 100,
    completed = (completed_lectures = total_lectures)
WHERE id = NEW.enrollment_id;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Junction table for tracking completed lectures
CREATE TABLE enrollment_lectures (
                                     enrollment_id UUID REFERENCES enrollments(id) ON DELETE CASCADE,
                                     lecture_id UUID REFERENCES lectures(id) ON DELETE CASCADE,
                                     completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (enrollment_id, lecture_id)
);

-- Trigger for after lecture completion
CREATE TRIGGER after_lecture_completion_update
    AFTER INSERT ON enrollment_lectures
    FOR EACH ROW
    EXECUTE FUNCTION update_course_progress();