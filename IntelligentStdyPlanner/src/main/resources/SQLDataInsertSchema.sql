INSERT INTO students (name, english_fluency, tuition_hours_weekly, commute_fatigue, stream, district)
VALUES
    ('Kamal', 4.2, 6, 2.5, 'Physical Science', 'Colombo'),
    ('Niroshan', 5.0, 8, 3.0, 'Technology', 'Gampaha'),
    ('Anjana', 3.5, 4, 1.5, 'Commerce', 'Kandy'),
    ('Sithara', 4.7, 7, 2.0, 'Bio Science', 'Galle'),
    ('Dilshan', 2.8, 3, 4.0, 'Arts', 'Matara');


INSERT INTO subjects (name, difficalty_level, current_score, student_id)
VALUES
    ('Mathematics', 4, 78.5, 1),
    ('Physics', 5, 65.0, 1),
    ('ICT', 3, 82.0, 2),
    ('Accounting', 4, 70.0, 3),
    ('Biology', 5, 60.0, 4);


INSERT INTO study_sessions (start_time, end_time, title, subject_id)
VALUES
    ('2025-01-15 09:00:00', '2025-01-15 11:00:00', 'Algebra Basics', 1),
    ('2025-01-16 10:00:00', '2025-01-16 12:00:00', 'Nuclear Physics', 2),
    ('2025-01-17 08:30:00', '2025-01-17 10:00:00', 'Web App Development', 3),
    ('2025-01-18 15:00:00', '2025-01-18 17:00:00', 'Financial Statements', 4),
    ('2025-01-19 14:00:00', '2025-01-19 16:00:00', 'Cell Structure', 5);

INSERT INTO performance_logs (score, hourse_studies, sate_recorded, subject_id)
VALUES
    (75.5, 2, '2025-01-10', 1),
    (68.0, 3, '2025-01-12', 2),
    (88.0, 1, '2025-01-13', 3),
    (72.5, 4, '2025-01-14', 4),
    (65.0, 2, '2025-01-15', 5);

INSERT INTO exams (name, deadline, subject_id)
VALUES
    ('Math Midterm', '2025-02-20 09:00:00', 1),
    ('Physics Practical', '2025-02-25 10:00:00', 2),
    ('ICT Final', '2025-03-01 14:00:00', 3),
    ('Accounting Quiz', '2025-02-22 11:00:00', 4),
    ('Bio Theory Exam', '2025-02-28 09:30:00', 5);


INSERT INTO availability (day_of_week, start_time, end_time, student_id)
VALUES
    ('MONDAY', '16:00:00', '18:00:00', 1),
    ('TUESDAY', '14:00:00', '17:00:00', 2),
    ('WEDNESDAY', '09:00:00', '12:00:00', 3),
    ('THURSDAY', '15:00:00', '17:00:00', 4),
    ('FRIDAY', '08:00:00', '11:00:00', 5);