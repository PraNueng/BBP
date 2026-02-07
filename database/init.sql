-- ============================================
-- BBP TUTOR DATABASE SCHEMA V3
-- ============================================

CREATE DATABASE IF NOT EXISTS BBP_Tutor_v3 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE BBP_Tutor_v3;
-- DROP DATABASE BBP_Tutor_v3;

-- ============================================
-- CORE TABLES
-- ============================================

-- Users/Tutors Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL COMMENT 'admin, tutor',
    nickname VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Grades Table (ระดับชั้น)
CREATE TABLE grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    grade_name VARCHAR(50) UNIQUE NOT NULL,
    grade_order INT NOT NULL UNIQUE,
    next_grade_id BIGINT NULL,
    INDEX idx_order (grade_order),
    FOREIGN KEY (next_grade_id) REFERENCES grades(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Subjects Table (วิชา)
CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Students Table
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_code VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    nickname VARCHAR(100),
    school_name VARCHAR(255),
    grade_id BIGINT NOT NULL,
    phone_student VARCHAR(50),
    phone_parent VARCHAR(50),
    study_plan VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_name (first_name, last_name),
    INDEX idx_grade_active (grade_id, is_active),
    INDEX idx_active (is_active),
    INDEX idx_school (school_name(50)),
    INDEX idx_student_code (student_code),
    INDEX idx_student_active (is_active, first_name, last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CLASS SUBTYPE TABLES
-- ============================================

-- Hourly Group Subtypes (A, B, C, PV, etc.)
CREATE TABLE hourly_group_subtypes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subtype_name VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Monthly Subtypes (จันทร์-ศุกร์, etc.)
CREATE TABLE monthly_subtypes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subtype_name VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_active (is_active),
    INDEX idx_display_order (display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CLASS TABLES (3 ประเภท)
-- ============================================

-- 1. Hourly Group Classes
CREATE TABLE hourly_group_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    grade_id BIGINT NULL,
    subtype_id BIGINT NOT NULL,
    class_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (subtype_id) REFERENCES hourly_group_subtypes(id),
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_subject (subject_id),
    INDEX idx_subtype (subtype_id),
    INDEX idx_active (is_active),
    INDEX idx_grade (grade_id),
    INDEX idx_subject_grade (subject_id, grade_id),
    INDEX idx_class_active_subject (is_active, subject_id, grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hourly Group Class Tutors (Many-to-Many)
CREATE TABLE hourly_group_class_tutors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    FOREIGN KEY (class_id) REFERENCES hourly_group_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_tutor (class_id, tutor_id),
    INDEX idx_class (class_id),
    INDEX idx_tutor (tutor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Hourly Individual Classes
CREATE TABLE hourly_individual_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    grade_id BIGINT NULL,
    student_id BIGINT NULL,
    class_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_subject (subject_id),
    INDEX idx_student (student_id),
    INDEX idx_active (is_active),
    INDEX idx_grade (grade_id),
    INDEX idx_student_subject (student_id, subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE hourly_individual_class_students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    status_change_reason TEXT NULL COMMENT 'เหตุผลการ active/inactive',
    status_changed_by BIGINT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
    status_changed_at TIMESTAMP NULL COMMENT 'เวลาที่เปลี่ยน status',
    FOREIGN KEY (class_id) REFERENCES hourly_individual_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_hics_status_changed_by 
    FOREIGN KEY (status_changed_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_student (class_id, student_id),
    INDEX idx_class (class_id),
    INDEX idx_student (student_id),
    INDEX idx_active (is_active),
    INDEX idx_status_changed_at (status_changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hourly Individual Class Tutors (Many-to-Many)
CREATE TABLE hourly_individual_class_tutors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    FOREIGN KEY (class_id) REFERENCES hourly_individual_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_tutor (class_id, tutor_id),
    INDEX idx_class (class_id),
    INDEX idx_tutor (tutor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Monthly Classes
CREATE TABLE monthly_classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    grade_id BIGINT NULL,
    subtype_id BIGINT NOT NULL,
    class_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (subtype_id) REFERENCES monthly_subtypes(id),
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_subject (subject_id),
    INDEX idx_subtype (subtype_id),
    INDEX idx_active (is_active),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_grade (grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Monthly Class Tutors (Many-to-Many)
CREATE TABLE monthly_class_tutors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    tutor_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    FOREIGN KEY (class_id) REFERENCES monthly_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_tutor (class_id, tutor_id),
    INDEX idx_class (class_id),
    INDEX idx_tutor (tutor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- STUDENT-CLASS ENROLLMENT TABLES
-- ============================================

-- Hourly Group Enrollments
CREATE TABLE hourly_group_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enrolled_grade_id BIGINT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    status_change_reason TEXT NULL COMMENT 'เหตุผลการ active/inactive',
    status_changed_by BIGINT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
    status_changed_at TIMESTAMP NULL COMMENT 'เวลาที่เปลี่ยน status',
    FOREIGN KEY (class_id) REFERENCES hourly_group_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (enrolled_grade_id) REFERENCES grades(id),
    CONSTRAINT fk_hge_status_changed_by 
    FOREIGN KEY (status_changed_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_student (class_id, student_id),
    INDEX idx_class (class_id),
    INDEX idx_student (student_id),
    INDEX idx_active (is_active),
    INDEX idx_class_active (class_id, is_active),
    INDEX idx_status_changed_at (status_changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Monthly Enrollments
CREATE TABLE monthly_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enrolled_grade_id BIGINT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    status_change_reason TEXT NULL COMMENT 'เหตุผลการ active/inactive',
    status_changed_by BIGINT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
    status_changed_at TIMESTAMP NULL COMMENT 'เวลาที่เปลี่ยน status',
    FOREIGN KEY (class_id) REFERENCES monthly_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (enrolled_grade_id) REFERENCES grades(id),
    CONSTRAINT fk_me_status_changed_by 
    FOREIGN KEY (status_changed_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY uk_class_student (class_id, student_id),
    INDEX idx_class (class_id),
    INDEX idx_student (student_id),
    INDEX idx_active (is_active),
    INDEX idx_status_changed_at (status_changed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- RECEIPT TABLES
-- ============================================

CREATE TABLE receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NULL COMMENT 'นักเรียนที่จ่ายเงิน (optional)',
    class_type VARCHAR(50) NULL COMMENT 'hourly_group, hourly_individual, monthly',
    class_id BIGINT NULL COMMENT 'ID ของคลาส (optional)',
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE NOT NULL,
    image_path VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT 'pending, paid, cancelled',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_student (student_id),
    INDEX idx_class (class_type, class_id),
    INDEX idx_status (status),
    INDEX idx_payment_date (payment_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- STUDENT COURSE PURCHASES (ข้อมูลการซื้อคอร์ส)
-- ============================================

CREATE TABLE student_course_purchases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    class_type VARCHAR(50) NULL COMMENT 'hourly_group, hourly_individual, hourly_individual_group, monthly, NULL=ซื้อแบบไม่ระบุคลาส',
    class_id BIGINT NULL,
    monthly_subtype_id BIGINT NULL COMMENT 'สำหรับคลาสรายเดือน (nullable)',
    hours_purchased DECIMAL(7,2) NULL COMMENT 'จำนวนชั่วโมงที่ซื้อ (NULL สำหรับคลาสรายเดือน)',
    hours_completed DECIMAL(7,2) DEFAULT 0 COMMENT 'จำนวนชั่วโมงที่เรียนไปแล้ว',
    purchase_date DATE NOT NULL,
    receipt_id BIGINT NULL,
    notes TEXT,
    deleted_reason TEXT NULL COMMENT 'เหตุผลในการลบคอร์ส (soft delete)',
    edit_reason TEXT NULL COMMENT 'เหตุผลในการแก้ไขคอร์ส',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (receipt_id) REFERENCES receipts(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_student (student_id),
    INDEX idx_subject (subject_id),
    INDEX idx_active (is_active),
    INDEX idx_purchase_date (purchase_date),
    INDEX idx_class_lookup (class_type, class_id),
    INDEX idx_student_subject_active (student_id, subject_id, is_active),
    INDEX idx_student_class (student_id, class_type, class_id, is_active),
    INDEX idx_monthly_subtype (monthly_subtype_id),
    CONSTRAINT chk_hours_valid CHECK ((hours_purchased IS NULL OR hours_purchased > 0) AND (hours_completed >= 0)),
    CONSTRAINT chk_hours_not_exceed CHECK (hours_completed <= hours_purchased * 100),
    CONSTRAINT fk_course_monthly_subtype FOREIGN KEY (monthly_subtype_id) REFERENCES monthly_subtypes(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- HOUR FORMS (เฉพาะคลาสรายชั่วโมง)
-- ============================================

CREATE TABLE hour_forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tutor_id BIGINT NOT NULL,
    class_type VARCHAR(50) NOT NULL COMMENT 'hourly_group, hourly_individual',
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    teaching_date DATE NOT NULL,
    hours_taught DECIMAL(7,2) NOT NULL,
    students_present INT NOT NULL DEFAULT 0,
    students_absent INT NOT NULL DEFAULT 0,
    remark TEXT,
    is_admin_created BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_tutor (tutor_id),
    INDEX idx_tutor_date (tutor_id, teaching_date),
    INDEX idx_class (class_type, class_id),
    INDEX idx_teaching_date (teaching_date),
    INDEX idx_subject (subject_id),
    INDEX idx_class_date (class_type, class_id, teaching_date DESC),
    CONSTRAINT chk_students_valid CHECK (students_present >= 0 AND students_absent >= 0),
    CONSTRAINT chk_hours_taught_positive CHECK (hours_taught > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Math Forms (สำหรับคลาสรายเดือนเท่านั้น)
CREATE TABLE math_forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tutor_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL COMMENT 'ต้องเป็น monthly_classes เท่านั้น',
    subject_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    teaching_date DATE NOT NULL,
    hours_taught DECIMAL(7,2) NOT NULL,
    students_present INT NOT NULL DEFAULT 0,
    students_absent INT NOT NULL DEFAULT 0,
    remark TEXT,
    is_admin_created BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES monthly_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_tutor (tutor_id),
    INDEX idx_class (class_id),
    INDEX idx_teaching_date (teaching_date),
    INDEX idx_tutor_date (tutor_id, teaching_date),
    CONSTRAINT chk_math_hours_taught_positive CHECK (hours_taught > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Science Forms (สำหรับคลาสรายเดือนเท่านั้น)
CREATE TABLE science_forms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tutor_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL COMMENT 'ต้องเป็น monthly_classes เท่านั้น',
    subject_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    teaching_date DATE NOT NULL,
    hours_taught DECIMAL(7,2) NOT NULL,
    students_present INT NOT NULL DEFAULT 0,
    students_absent INT NOT NULL DEFAULT 0,
    remark TEXT,
    is_admin_created BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES monthly_classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_tutor (tutor_id),
    INDEX idx_class (class_id),
    INDEX idx_teaching_date (teaching_date),
    INDEX idx_tutor_date (tutor_id, teaching_date),
    CONSTRAINT chk_science_hours_taught_positive CHECK (hours_taught > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- NOTIFICATION SYSTEM
-- ============================================

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_type VARCHAR(50) NOT NULL COMMENT 'HOURS_MILESTONE, GRADE_MISMATCH, SYSTEM',
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    class_type VARCHAR(50) NULL,
    class_id BIGINT NULL,
    student_id BIGINT NULL,
    subject_id BIGINT NULL,
    hours_completed DECIMAL(7,2) NULL,
    hours_target DECIMAL(7,2) NULL,
    milestone_reached INT NULL COMMENT 'ครั้งที่ครบ (1, 2, 3...)',
    receipt_code VARCHAR(100) NULL COMMENT 'รหัสใบเสร็จที่ admin กรอกตอนอ่าน notification',
    is_read BOOLEAN DEFAULT FALSE,
    read_by BIGINT NULL,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL,
    FOREIGN KEY (read_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_unread (is_read, created_at),
    INDEX idx_class (class_type, class_id),
    INDEX idx_student (student_id),
    INDEX idx_type (notification_type),
    INDEX idx_created_at (created_at DESC),
    INDEX idx_receipt_code (receipt_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- HISTORY TABLES (Track ทุกอย่าง)
-- ============================================

-- Student History
CREATE TABLE student_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL COMMENT 'ชื่อ field ที่แก้ไข (NULL ถ้าเป็น INSERT/DELETE)',
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_student (student_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Hour Form History
CREATE TABLE hour_form_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hour_form_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hour_form_id) REFERENCES hour_forms(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_hour_form (hour_form_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Math Form History
CREATE TABLE math_form_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    math_form_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (math_form_id) REFERENCES math_forms(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_math_form (math_form_id),
    INDEX idx_updated_at (updated_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Science Form History
CREATE TABLE science_form_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    science_form_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (science_form_id) REFERENCES science_forms(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_science_form (science_form_id),
    INDEX idx_updated_at (updated_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Class History (ทั้ง 3 ประเภท)
CREATE TABLE class_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_type VARCHAR(50) NOT NULL COMMENT 'hourly_group, hourly_individual, monthly',
    class_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_class (class_type, class_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Enrollment History
CREATE TABLE enrollment_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enrollment_type VARCHAR(50) NOT NULL COMMENT 'hourly_group, monthly',
    enrollment_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_change_reason TEXT NULL COMMENT 'เหตุผลที่บันทึกไว้ตอนเปลี่ยน status',
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_enrollment (enrollment_type, enrollment_id),
    INDEX idx_class (class_id),
    INDEX idx_student (student_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Receipt History
CREATE TABLE receipt_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (receipt_id) REFERENCES receipts(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_receipt (receipt_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Course Purchase History
CREATE TABLE course_purchase_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (purchase_id) REFERENCES student_course_purchases(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_purchase (purchase_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notification History
CREATE TABLE notification_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL COMMENT 'ชื่อ field ที่แก้ไข (NULL ถ้าเป็น INSERT/DELETE)',
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL COMMENT 'user ที่ทำการแก้ไข',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notifications(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_notification (notification_id),
    INDEX idx_updated_at (updated_at DESC),
    INDEX idx_action (action),
    INDEX idx_field_name (field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Grade Progression History
CREATE TABLE grade_progression_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    old_grade_id BIGINT NOT NULL,
    new_grade_id BIGINT NOT NULL,
    progression_date DATE NOT NULL,
    progression_type VARCHAR(20) DEFAULT 'manual' COMMENT 'auto, manual',
    academic_year VARCHAR(10),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (old_grade_id) REFERENCES grades(id),
    FOREIGN KEY (new_grade_id) REFERENCES grades(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_student (student_id),
    INDEX idx_progression_date (progression_date),
    INDEX idx_type (progression_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Activity Log สำหรับทุกการกระทำใน System
CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL COMMENT 'LOGIN, LOGOUT, CREATE, UPDATE, DELETE, ASSIGN, etc.',
    entity_type VARCHAR(50) NOT NULL COMMENT 'student, class, enrollment, receipt, etc.',
    entity_id BIGINT NULL,
    description TEXT NOT NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    session_id VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_at TIMESTAMP NOT NULL,
    logout_at TIMESTAMP NULL,
    session_duration INT NULL COMMENT 'ระยะเวลา (วินาที)',
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    logout_type VARCHAR(20) NULL COMMENT 'manual, auto, timeout',
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_login_at (login_at DESC),
    INDEX idx_logout_at (logout_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    field_name VARCHAR(255) NULL,
    old_value TEXT,
    new_value TEXT,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_updated_at (updated_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_hour_form_overrides (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hour_form_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    hours_override DECIMAL(5,2),
    remark_override VARCHAR(500) NULL,
    FOREIGN KEY (hour_form_id) REFERENCES hour_forms(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE KEY unique_override (hour_form_id, student_id)
);

CREATE TABLE student_hour_form_override_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    override_id BIGINT NULL,
    hour_form_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    old_value TEXT NULL,
    new_value TEXT NULL,
    updated_by BIGINT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    action VARCHAR(50) DEFAULT 'UPDATE',
    FOREIGN KEY (override_id) REFERENCES student_hour_form_overrides(id) ON DELETE SET NULL,
    FOREIGN KEY (hour_form_id) REFERENCES hour_forms(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_override_id (override_id),
    INDEX idx_hour_form_id (hour_form_id),
    INDEX idx_student_id (student_id),
    INDEX idx_hour_form_student (hour_form_id, student_id),
    INDEX idx_updated_at (updated_at),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- USEFUL VIEWS
-- ============================================

-- View: Student Course Summary
CREATE VIEW v_student_course_summary AS
SELECT 
    scp.id,
    scp.student_id,
    s.student_code,
    s.first_name,
    s.last_name,
    s.nickname,
    g.grade_name,
    sub.subject_name,
    scp.class_type,
    scp.class_id,
    scp.monthly_subtype_id,
    ms.subtype_name as monthly_subtype_name,
    scp.hours_purchased,
    scp.hours_completed,
    CASE 
        WHEN scp.hours_purchased IS NOT NULL THEN (scp.hours_purchased - scp.hours_completed)
        ELSE NULL
    END as hours_remaining,
    CASE 
        WHEN scp.hours_purchased IS NOT NULL AND scp.hours_purchased > 0 
        THEN ROUND((scp.hours_completed / scp.hours_purchased) * 100, 2)
        ELSE NULL
    END as completion_percentage,
    CASE 
        WHEN scp.hours_purchased IS NOT NULL AND scp.hours_purchased > 0 
        THEN FLOOR(scp.hours_completed / scp.hours_purchased)
        ELSE NULL
    END as milestones_completed,
    scp.purchase_date,
    scp.notes,
    scp.deleted_reason,
    scp.is_active,
    scp.created_at
FROM student_course_purchases scp
JOIN students s ON scp.student_id = s.id
JOIN subjects sub ON scp.subject_id = sub.id
LEFT JOIN grades g ON s.grade_id = g.id
LEFT JOIN monthly_subtypes ms ON scp.monthly_subtype_id = ms.id
WHERE scp.is_active = TRUE;

-- View: Class Summary (Hourly Group)
CREATE VIEW v_hourly_group_class_summary AS
SELECT 
    c.id as class_id,
    c.class_name,
    sub.subject_name,
    g.grade_name,
    hgs.subtype_name,
    COUNT(DISTINCT e.student_id) as total_students,
    COUNT(DISTINCT hf.id) as total_sessions,
    COALESCE(SUM(hf.hours_taught), 0) as total_hours_taught,
    c.is_active,
    c.created_at
FROM hourly_group_classes c
JOIN subjects sub ON c.subject_id = sub.id
LEFT JOIN grades g ON c.grade_id = g.id
JOIN hourly_group_subtypes hgs ON c.subtype_id = hgs.id
LEFT JOIN hourly_group_enrollments e ON c.id = e.class_id AND e.is_active = TRUE
LEFT JOIN hour_forms hf ON hf.class_type = 'hourly_group' AND hf.class_id = c.id
GROUP BY c.id, c.class_name, sub.subject_name, g.grade_name, hgs.subtype_name, c.is_active, c.created_at;

-- View: Class Summary (Hourly Individual)
CREATE VIEW v_hourly_individual_class_summary AS
SELECT 
    c.id as class_id,
    c.class_name,
    sub.subject_name,
    g.grade_name,
    s.first_name as student_first_name,
    s.last_name as student_last_name,
    s.nickname as student_nickname,
    COUNT(hf.id) as total_sessions,
    COALESCE(SUM(hf.hours_taught), 0) as total_hours_taught,
    c.is_active,
    c.created_at
FROM hourly_individual_classes c
JOIN subjects sub ON c.subject_id = sub.id
LEFT JOIN grades g ON c.grade_id = g.id
JOIN students s ON c.student_id = s.id
LEFT JOIN hour_forms hf ON hf.class_type = 'hourly_individual' AND hf.class_id = c.id
GROUP BY c.id, c.class_name, sub.subject_name, g.grade_name, 
         s.first_name, s.last_name, s.nickname, c.is_active, c.created_at;

-- View: Tutor Hour Forms Summary
CREATE VIEW v_tutor_hour_forms_summary AS
SELECT 
    hf.tutor_id,
    u.username,
    u.nickname as tutor_nickname,
    hf.class_type,
    hf.class_id,
    sub.subject_name,
    hf.teaching_date,
    hf.hours_taught,
    hf.students_present,
    hf.students_absent,
    hf.content,
    hf.remark,
    hf.created_at
FROM hour_forms hf
JOIN users u ON hf.tutor_id = u.id
JOIN subjects sub ON hf.subject_id = sub.id
ORDER BY hf.teaching_date DESC, hf.created_at DESC;

-- View: Math Forms Summary
CREATE VIEW v_math_forms_summary AS
SELECT 
    mf.tutor_id,
    u.username,
    u.nickname as tutor_nickname,
    mf.class_id,
    mc.class_name,
    sub.subject_name,
    mf.teaching_date,
    mf.hours_taught,
    mf.students_present,
    mf.students_absent,
    mf.content,
    mf.remark,
    mf.created_at
FROM math_forms mf
JOIN users u ON mf.tutor_id = u.id
JOIN monthly_classes mc ON mf.class_id = mc.id
JOIN subjects sub ON mf.subject_id = sub.id
ORDER BY mf.teaching_date DESC, mf.created_at DESC;

-- View: Science Forms Summary
CREATE VIEW v_science_forms_summary AS
SELECT 
    sf.tutor_id,
    u.username,
    u.nickname as tutor_nickname,
    sf.class_id,
    mc.class_name,
    sub.subject_name,
    sf.teaching_date,
    sf.hours_taught,
    sf.students_present,
    sf.students_absent,
    sf.content,
    sf.remark,
    sf.created_at
FROM science_forms sf
JOIN users u ON sf.tutor_id = u.id
JOIN monthly_classes mc ON sf.class_id = mc.id
JOIN subjects sub ON sf.subject_id = sub.id
ORDER BY sf.teaching_date DESC, sf.created_at DESC;

-- ============================================
-- INSERT INITIAL DATA
-- ============================================

-- Insert Grades
INSERT INTO grades (grade_name, grade_order, next_grade_id) VALUES
('ม.1', 1, NULL), ('ม.2', 2, NULL), ('ม.3', 3, NULL),
('ม.4', 4, NULL), ('ม.5', 5, NULL), ('ม.6', 6, NULL),
('อื่น ๆ', 7, NULL);

UPDATE grades g1
JOIN grades g2 ON g2.grade_order = g1.grade_order + 1
SET g1.next_grade_id = g2.id
WHERE g1.grade_order < 7;

-- Insert Subjects
INSERT INTO subjects (subject_name, display_order) VALUES
('คณิตศาสตร์', 1), 
('ฟิสิกส์', 2), 
('เคมี', 3), 
('ชีววิทยา', 4), 
('ภาษาต่างประเทศ', 5);

-- Insert Hourly Group Subtypes
INSERT INTO hourly_group_subtypes (subtype_name, display_order) VALUES
('A', 1), ('B', 2), ('C', 3), ('PV', 4);

-- Insert Monthly Subtypes
INSERT INTO monthly_subtypes (subtype_name, display_order) VALUES
('จันทร์-ศุกร์', 1), 
('อังคาร-พฤหัส', 2), 
('เสาร์', 3), 
('อาทิตย์', 4);

-- Insert Sample Users
INSERT INTO users (username, password, role, nickname) VALUES
('mod', '$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.', 'admin', 'มด'),
('bam', '$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.', 'tutor', 'แบม'),
('nueng', '$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.', 'tutor', 'หนึ่ง'),
('year', '$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.', 'tutor', 'เยียร์'),
('pol', '$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.', 'tutor', 'พล');

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_auto_progress_grades$$

CREATE PROCEDURE sp_auto_progress_grades(
    IN p_date DATE,
    IN p_exclude_student_ids TEXT -- รายการ student_id ที่ยกเว้น (เช่น "1,5,10")
)
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE v_student_id BIGINT;
    DECLARE v_old_grade_id BIGINT;
    DECLARE v_new_grade_id BIGINT;
    DECLARE v_count INT DEFAULT 0;

    DECLARE cur_students CURSOR FOR
        SELECT s.id, s.grade_id, g.next_grade_id
        FROM students s
        JOIN grades g ON s.grade_id = g.id
        WHERE s.is_active = TRUE
          AND g.next_grade_id IS NOT NULL
          AND (
              p_exclude_student_ids IS NULL 
              OR FIND_IN_SET(s.id, p_exclude_student_ids) = 0
          );

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur_students;

    read_loop: LOOP
        FETCH cur_students INTO v_student_id, v_old_grade_id, v_new_grade_id;
        IF done = 1 THEN
            LEAVE read_loop;
        END IF;

        -- Update grade
        UPDATE students
        SET grade_id = v_new_grade_id,
            updated_at = NOW()
        WHERE id = v_student_id;

        -- Insert history
        INSERT INTO grade_progression_history (
            student_id,
            old_grade_id,
            new_grade_id,
            progression_date,
            progression_type,
            academic_year,
            notes,
            created_by
        ) VALUES (
            v_student_id,
            v_old_grade_id,
            v_new_grade_id,
            p_date,
            'auto',
            CONCAT(YEAR(p_date) - 1, '/', YEAR(p_date)),
            'Auto grade progression',
            NULL
        );
        
        SET v_count = v_count + 1;
    END LOOP;

    CLOSE cur_students;
    
    SELECT CONCAT('Progressed ', v_count, ' students successfully') as result;
END$$

DELIMITER ;

SELECT 'BBP Tutor Database v3 created successfully!' as status;

-- *****
-- Auto Progress Grades (run on March 1st)
-- CALL sp_auto_progress_grades('2025-03-01', NULL);
-- CALL sp_auto_progress_grades('2025-03-01', '1,3');
