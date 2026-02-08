-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bbp_tutor_v3
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'LOGIN, LOGOUT, CREATE, UPDATE, DELETE, ASSIGN, etc.',
  `entity_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'student, class, enrollment, receipt, etc.',
  `entity_id` bigint DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `session_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_action` (`action`),
  KEY `idx_entity` (`entity_type`,`entity_id`),
  KEY `idx_created_at` (`created_at` DESC),
  CONSTRAINT `activity_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_history`
--

DROP TABLE IF EXISTS `class_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'hourly_group, hourly_individual, monthly',
  `class_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_class` (`class_type`,`class_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `class_history_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_history`
--

LOCK TABLES `class_history` WRITE;
/*!40000 ALTER TABLE `class_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_purchase_history`
--

DROP TABLE IF EXISTS `course_purchase_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_purchase_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `purchase_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_purchase` (`purchase_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `course_purchase_history_ibfk_1` FOREIGN KEY (`purchase_id`) REFERENCES `student_course_purchases` (`id`) ON DELETE CASCADE,
  CONSTRAINT `course_purchase_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_purchase_history`
--

LOCK TABLES `course_purchase_history` WRITE;
/*!40000 ALTER TABLE `course_purchase_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_purchase_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_history`
--

DROP TABLE IF EXISTS `enrollment_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enrollment_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'hourly_group, monthly',
  `enrollment_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status_change_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลที่บันทึกไว้ตอนเปลี่ยน status',
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_enrollment` (`enrollment_type`,`enrollment_id`),
  KEY `idx_class` (`class_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `enrollment_history_ibfk_1` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_history`
--

LOCK TABLES `enrollment_history` WRITE;
/*!40000 ALTER TABLE `enrollment_history` DISABLE KEYS */;
INSERT INTO `enrollment_history` VALUES (1,'monthly',1,1,1,'UPDATE','is_active','true','false',1,'2026-01-04 08:13:21','น้องขอหยุดเรียน'),(2,'monthly',2,1,2,'UPDATE','is_active','true','false',1,'2026-01-04 08:13:41','น้องเลื่อนระดับชั้น'),(3,'hourly-group',1,1,2,'UPDATE','is_active','true','false',1,'2026-01-04 08:20:35','น้องขอหยุดเรียน'),(4,'hourly-group',1,1,2,'UPDATE','is_active','false','true',1,'2026-01-04 08:21:17','น้องกลับมาเรียน'),(5,'hourly-group',1,1,2,'UPDATE','is_active','true','false',1,'2026-01-04 08:22:19','น้องขอหยุดเรียน'),(6,'hourly-group',1,1,2,'UPDATE','is_active','false','true',1,'2026-01-04 08:24:01','อื่น ๆ (แจ้งหัวหน้า)'),(7,'hourly-group',2,1,6,'UPDATE','is_active','true','false',1,'2026-01-04 08:25:07','แอดมินนำน้องเข้าผิดคลาส'),(8,'monthly',11,3,12,'UPDATE','is_active','true','false',1,'2026-01-06 09:41:30','น้องเลื่อนระดับชั้น'),(9,'hourly-group',3,1,4,'UPDATE','is_active','true','false',1,'2026-01-06 09:42:00','น้องเลื่อนระดับชั้น'),(10,'hourly-individual',2,1,6,'UPDATE','is_active','true','false',1,'2026-01-06 09:44:26','แอดมินนำน้องเข้าผิดคลาส'),(11,'monthly',10,2,11,'UPDATE','is_active','true','false',1,'2026-01-06 09:50:09','น้องเลื่อนระดับชั้น'),(12,'monthly',1,1,1,'UPDATE','is_active','true','false',1,'2026-01-06 09:50:51','น้องเลื่อนระดับชั้น'),(13,'monthly',10,2,11,'UPDATE','is_active','true','false',1,'2026-01-06 09:51:10','น้องเลื่อนระดับชั้น'),(14,'monthly',2,1,2,'UPDATE','is_active','true','false',1,'2026-01-06 09:51:32','น้องเลื่อนระดับชั้น'),(15,'monthly',2,1,2,'UPDATE','is_active','true','false',1,'2026-01-06 09:53:10','น้องเลื่อนระดับชั้น'),(16,'monthly',1,1,1,'UPDATE','is_active','true','false',1,'2026-01-06 09:56:10','น้องเลื่อนระดับชั้น'),(17,'monthly',3,1,3,'UPDATE','is_active','true','false',1,'2026-01-06 09:58:32','น้องเลื่อนระดับชั้น'),(18,'monthly',8,1,8,'UPDATE','is_active','true','false',1,'2026-01-06 09:59:20','น้องเลื่อนระดับชั้น'),(19,'monthly',10,2,11,'UPDATE','is_active','true','false',1,'2026-01-06 09:59:32','น้องเลื่อนระดับชั้น'),(20,'monthly',16,3,5,'UPDATE','is_active','true','false',1,'2026-01-06 10:07:05','น้องเลื่อนระดับชั้น'),(21,'monthly',11,3,12,'UPDATE','is_active','false','true',1,'2026-01-06 10:11:25','น้องกลับมาเรียน'),(22,'monthly',14,2,3,'UPDATE','is_active','true','false',1,'2026-01-06 10:17:15','น้องเลื่อนระดับชั้น'),(23,'monthly',13,2,2,'UPDATE','is_active','true','false',1,'2026-01-06 10:17:18','น้องเลื่อนระดับชั้น'),(24,'hourly-individual',6,3,6,'UPDATE','is_active','true','false',1,'2026-01-06 10:23:59','น้องเลื่อนระดับชั้น'),(25,'hourly-individual',9,4,6,'UPDATE','is_active','true','false',1,'2026-01-06 10:27:58','น้องเลื่อนระดับชั้น'),(26,'hourly-individual',8,4,4,'UPDATE','is_active','true','false',1,'2026-01-06 10:32:41','อื่น ๆ (แจ้งหัวหน้า)'),(27,'hourly-individual',17,5,4,'UPDATE','is_active','true','false',1,'2026-01-06 10:46:45','น้องเลื่อนระดับชั้น'),(28,'hourly-individual',20,6,6,'UPDATE','is_active','true','false',1,'2026-01-06 10:52:26','น้องเลื่อนระดับชั้น'),(29,'hourly-individual',4,1,3,'UPDATE','is_active','true','false',1,'2026-01-06 11:34:54','อื่น ๆ (แจ้งหัวหน้า)'),(30,'monthly',1,1,1,'UPDATE','is_active','false','true',1,'2026-01-06 11:39:30','น้องกลับมาเรียน'),(31,'hourly-individual',26,7,6,'UPDATE','is_active','true','false',1,'2026-01-06 11:50:24','แอดมินนำน้องเข้าผิดคลาส'),(32,'monthly',1,1,1,'UPDATE','is_active','true','false',1,'2026-01-06 11:55:49','น้องขอจบคอร์ส'),(33,'monthly',19,3,1,'UPDATE','is_active','true','false',1,'2026-01-06 11:58:23','น้องขอจบคอร์ส'),(34,'monthly',12,2,1,'UPDATE','is_active','true','false',1,'2026-01-06 11:58:31','น้องขอจบคอร์ส'),(35,'hourly-group',1,1,2,'UPDATE','is_active','true','false',1,'2026-01-06 12:17:02','น้องขอจบคอร์ส'),(36,'monthly',2,1,2,'UPDATE','is_active','false','true',1,'2026-01-06 12:19:17','น้องกลับมาเรียน'),(37,'monthly',18,3,2,'UPDATE','is_active','true','false',1,'2026-01-06 22:53:46','น้องขอหยุดเรียน'),(38,'monthly',18,3,2,'UPDATE','is_active','false','true',1,'2026-01-06 22:54:23','น้องกลับมาเรียน'),(39,'hourly-group',1,1,2,'UPDATE','is_active','false','true',1,'2026-01-06 23:00:03','น้องกลับมาเรียน'),(40,'monthly',2,1,2,'UPDATE','is_active','true','false',1,'2026-01-07 07:48:18','น้องเลื่อนระดับชั้น'),(41,'monthly',28,4,15,'UPDATE','is_active','true','false',1,'2026-01-07 08:43:52','น้องเลื่อนระดับชั้น'),(42,'monthly',31,4,17,'UPDATE','is_active','true','false',1,'2026-01-07 08:43:52','น้องเลื่อนระดับชั้น'),(43,'monthly',30,4,18,'UPDATE','is_active','true','false',1,'2026-01-07 08:43:52','น้องเลื่อนระดับชั้น'),(44,'monthly',29,4,16,'UPDATE','is_active','true','false',1,'2026-01-07 08:43:52','น้องเลื่อนระดับชั้น'),(45,'hourly-group',1,1,2,'UPDATE','is_active','true','false',1,'2026-01-07 08:46:52','น้องเลื่อนระดับชั้น'),(46,'monthly',33,7,20,'UPDATE','is_active','true','false',1,'2026-01-07 08:58:16','น้องเลื่อนระดับชั้น'),(47,'monthly',32,7,19,'UPDATE','is_active','true','false',1,'2026-01-07 08:58:16','น้องเลื่อนระดับชั้น'),(48,'monthly',34,7,21,'UPDATE','is_active','true','false',1,'2026-01-07 08:58:16','น้องเลื่อนระดับชั้น'),(49,'monthly',36,9,19,'UPDATE','is_active','true','false',1,'2026-01-07 08:59:59','น้องขอหยุดเรียน'),(50,'monthly',38,9,22,'UPDATE','is_active','true','false',1,'2026-01-07 09:00:20','น้องเลื่อนระดับชั้น'),(51,'monthly',35,9,20,'UPDATE','is_active','true','false',1,'2026-01-07 09:00:20','น้องเลื่อนระดับชั้น'),(52,'monthly',37,9,21,'UPDATE','is_active','true','false',1,'2026-01-07 09:00:20','น้องเลื่อนระดับชั้น'),(53,'hourly-group',6,2,13,'UPDATE','is_active','true','false',1,'2026-01-07 09:09:26','น้องขอหยุดเรียน'),(54,'hourly-group',5,2,22,'UPDATE','is_active','true','false',1,'2026-01-07 09:10:52','น้องเลื่อนระดับชั้น'),(55,'hourly-group',4,2,17,'UPDATE','is_active','true','false',1,'2026-01-07 09:10:52','น้องเลื่อนระดับชั้น'),(56,'hourly-group',9,4,23,'UPDATE','is_active','true','false',1,'2026-01-07 09:14:21','อื่น ๆ (แจ้งหัวหน้า)'),(57,'monthly',41,10,21,'UPDATE','is_active','true','false',1,'2026-01-07 09:28:39','น้องเลื่อนระดับชั้น'),(58,'monthly',40,10,20,'UPDATE','is_active','true','false',1,'2026-01-07 09:28:39','น้องเลื่อนระดับชั้น'),(59,'monthly',39,10,22,'UPDATE','is_active','true','false',1,'2026-01-07 09:28:39','น้องเลื่อนระดับชั้น'),(60,'hourly-group',7,4,22,'UPDATE','is_active','true','false',1,'2026-01-07 09:30:55','น้องเลื่อนระดับชั้น'),(61,'hourly-group',8,4,17,'UPDATE','is_active','true','false',1,'2026-01-07 09:30:55','น้องเลื่อนระดับชั้น'),(62,'hourly-group',10,2,10,'UPDATE','is_active','true','false',1,'2026-01-07 09:32:47','น้องเลื่อนระดับชั้น'),(63,'monthly',32,7,19,'UPDATE','is_active','false','true',1,'2026-01-07 09:56:47','อื่น ๆ (แจ้งหัวหน้า)'),(64,'monthly',1,1,1,'UPDATE','is_active','false','true',1,'2026-01-07 10:10:20','น้องกลับมาเรียน'),(65,'hourly-group',1,1,2,'UPDATE','is_active','false','true',1,'2026-01-10 23:07:32','น้องกลับมาเรียน'),(66,'hourly-group',1,1,2,'UPDATE','is_active','true','false',1,'2026-01-10 23:07:39','น้องขอหยุดเรียน'),(67,'hourly-group',1,1,2,'UPDATE','is_active','false','true',1,'2026-01-10 23:11:13','น้องกลับมาเรียน'),(68,'monthly',32,7,19,'UPDATE','is_active','true','false',1,'2026-01-10 23:23:23','น้องเลื่อนระดับชั้น'),(69,'hourly-group',13,16,27,'UPDATE','is_active','true','false',1,'2026-01-30 03:34:40','แอดมินนำน้องเข้าผิดคลาส'),(70,'monthly',45,12,2,'UPDATE','is_active','true','false',1,'2026-02-07 10:56:35','น้องขอหยุดเรียน'),(71,'hourly-group',4,2,17,'UPDATE','is_active','false','true',1,'2026-02-07 11:11:57','น้องกลับมาเรียน');
/*!40000 ALTER TABLE `enrollment_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_progression_history`
--

DROP TABLE IF EXISTS `grade_progression_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_progression_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `old_grade_id` bigint NOT NULL,
  `new_grade_id` bigint NOT NULL,
  `progression_date` date NOT NULL,
  `progression_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'manual' COMMENT 'auto, manual',
  `academic_year` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `old_grade_id` (`old_grade_id`),
  KEY `new_grade_id` (`new_grade_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_student` (`student_id`),
  KEY `idx_progression_date` (`progression_date`),
  KEY `idx_type` (`progression_type`),
  CONSTRAINT `grade_progression_history_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grade_progression_history_ibfk_2` FOREIGN KEY (`old_grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `grade_progression_history_ibfk_3` FOREIGN KEY (`new_grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `grade_progression_history_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_progression_history`
--

LOCK TABLES `grade_progression_history` WRITE;
/*!40000 ALTER TABLE `grade_progression_history` DISABLE KEYS */;
INSERT INTO `grade_progression_history` VALUES (1,8,1,2,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(2,7,3,4,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(3,1,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(4,2,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(5,3,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(6,4,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(7,5,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(8,6,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(9,9,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(10,10,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(11,11,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(12,14,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(13,15,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(14,16,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(15,18,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(16,19,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(17,20,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(18,21,4,5,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(19,12,5,6,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:05'),(20,13,5,6,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:06'),(21,17,5,6,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:06'),(22,22,5,6,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:06'),(23,23,6,7,'2025-03-01','auto','2024/2025','Auto grade progression',NULL,'2026-01-07 16:15:06');
/*!40000 ALTER TABLE `grade_progression_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grades` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grade_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `grade_order` int NOT NULL,
  `next_grade_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `grade_name` (`grade_name`),
  UNIQUE KEY `grade_order` (`grade_order`),
  KEY `idx_order` (`grade_order`),
  KEY `next_grade_id` (`next_grade_id`),
  CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`next_grade_id`) REFERENCES `grades` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grades`
--

LOCK TABLES `grades` WRITE;
/*!40000 ALTER TABLE `grades` DISABLE KEYS */;
INSERT INTO `grades` VALUES (1,'ม.1',1,2),(2,'ม.2',2,3),(3,'ม.3',3,4),(4,'ม.4',4,5),(5,'ม.5',5,6),(6,'ม.6',6,7),(7,'อื่น ๆ',7,NULL);
/*!40000 ALTER TABLE `grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hour_form_history`
--

DROP TABLE IF EXISTS `hour_form_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_form_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hour_form_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_hour_form` (`hour_form_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `hour_form_history_ibfk_1` FOREIGN KEY (`hour_form_id`) REFERENCES `hour_forms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hour_form_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hour_form_history`
--

LOCK TABLES `hour_form_history` WRITE;
/*!40000 ALTER TABLE `hour_form_history` DISABLE KEYS */;
INSERT INTO `hour_form_history` VALUES (1,5,'UPDATE','hoursTaught','14.00','12',1,'2026-01-04 08:53:34'),(2,7,'UPDATE','hoursTaught','4.00','0',1,'2026-01-04 08:54:58'),(3,5,'UPDATE','hoursTaught','12.00','4',1,'2026-01-04 09:03:27'),(4,5,'UPDATE','remark','ครูกรอกเกิน','น้องขอจบคอร์ส',1,'2026-01-04 09:03:27'),(5,22,'UPDATE','hoursTaught','2.00','0',1,'2026-01-04 21:54:57'),(6,22,'UPDATE','remark',NULL,'ครูกรอกเกิน',1,'2026-01-04 21:54:57'),(7,23,'UPDATE','hoursTaught','2.00','0',1,'2026-01-04 21:56:05'),(8,23,'UPDATE','remark',NULL,'น้องขอจบคอร์ส',1,'2026-01-04 21:56:05'),(9,27,'UPDATE','hoursTaught','12.00','10',1,'2026-01-04 22:02:48'),(10,27,'UPDATE','remark',NULL,'ครูกรอกเกิน',1,'2026-01-04 22:02:48'),(11,1,'UPDATE','hoursTaught','2.00','0',1,'2026-01-06 23:01:24'),(12,1,'UPDATE','remark',NULL,'ครูกรอกเกิน',1,'2026-01-06 23:01:24'),(13,46,'UPDATE','hoursTaught','2.00','0',1,'2026-01-10 23:09:32'),(14,46,'UPDATE','remark',NULL,'ครูกรอกเกิน',1,'2026-01-10 23:09:32'),(15,64,'UPDATE','hoursTaught','2.00','0',1,'2026-01-18 02:16:20'),(16,64,'UPDATE','remark',NULL,'แก้วันเรียน',1,'2026-01-18 02:16:20'),(17,65,'UPDATE','remark',NULL,'แอดมินนำเด็กออกจากคลาสไม่ทัน',1,'2026-01-18 07:46:06'),(18,65,'UPDATE','remark','แอดมินนำเด็กออกจากคลาสไม่ทัน','แก้วันเรียน',1,'2026-01-18 07:47:32');
/*!40000 ALTER TABLE `hour_form_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hour_forms`
--

DROP TABLE IF EXISTS `hour_forms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hour_forms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tutor_id` bigint NOT NULL,
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'hourly_group, hourly_individual',
  `class_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `teaching_date` date NOT NULL,
  `hours_taught` decimal(7,2) NOT NULL,
  `students_present` int NOT NULL DEFAULT '0',
  `students_absent` int NOT NULL DEFAULT '0',
  `remark` text COLLATE utf8mb4_unicode_ci,
  `is_admin_created` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_tutor` (`tutor_id`),
  KEY `idx_tutor_date` (`tutor_id`,`teaching_date`),
  KEY `idx_class` (`class_type`,`class_id`),
  KEY `idx_teaching_date` (`teaching_date`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_class_date` (`class_type`,`class_id`,`teaching_date` DESC),
  CONSTRAINT `hour_forms_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hour_forms_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `hour_forms_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `chk_hours_taught_positive` CHECK ((`hours_taught` > 0)),
  CONSTRAINT `chk_students_valid` CHECK (((`students_present` >= 0) and (`students_absent` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hour_forms`
--

LOCK TABLES `hour_forms` WRITE;
/*!40000 ALTER TABLE `hour_forms` DISABLE KEYS */;
INSERT INTO `hour_forms` VALUES (1,3,'hourly_group',1,1,'1','2026-01-04',2.00,2,0,'',0,'2026-01-04 08:15:36','2026-01-04 08:15:36',NULL),(2,3,'hourly_group',1,1,'2','2026-01-04',2.00,1,0,'',0,'2026-01-04 08:20:51','2026-01-04 08:20:51',NULL),(3,3,'hourly_group',1,1,'3','2026-01-04',2.00,2,0,'',0,'2026-01-04 08:21:26','2026-01-04 08:21:26',NULL),(4,3,'hourly_group',1,1,'4','2026-01-04',2.00,1,0,'',0,'2026-01-04 08:21:50','2026-01-04 08:21:50',NULL),(5,3,'hourly_group',1,1,'ไม่มี','2026-01-05',14.00,1,0,'น้องขอจบคอร์ส',1,'2026-01-04 08:24:34','2026-01-04 08:24:34',NULL),(6,3,'hourly_group',1,1,'6','2026-01-04',2.00,1,0,'',0,'2026-01-04 08:27:16','2026-01-04 08:27:16',NULL),(7,5,'hourly_group',1,1,'001','2026-01-04',4.00,1,0,'แอดมินนำเด็กเข้าคลาสไม่ทัน',1,'2026-01-04 08:54:24','2026-01-04 08:54:24',NULL),(8,3,'hourly_group',1,1,'0001','2026-01-04',4.00,1,0,'',0,'2026-01-04 09:12:10','2026-01-04 09:12:10',NULL),(9,3,'hourly_group',1,1,'0000102','2026-01-24',2.00,1,0,'',0,'2026-01-04 09:12:35','2026-01-04 09:14:30',NULL),(10,3,'hourly_individual',1,1,'-1','2026-01-04',2.00,2,0,'',0,'2026-01-04 09:43:47','2026-01-04 09:43:47',NULL),(11,3,'hourly_individual',2,1,'-1','2026-01-04',2.00,1,0,'กกกกก',0,'2026-01-04 09:45:48','2026-01-07 11:17:16',NULL),(12,6,'hourly_group',1,1,'01','2026-01-05',2.00,1,0,'',0,'2026-01-04 20:36:46','2026-01-04 20:36:46',NULL),(13,6,'hourly_group',1,1,'/1','2026-01-05',2.00,1,0,'',0,'2026-01-04 20:39:05','2026-01-04 20:39:05',NULL),(14,6,'hourly_group',1,1,'/2','2026-01-05',2.00,1,0,'',0,'2026-01-04 20:39:12','2026-01-04 20:39:12',NULL),(15,6,'hourly_group',1,1,'/3','2026-01-05',2.00,2,0,'',0,'2026-01-04 20:40:00','2026-01-04 20:40:00',NULL),(16,6,'hourly_group',1,1,'/4','2026-01-05',8.00,2,1,'',0,'2026-01-04 20:41:28','2026-01-04 20:41:28',NULL),(17,6,'hourly_group',1,1,'/5','2026-01-05',2.00,1,0,'',0,'2026-01-04 20:41:52','2026-01-04 20:41:52',NULL),(18,6,'hourly_group',1,1,'/7','2026-01-06',2.00,1,0,'แอดมินนำเด็กเข้าคลาสไม่ทัน',1,'2026-01-04 20:43:18','2026-01-04 20:43:18',NULL),(19,6,'hourly_group',1,1,'//1','2026-01-05',2.00,1,0,'',0,'2026-01-04 21:47:13','2026-01-04 21:47:13',NULL),(20,6,'hourly_group',1,1,'//2','2026-01-05',4.00,1,0,'',0,'2026-01-04 21:47:44','2026-01-04 21:47:44',NULL),(21,3,'hourly_individual',1,1,'+1','2026-01-05',8.00,2,0,'',0,'2026-01-04 21:53:56','2026-01-04 21:53:56',NULL),(22,3,'hourly_individual',1,1,'+2','2026-01-06',2.00,1,0,'',0,'2026-01-04 21:54:19','2026-01-04 21:54:19',NULL),(23,3,'hourly_individual',1,1,'+3','2026-01-05',2.00,1,0,'',0,'2026-01-04 21:55:10','2026-01-04 21:55:10',NULL),(24,3,'hourly_individual',1,1,'+5','2026-01-05',2.50,2,0,'',0,'2026-01-04 21:56:25','2026-01-04 21:56:25',NULL),(25,3,'hourly_individual',2,1,'0101','2026-01-05',8.00,1,0,'',0,'2026-01-04 22:01:39','2026-01-04 22:01:39',NULL),(26,3,'hourly_individual',2,1,'0102','2026-01-05',2.00,1,0,'',0,'2026-01-04 22:01:48','2026-01-04 22:01:48',NULL),(27,3,'hourly_individual',2,1,'0103','2026-01-05',12.00,1,0,'',0,'2026-01-04 22:02:10','2026-01-04 22:02:10',NULL),(28,3,'hourly_individual',2,1,'0103','2026-01-05',2.00,1,0,'',0,'2026-01-04 22:03:03','2026-01-04 22:03:03',NULL),(29,3,'hourly_individual',1,1,'241245','2026-01-05',12.00,1,0,'',0,'2026-01-04 22:12:49','2026-01-04 22:12:49',NULL),(30,3,'hourly_individual',2,1,'12','2026-01-05',2.00,1,0,'',0,'2026-01-05 09:12:29','2026-01-05 09:12:29',NULL),(31,3,'hourly_individual',1,1,'241246','2026-01-05',2.00,1,0,'',0,'2026-01-05 09:15:20','2026-01-05 09:15:20',NULL),(32,3,'hourly_individual',1,1,'241247','2026-01-05',6.00,1,0,'',0,'2026-01-05 09:16:24','2026-01-05 09:16:24',NULL),(33,3,'hourly_individual',1,1,'xxxxx','2026-01-05',12.00,2,0,'',0,'2026-01-05 09:17:28','2026-01-05 09:17:28',NULL),(34,3,'hourly_individual',2,1,'54545454545','2026-01-05',8.50,1,0,'',0,'2026-01-05 09:25:54','2026-01-05 09:25:54',NULL),(35,3,'hourly_individual',2,1,'5625752725','2026-01-05',2.00,1,0,'',0,'2026-01-05 09:26:06','2026-01-05 09:26:06',NULL),(36,3,'hourly_individual',2,1,'5454555757','2026-01-05',12.00,1,0,'',0,'2026-01-05 09:27:16','2026-01-05 09:27:16',NULL),(37,3,'hourly_individual',1,1,'458278785','2026-01-05',5.00,2,1,'',0,'2026-01-05 09:28:24','2026-01-05 09:28:24',NULL),(38,3,'hourly_individual',1,1,'426456','2026-01-05',12.00,1,0,'',0,'2026-01-05 09:34:21','2026-01-05 09:34:21',NULL),(39,3,'hourly_individual',1,1,'หกอะฟพด','2026-01-05',12.00,2,0,'',0,'2026-01-05 09:36:47','2026-01-05 09:36:47',NULL),(40,3,'hourly_individual',1,1,'445224','2026-01-05',12.00,1,1,'',0,'2026-01-05 09:38:49','2026-01-05 09:38:49',NULL),(41,3,'hourly_individual',1,1,'ะีืกืัีกัื','2026-01-05',12.00,1,0,'',0,'2026-01-05 09:46:53','2026-01-05 09:46:53',NULL),(42,3,'hourly_individual',1,1,'อหเ','2026-01-07',2.00,1,0,'',0,'2026-01-06 12:13:38','2026-01-06 12:13:38',NULL),(43,3,'hourly_group',1,1,'้นมเใ','2026-01-07',20.00,2,0,'',0,'2026-01-06 12:13:45','2026-01-06 12:13:45',NULL),(44,3,'hourly_group',1,1,'หหหหหหหห454545','2026-01-07',1.00,1,0,'',0,'2026-01-06 13:28:04','2026-01-06 13:28:39',NULL),(45,3,'hourly_group',1,1,'545224','2026-01-02',2.00,1,0,'แอดมินนำเด็กเข้าคลาสไม่ทัน',1,'2026-01-06 23:00:40','2026-01-06 23:00:40',NULL),(46,3,'hourly_group',1,1,'453626','2025-12-26',2.00,1,0,'',0,'2026-01-07 07:05:51','2026-01-07 07:05:51',NULL),(47,3,'hourly_individual',8,2,'2222222','2025-12-12',2.00,1,0,'แอดมินนำเด็กเข้าคลาสไม่ทัน',1,'2026-01-07 07:20:55','2026-01-07 07:20:55',NULL),(48,5,'hourly_group',2,6,'test','2026-01-07',2.00,1,0,'test',0,'2026-01-07 09:09:07','2026-01-08 00:22:44',NULL),(49,5,'hourly_group',2,6,'test','2026-01-07',14.00,20,0,'test',0,'2026-01-07 09:09:38','2026-01-08 00:22:57',NULL),(50,5,'hourly_group',2,6,'test','2026-01-07',2.00,1,0,'test',0,'2026-01-07 09:10:05','2026-01-08 00:23:05',NULL),(51,5,'hourly_group',2,6,'test','2026-01-07',4.00,1,0,'test',0,'2026-01-07 09:11:27','2026-01-08 00:23:13',NULL),(52,3,'hourly_group',4,6,'0.0','2026-01-07',2.00,1,0,'',0,'2026-01-07 09:13:13','2026-01-07 09:13:13',NULL),(53,3,'hourly_group',4,6,'0.1','2026-01-07',16.00,2,0,'',0,'2026-01-07 09:14:26','2026-01-07 09:14:26',NULL),(54,3,'hourly_group',4,6,'4545454545\n4\n\n000','2026-01-07',4.00,1,0,'',0,'2026-01-07 09:34:12','2026-01-07 09:34:12',NULL),(55,3,'hourly_group',4,6,'หกหแ หกด ฟห','2026-01-07',14.00,1,0,'',0,'2026-01-07 09:36:20','2026-01-07 09:36:20',NULL),(56,3,'hourly_group',4,6,'777','2026-01-07',2.00,1,0,'น้องขอจบคอร์ส',1,'2026-01-07 09:37:42','2026-01-07 09:37:42',NULL),(57,3,'hourly_group',1,1,'srrynsrsynrnrn','2026-01-08',2.00,1,0,'',0,'2026-01-07 10:16:27','2026-01-07 10:16:27',NULL),(58,3,'hourly_group',1,1,'ำเพิหเพิ','2026-01-08',2.00,1,0,'',0,'2026-01-07 11:21:16','2026-01-07 11:21:16',NULL),(59,8,'hourly_group',6,3,'test','2026-01-15',2.00,1,0,'test',0,'2026-01-08 00:21:02','2026-01-08 00:21:58',NULL),(60,3,'hourly_group',15,2,'การเคลื่อนที่แนวตรง, การเคลื่อนที่แบบวงกลม','2026-01-07',2.00,3,0,'',0,'2026-01-08 00:41:42','2026-01-08 00:43:29',NULL),(61,3,'hourly_group',15,2,'คลื่นนิ่ง','2025-12-26',2.00,3,1,'',0,'2026-01-08 00:47:38','2026-01-18 00:55:13',NULL),(62,3,'hourly_group',15,2,'test','2026-01-08',2.00,3,0,'ยังไม่ทราบจำนวนน้องที่ขาดแน่นอน',0,'2026-01-08 00:50:08','2026-01-08 00:50:08',NULL),(63,3,'hourly_group',1,1,'-','2026-01-11',2.00,1,0,'แอดมินนำเด็กเข้าคลาสไม่ทัน',1,'2026-01-10 23:12:01','2026-01-10 23:12:01',NULL),(64,3,'hourly_group',15,2,'ฟฟฟ เกกกกssss','2026-01-18',2.00,74,0,'',0,'2026-01-18 02:15:51','2026-01-18 07:40:09',NULL),(65,10,'hourly_group',16,2,'คลื่นกล','2026-01-18',2.00,3,0,'แก้วันเรียน',0,'2026-01-18 07:44:47','2026-01-18 07:54:49',NULL),(66,3,'hourly_group',15,2,'444','2026-01-18',2.00,1,0,'',0,'2026-01-18 08:02:03','2026-01-18 08:02:03',NULL),(67,3,'hourly_group',15,2,'อพหออกหดอก 555666777','2026-01-18',2.00,1,0,'้่ด ่ด ่เ',0,'2026-01-18 08:02:20','2026-02-07 10:26:47',NULL),(68,9,'hourly_group',15,2,'44242','2026-01-19',4.00,1,0,'น้องขอจบคอร์ส',1,'2026-01-18 08:03:01','2026-01-18 08:03:01',NULL),(69,3,'hourly_group',15,2,'กกกกกกกกกกกกกก','2026-01-18',2.00,1,0,'น้องขอจบคอร์ส',1,'2026-01-18 08:03:25','2026-01-18 08:03:25',NULL),(70,10,'hourly_group',16,2,'กกกกกก5555','2026-01-18',2.00,1,0,'',0,'2026-01-18 08:27:46','2026-01-18 08:27:46',NULL),(71,3,'hourly_group',2,6,'test ffff','2026-02-08',18.00,1,0,'',0,'2026-02-07 11:12:50','2026-02-07 11:12:50',NULL);
/*!40000 ALTER TABLE `hour_forms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_group_class_tutors`
--

DROP TABLE IF EXISTS `hourly_group_class_tutors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_group_class_tutors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `tutor_id` bigint NOT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_tutor` (`class_id`,`tutor_id`),
  KEY `assigned_by` (`assigned_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_tutor` (`tutor_id`),
  CONSTRAINT `hourly_group_class_tutors_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `hourly_group_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_group_class_tutors_ibfk_2` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_group_class_tutors_ibfk_3` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_group_class_tutors`
--

LOCK TABLES `hourly_group_class_tutors` WRITE;
/*!40000 ALTER TABLE `hourly_group_class_tutors` DISABLE KEYS */;
INSERT INTO `hourly_group_class_tutors` VALUES (2,1,6,'2026-01-04 20:36:23',1),(5,4,7,'2026-01-07 10:12:40',1),(7,14,9,'2026-01-08 00:15:05',1),(8,13,9,'2026-01-08 00:15:08',1),(9,12,9,'2026-01-08 00:15:12',1),(10,11,9,'2026-01-08 00:15:15',1),(11,10,8,'2026-01-08 00:15:19',1),(12,9,8,'2026-01-08 00:15:31',1),(13,8,8,'2026-01-08 00:15:35',1),(14,7,8,'2026-01-08 00:15:38',1),(15,6,8,'2026-01-08 00:15:42',1),(16,15,3,'2026-01-08 00:17:23',1),(17,16,10,'2026-01-18 07:43:19',1),(18,2,3,'2026-02-07 11:12:34',1);
/*!40000 ALTER TABLE `hourly_group_class_tutors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_group_classes`
--

DROP TABLE IF EXISTS `hourly_group_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_group_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_id` bigint NOT NULL,
  `grade_id` bigint DEFAULT NULL,
  `subtype_id` bigint NOT NULL,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `note` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_subtype` (`subtype_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_grade` (`grade_id`),
  KEY `idx_subject_grade` (`subject_id`,`grade_id`),
  KEY `idx_class_active_subject` (`is_active`,`subject_id`,`grade_id`),
  CONSTRAINT `hourly_group_classes_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `hourly_group_classes_ibfk_2` FOREIGN KEY (`subtype_id`) REFERENCES `hourly_group_subtypes` (`id`),
  CONSTRAINT `hourly_group_classes_ibfk_3` FOREIGN KEY (`grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `hourly_group_classes_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_group_classes`
--

LOCK TABLES `hourly_group_classes` WRITE;
/*!40000 ALTER TABLE `hourly_group_classes` DISABLE KEYS */;
INSERT INTO `hourly_group_classes` VALUES (1,1,4,1,'คณิตศาสตร์ ม.4 - A',1,NULL,'2026-01-04 08:14:04','2026-01-04 08:14:04',1),(2,6,5,1,'TPAT1 ม.5 - A',1,NULL,'2026-01-04 08:14:33','2026-01-04 08:14:33',1),(3,1,5,1,'คณิตศาสตร์ ม.5 - A',1,NULL,'2026-01-07 08:46:49','2026-01-07 08:46:49',1),(4,6,6,1,'TPAT1 ม.6 - A',1,NULL,'2026-01-07 09:10:47','2026-01-07 09:10:47',1),(5,4,7,3,'ชีววิทยา อื่น ๆ - C',1,NULL,'2026-01-07 09:51:32','2026-01-07 09:51:32',1),(6,3,4,1,'เคมี ม.4 - A',1,NULL,'2026-01-08 00:11:22','2026-01-08 00:11:22',1),(7,3,4,2,'เคมี ม.4 - B',1,NULL,'2026-01-08 00:11:29','2026-01-08 00:11:29',1),(8,3,4,3,'เคมี ม.4 - C',1,NULL,'2026-01-08 00:11:34','2026-01-08 00:11:34',1),(9,3,5,1,'เคมี ม.5 - A',1,NULL,'2026-01-08 00:11:42','2026-01-08 00:11:42',1),(10,3,5,2,'เคมี ม.5 - B',1,NULL,'2026-01-08 00:11:48','2026-01-08 00:11:48',1),(11,4,4,1,'ชีววิทยา ม.4 - A',1,NULL,'2026-01-08 00:11:59','2026-01-08 00:11:59',1),(12,4,4,2,'ชีววิทยา ม.4 - B',1,NULL,'2026-01-08 00:12:13','2026-01-08 00:12:13',1),(13,4,4,3,'ชีววิทยา ม.4 - C',1,NULL,'2026-01-08 00:12:19','2026-01-08 00:12:19',1),(14,4,5,1,'ชีววิทยา ม.5 - A',1,NULL,'2026-01-08 00:12:24','2026-01-08 00:12:24',1),(15,2,4,2,'ฟิสิกส์ ม.4 - B',1,NULL,'2026-01-08 00:17:13','2026-01-08 00:17:13',1),(16,2,4,3,'ฟิสิกส์ ม.4 - C',1,NULL,'2026-01-10 23:18:47','2026-01-10 23:18:47',1);
/*!40000 ALTER TABLE `hourly_group_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_group_enrollments`
--

DROP TABLE IF EXISTS `hourly_group_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_group_enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `enrolled_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enrolled_grade_id` bigint DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `status_change_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลการ active/inactive',
  `status_changed_by` bigint DEFAULT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
  `status_changed_at` timestamp NULL DEFAULT NULL COMMENT 'เวลาที่เปลี่ยน status',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`,`student_id`),
  KEY `enrolled_grade_id` (`enrolled_grade_id`),
  KEY `fk_hge_status_changed_by` (`status_changed_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_class_active` (`class_id`,`is_active`),
  KEY `idx_status_changed_at` (`status_changed_at`),
  CONSTRAINT `fk_hge_status_changed_by` FOREIGN KEY (`status_changed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `hourly_group_enrollments_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `hourly_group_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_group_enrollments_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_group_enrollments_ibfk_3` FOREIGN KEY (`enrolled_grade_id`) REFERENCES `grades` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_group_enrollments`
--

LOCK TABLES `hourly_group_enrollments` WRITE;
/*!40000 ALTER TABLE `hourly_group_enrollments` DISABLE KEYS */;
INSERT INTO `hourly_group_enrollments` VALUES (1,1,2,NULL,4,1,'น้องกลับมาเรียน',1,'2026-01-10 23:11:13'),(2,1,6,NULL,4,0,'แอดมินนำน้องเข้าผิดคลาส',1,'2026-01-04 08:25:07'),(3,1,4,NULL,4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 09:42:00'),(4,2,17,NULL,5,1,'น้องกลับมาเรียน',1,'2026-02-07 11:11:57'),(5,2,22,NULL,5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:10:52'),(6,2,13,NULL,5,0,'น้องขอหยุดเรียน',1,'2026-01-07 09:09:26'),(7,4,22,NULL,5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:30:55'),(8,4,17,NULL,5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:30:55'),(9,4,23,NULL,6,0,'อื่น ๆ (แจ้งหัวหน้า)',1,'2026-01-07 09:14:21'),(10,2,10,NULL,5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:32:47'),(11,4,10,NULL,5,1,NULL,NULL,NULL),(12,1,24,NULL,4,1,NULL,NULL,NULL),(13,16,27,NULL,4,0,'แอดมินนำน้องเข้าผิดคลาส',1,'2026-01-30 03:34:40'),(14,16,7,NULL,4,1,NULL,NULL,NULL),(15,15,27,NULL,4,1,NULL,NULL,NULL),(16,15,7,NULL,4,1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `hourly_group_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_group_subtypes`
--

DROP TABLE IF EXISTS `hourly_group_subtypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_group_subtypes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subtype_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `subtype_name` (`subtype_name`),
  KEY `idx_active` (`is_active`),
  KEY `idx_display_order` (`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_group_subtypes`
--

LOCK TABLES `hourly_group_subtypes` WRITE;
/*!40000 ALTER TABLE `hourly_group_subtypes` DISABLE KEYS */;
INSERT INTO `hourly_group_subtypes` VALUES (1,'A',1,1,'2026-01-04 15:06:15'),(2,'B',1,2,'2026-01-04 15:06:15'),(3,'C',1,3,'2026-01-04 15:06:15'),(4,'PV',1,4,'2026-01-04 15:06:15'),(5,'D',1,5,'2026-01-10 23:24:55');
/*!40000 ALTER TABLE `hourly_group_subtypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_individual_class_students`
--

DROP TABLE IF EXISTS `hourly_individual_class_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_individual_class_students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `enrolled_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  `status_change_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลการ active/inactive',
  `status_changed_by` bigint DEFAULT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
  `status_changed_at` timestamp NULL DEFAULT NULL COMMENT 'เวลาที่เปลี่ยน status',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`,`student_id`),
  KEY `fk_hics_status_changed_by` (`status_changed_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_status_changed_at` (`status_changed_at`),
  CONSTRAINT `fk_hics_status_changed_by` FOREIGN KEY (`status_changed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `hourly_individual_class_students_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `hourly_individual_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_individual_class_students_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_individual_class_students`
--

LOCK TABLES `hourly_individual_class_students` WRITE;
/*!40000 ALTER TABLE `hourly_individual_class_students` DISABLE KEYS */;
INSERT INTO `hourly_individual_class_students` VALUES (1,1,4,'2026-01-04 09:42:30',1,NULL,NULL,NULL),(2,1,6,'2026-01-04 09:42:30',0,'แอดมินนำน้องเข้าผิดคลาส',1,'2026-01-06 09:44:26'),(3,2,6,'2026-01-04 09:42:48',1,NULL,NULL,NULL),(4,1,3,'2026-01-06 10:20:48',0,'อื่น ๆ (แจ้งหัวหน้า)',1,'2026-01-06 11:34:54'),(5,3,4,'2026-01-06 10:23:34',1,NULL,NULL,NULL),(6,3,6,'2026-01-06 10:23:34',0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:23:59'),(7,3,3,'2026-01-06 10:24:01',1,NULL,NULL,NULL),(8,4,4,'2026-01-06 10:26:20',0,'อื่น ๆ (แจ้งหัวหน้า)',1,'2026-01-06 10:32:41'),(9,4,6,'2026-01-06 10:26:20',0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:27:58'),(16,4,3,'2026-01-06 10:31:46',1,NULL,NULL,NULL),(17,5,4,'2026-01-06 10:46:25',0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:46:45'),(18,5,6,'2026-01-06 10:46:25',1,NULL,NULL,NULL),(19,6,4,'2026-01-06 10:46:53',1,NULL,NULL,NULL),(20,6,6,'2026-01-06 10:46:53',0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:52:26'),(21,6,3,'2026-01-06 10:52:30',1,NULL,NULL,NULL),(25,7,4,'2026-01-06 11:49:48',1,NULL,NULL,NULL),(26,7,6,'2026-01-06 11:49:48',1,'น้องกลับมาเรียน',1,'2026-01-06 12:10:26'),(30,8,4,'2026-01-06 12:04:54',1,NULL,NULL,NULL),(31,8,6,'2026-01-06 12:04:54',1,NULL,NULL,NULL),(33,5,3,'2026-01-06 12:13:23',1,NULL,NULL,NULL),(35,9,25,'2026-01-08 00:14:10',1,NULL,NULL,NULL),(36,10,25,'2026-01-08 00:14:16',1,NULL,NULL,NULL),(37,11,25,'2026-01-08 00:14:22',1,NULL,NULL,NULL),(38,12,25,'2026-01-08 00:14:31',1,NULL,NULL,NULL),(39,13,26,'2026-01-08 00:14:47',1,NULL,NULL,NULL),(40,14,27,'2026-01-10 23:20:27',1,NULL,NULL,NULL),(41,14,2,'2026-01-10 23:20:27',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `hourly_individual_class_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_individual_class_tutors`
--

DROP TABLE IF EXISTS `hourly_individual_class_tutors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_individual_class_tutors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `tutor_id` bigint NOT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_tutor` (`class_id`,`tutor_id`),
  KEY `assigned_by` (`assigned_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_tutor` (`tutor_id`),
  CONSTRAINT `hourly_individual_class_tutors_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `hourly_individual_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_individual_class_tutors_ibfk_2` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_individual_class_tutors_ibfk_3` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_individual_class_tutors`
--

LOCK TABLES `hourly_individual_class_tutors` WRITE;
/*!40000 ALTER TABLE `hourly_individual_class_tutors` DISABLE KEYS */;
INSERT INTO `hourly_individual_class_tutors` VALUES (1,1,3,'2026-01-04 09:42:39',NULL),(2,2,3,'2026-01-04 09:43:05',NULL),(3,9,4,'2026-01-08 00:15:52',NULL),(4,10,8,'2026-01-08 00:15:59',NULL),(5,11,9,'2026-01-08 00:16:04',NULL),(6,12,3,'2026-01-08 00:16:08',NULL),(7,13,5,'2026-01-08 00:16:15',NULL),(8,5,10,'2026-02-07 11:05:04',NULL),(9,1,9,'2026-02-07 11:09:58',NULL);
/*!40000 ALTER TABLE `hourly_individual_class_tutors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hourly_individual_classes`
--

DROP TABLE IF EXISTS `hourly_individual_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hourly_individual_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_id` bigint NOT NULL,
  `grade_id` bigint DEFAULT NULL,
  `student_id` bigint DEFAULT NULL,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `note` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_grade` (`grade_id`),
  KEY `idx_student_subject` (`student_id`,`subject_id`),
  CONSTRAINT `hourly_individual_classes_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `hourly_individual_classes_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hourly_individual_classes_ibfk_3` FOREIGN KEY (`grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `hourly_individual_classes_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hourly_individual_classes`
--

LOCK TABLES `hourly_individual_classes` WRITE;
/*!40000 ALTER TABLE `hourly_individual_classes` DISABLE KEYS */;
INSERT INTO `hourly_individual_classes` VALUES (1,1,NULL,NULL,'PV-กลุ่ม - d, f (คณิตศาสตร์)',1,NULL,'2026-01-04 09:42:30','2026-01-04 09:42:30',1),(2,1,NULL,NULL,'PV-เดี่ยว - f (คณิตศาสตร์)',1,NULL,'2026-01-04 09:42:48','2026-01-04 09:42:48',1),(3,1,NULL,NULL,'PV-กลุ่ม - d, f (คณิตศาสตร์)',1,NULL,'2026-01-06 10:23:34','2026-01-06 10:23:34',1),(4,1,NULL,NULL,'PV-กลุ่ม - d, f (คณิตศาสตร์)',1,NULL,'2026-01-06 10:26:20','2026-01-06 10:26:20',1),(5,1,NULL,NULL,'PV-กลุ่ม - d, f (คณิตศาสตร์)',1,NULL,'2026-01-06 10:46:25','2026-01-06 10:46:25',1),(6,1,NULL,NULL,'PV-กลุ่ม - d, f (คณิตศาสตร์)',1,NULL,'2026-01-06 10:46:53','2026-01-06 10:46:53',1),(7,2,NULL,NULL,'PV-กลุ่ม - d, f (ฟิสิกส์)',1,NULL,'2026-01-06 11:49:48','2026-01-06 11:49:48',1),(8,2,NULL,NULL,'PV-กลุ่ม - d, f (ฟิสิกส์)',1,NULL,'2026-01-06 12:04:54','2026-01-06 12:04:54',1),(9,1,NULL,NULL,'PV-เดี่ยว - ดีเซ็ม (คณิตศาสตร์)',1,NULL,'2026-01-08 00:14:10','2026-01-08 00:14:10',1),(10,3,NULL,NULL,'PV-เดี่ยว - ดีเซ็ม (เคมี)',1,NULL,'2026-01-08 00:14:16','2026-01-08 00:14:16',1),(11,4,NULL,NULL,'PV-เดี่ยว - ดีเซ็ม (ชีววิทยา)',1,NULL,'2026-01-08 00:14:22','2026-01-08 00:14:22',1),(12,2,NULL,NULL,'PV-เดี่ยว - ดีเซ็ม (ฟิสิกส์)',1,NULL,'2026-01-08 00:14:31','2026-01-08 00:14:31',1),(13,1,NULL,NULL,'PV-เดี่ยว - พลอย (คณิตศาสตร์)',1,NULL,'2026-01-08 00:14:47','2026-01-08 00:14:47',1),(14,1,NULL,NULL,'PV-กลุ่ม - aaa, b (คณิตศาสตร์)',1,NULL,'2026-01-10 23:20:27','2026-01-10 23:20:27',1);
/*!40000 ALTER TABLE `hourly_individual_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `math_form_history`
--

DROP TABLE IF EXISTS `math_form_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `math_form_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `math_form_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_math_form` (`math_form_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  CONSTRAINT `math_form_history_ibfk_1` FOREIGN KEY (`math_form_id`) REFERENCES `math_forms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `math_form_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `math_form_history`
--

LOCK TABLES `math_form_history` WRITE;
/*!40000 ALTER TABLE `math_form_history` DISABLE KEYS */;
INSERT INTO `math_form_history` VALUES (1,5,'UPDATE','studentsPresent','1','2',3,'2026-01-18 02:54:23'),(2,3,'UPDATE','content','จำนวนเชิงซ้อน','จำนวนเชิงซ้อนoeee',3,'2026-01-18 03:03:02'),(3,6,'UPDATE','studentsPresent','5','54',3,'2026-02-07 10:30:44'),(4,5,'UPDATE','studentsAbsent','0','5',3,'2026-02-07 10:31:21'),(5,6,'UPDATE','studentsPresent','54','543',3,'2026-02-07 10:50:00');
/*!40000 ALTER TABLE `math_form_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `math_forms`
--

DROP TABLE IF EXISTS `math_forms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `math_forms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tutor_id` bigint NOT NULL,
  `class_id` bigint NOT NULL COMMENT 'ต้องเป็น monthly_classes เท่านั้น',
  `subject_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `teaching_date` date NOT NULL,
  `hours_taught` decimal(7,2) NOT NULL,
  `students_present` int NOT NULL DEFAULT '0',
  `students_absent` int NOT NULL DEFAULT '0',
  `remark` text COLLATE utf8mb4_unicode_ci,
  `is_admin_created` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subject_id` (`subject_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_tutor` (`tutor_id`),
  KEY `idx_class` (`class_id`),
  KEY `idx_teaching_date` (`teaching_date`),
  KEY `idx_tutor_date` (`tutor_id`,`teaching_date`),
  CONSTRAINT `math_forms_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `math_forms_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `monthly_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `math_forms_ibfk_3` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `math_forms_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `chk_math_hours_taught_positive` CHECK ((`hours_taught` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `math_forms`
--

LOCK TABLES `math_forms` WRITE;
/*!40000 ALTER TABLE `math_forms` DISABLE KEYS */;
INSERT INTO `math_forms` VALUES (1,3,1,1,'เซต','2026-01-18',2.00,5,1,'',0,'2026-01-18 00:59:30','2026-01-18 00:59:30',3),(2,3,1,1,'เลขชี้กำลัง','2026-01-18',4.00,2,0,'',0,'2026-01-18 01:03:13','2026-01-18 01:03:13',3),(3,3,1,1,'จำนวนเชิงซ้อนoeee','2026-01-19',2.00,6,1,'',0,'2026-01-18 01:50:29','2026-01-18 03:03:02',3),(4,3,1,1,'ddddd','2026-01-20',2.00,1,0,'',0,'2026-01-18 01:57:47','2026-01-18 01:57:47',3),(5,3,1,1,'aaaa','2026-01-18',5.00,2,5,'',0,'2026-01-18 02:00:40','2026-02-07 10:31:21',3),(6,3,1,1,'dasdfasd','2026-01-30',4.00,543,0,'',0,'2026-01-30 02:57:51','2026-02-07 10:50:00',3);
/*!40000 ALTER TABLE `math_forms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_class_tutors`
--

DROP TABLE IF EXISTS `monthly_class_tutors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_class_tutors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `tutor_id` bigint NOT NULL,
  `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `assigned_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_tutor` (`class_id`,`tutor_id`),
  KEY `assigned_by` (`assigned_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_tutor` (`tutor_id`),
  CONSTRAINT `monthly_class_tutors_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `monthly_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `monthly_class_tutors_ibfk_2` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `monthly_class_tutors_ibfk_3` FOREIGN KEY (`assigned_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_class_tutors`
--

LOCK TABLES `monthly_class_tutors` WRITE;
/*!40000 ALTER TABLE `monthly_class_tutors` DISABLE KEYS */;
INSERT INTO `monthly_class_tutors` VALUES (1,1,10,'2026-01-10 23:15:50',1),(2,1,3,'2026-01-18 00:59:17',1),(3,2,3,'2026-01-18 02:01:30',1),(4,3,10,'2026-01-18 08:15:54',1);
/*!40000 ALTER TABLE `monthly_class_tutors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_classes`
--

DROP TABLE IF EXISTS `monthly_classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_id` bigint NOT NULL,
  `grade_id` bigint DEFAULT NULL,
  `subtype_id` bigint NOT NULL,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `note` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_subtype` (`subtype_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_dates` (`start_date`,`end_date`),
  KEY `idx_grade` (`grade_id`),
  CONSTRAINT `monthly_classes_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `monthly_classes_ibfk_2` FOREIGN KEY (`subtype_id`) REFERENCES `monthly_subtypes` (`id`),
  CONSTRAINT `monthly_classes_ibfk_3` FOREIGN KEY (`grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `monthly_classes_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_classes`
--

LOCK TABLES `monthly_classes` WRITE;
/*!40000 ALTER TABLE `monthly_classes` DISABLE KEYS */;
INSERT INTO `monthly_classes` VALUES (1,1,4,1,'คณิตศาสตร์ ม.4 - จันทร์-ศุกร์',NULL,NULL,1,NULL,'2026-01-04 08:12:57','2026-01-04 08:12:57',1),(2,3,4,4,'เคมี ม.4 - อาทิตย์',NULL,NULL,1,NULL,'2026-01-06 09:40:05','2026-01-06 09:40:05',1),(3,6,4,3,'TPAT1 ม.4 - เสาร์',NULL,NULL,1,NULL,'2026-01-06 09:40:48','2026-01-06 09:40:48',1),(4,5,4,1,'ภาษาต่างประเทศ ม.4 - จันทร์-ศุกร์',NULL,NULL,1,NULL,'2026-01-07 08:36:56','2026-01-07 08:36:56',1),(5,5,5,2,'ภาษาต่างประเทศ ม.5 - อังคาร-พฤหัส',NULL,NULL,1,NULL,'2026-01-07 08:41:41','2026-01-07 08:41:41',1),(6,5,5,1,'ภาษาต่างประเทศ ม.5 - จันทร์-ศุกร์',NULL,NULL,1,NULL,'2026-01-07 08:43:09','2026-01-07 08:43:09',1),(7,4,4,2,'ชีววิทยา ม.4 - อังคาร-พฤหัส',NULL,NULL,1,NULL,'2026-01-07 08:56:42','2026-01-07 08:56:42',1),(8,4,5,1,'ชีววิทยา ม.5 - จันทร์-ศุกร์',NULL,NULL,1,NULL,'2026-01-07 08:57:06','2026-01-07 08:57:06',1),(9,4,5,2,'ชีววิทยา ม.5 - อังคาร-พฤหัส',NULL,NULL,1,NULL,'2026-01-07 08:57:17','2026-01-07 08:57:17',1),(10,4,6,2,'ชีววิทยา ม.6 - อังคาร-พฤหัส',NULL,NULL,1,NULL,'2026-01-07 09:00:11','2026-01-07 09:00:11',1),(11,1,7,3,'คณิตศาสตร์ อื่น ๆ - เสาร์',NULL,NULL,1,NULL,'2026-01-07 09:26:05','2026-01-07 09:26:05',1),(12,1,5,4,'คณิตศาสตร์ ม.5 - อาทิตย์',NULL,NULL,1,NULL,'2026-01-10 23:17:38','2026-01-10 23:17:38',1);
/*!40000 ALTER TABLE `monthly_classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_enrollments`
--

DROP TABLE IF EXISTS `monthly_enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `enrolled_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `enrolled_grade_id` bigint DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `status_change_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลการ active/inactive',
  `status_changed_by` bigint DEFAULT NULL COMMENT 'ใครเป็นคนเปลี่ยน status',
  `status_changed_at` timestamp NULL DEFAULT NULL COMMENT 'เวลาที่เปลี่ยน status',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`,`student_id`),
  KEY `enrolled_grade_id` (`enrolled_grade_id`),
  KEY `fk_me_status_changed_by` (`status_changed_by`),
  KEY `idx_class` (`class_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_status_changed_at` (`status_changed_at`),
  CONSTRAINT `fk_me_status_changed_by` FOREIGN KEY (`status_changed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  CONSTRAINT `monthly_enrollments_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `monthly_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `monthly_enrollments_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `monthly_enrollments_ibfk_3` FOREIGN KEY (`enrolled_grade_id`) REFERENCES `grades` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_enrollments`
--

LOCK TABLES `monthly_enrollments` WRITE;
/*!40000 ALTER TABLE `monthly_enrollments` DISABLE KEYS */;
INSERT INTO `monthly_enrollments` VALUES (1,1,1,'2026-01-04 08:13:04',4,1,'น้องกลับมาเรียน',1,'2026-01-07 10:10:20'),(2,1,2,'2026-01-04 08:13:05',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 07:48:18'),(3,1,3,'2026-01-04 08:13:05',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 09:58:32'),(4,1,4,'2026-01-04 08:13:06',4,1,NULL,NULL,NULL),(5,1,5,'2026-01-04 08:13:06',4,1,NULL,NULL,NULL),(6,1,6,'2026-01-04 08:13:07',4,1,NULL,NULL,NULL),(7,1,7,'2026-01-06 09:11:11',3,1,NULL,NULL,NULL),(8,1,8,'2026-01-06 16:16:06',NULL,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 09:59:20'),(9,1,10,'2026-01-06 09:34:06',4,1,NULL,NULL,NULL),(10,2,11,'2026-01-06 09:40:10',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 09:59:32'),(11,3,12,'2026-01-06 09:40:55',5,1,'น้องกลับมาเรียน',1,'2026-01-06 10:11:25'),(12,2,1,'2026-01-06 09:50:25',4,0,'น้องขอจบคอร์ส',1,'2026-01-06 11:58:31'),(13,2,2,'2026-01-06 09:50:26',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:17:18'),(14,2,3,'2026-01-06 09:50:26',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:17:15'),(15,3,4,'2026-01-06 09:58:50',4,1,NULL,NULL,NULL),(16,3,5,'2026-01-06 09:58:52',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-06 10:07:05'),(17,3,3,'2026-01-06 09:58:53',4,1,NULL,NULL,NULL),(18,3,2,'2026-01-06 09:58:53',4,1,'น้องกลับมาเรียน',1,'2026-01-06 22:54:23'),(19,3,1,'2026-01-06 09:58:53',4,0,'น้องขอจบคอร์ส',1,'2026-01-06 11:58:23'),(20,3,6,'2026-01-06 09:58:54',4,1,NULL,NULL,NULL),(21,3,9,'2026-01-06 09:58:55',4,1,NULL,NULL,NULL),(22,3,10,'2026-01-06 09:58:55',4,1,NULL,NULL,NULL),(23,3,11,'2026-01-06 09:58:55',4,1,NULL,NULL,NULL),(24,3,8,'2026-01-06 09:58:59',1,1,NULL,NULL,NULL),(25,1,12,'2026-01-06 10:57:31',5,1,NULL,NULL,NULL),(26,3,7,'2026-01-06 11:13:38',3,1,NULL,NULL,NULL),(27,1,9,'2026-01-06 23:14:10',4,1,NULL,NULL,NULL),(28,4,15,'2026-01-07 08:37:00',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:43:52'),(29,4,16,'2026-01-07 08:37:02',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:43:52'),(30,4,18,'2026-01-07 08:37:05',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:43:52'),(31,4,17,'2026-01-07 08:37:13',5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:43:52'),(32,7,19,'2026-01-07 08:56:47',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-10 23:23:23'),(33,7,20,'2026-01-07 08:56:48',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:58:16'),(34,7,21,'2026-01-07 08:56:50',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 08:58:16'),(35,9,20,'2026-01-07 08:58:16',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:00:20'),(36,9,19,'2026-01-07 08:58:16',4,1,'น้องขอหยุดเรียน',1,'2026-01-10 23:23:23'),(37,9,21,'2026-01-07 08:58:16',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:00:20'),(38,9,22,'2026-01-07 08:59:30',5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:00:20'),(39,10,22,'2026-01-07 09:00:20',5,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:28:39'),(40,10,20,'2026-01-07 09:00:20',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:28:39'),(41,10,21,'2026-01-07 09:00:20',4,0,'น้องเลื่อนระดับชั้น',1,'2026-01-07 09:28:39'),(42,12,19,'2026-01-10 23:18:02',5,1,NULL,NULL,NULL),(43,12,15,'2026-01-10 23:18:03',5,1,NULL,NULL,NULL),(44,12,1,'2026-01-10 23:18:03',5,1,NULL,NULL,NULL),(45,12,2,'2026-01-10 23:18:04',5,0,'น้องขอหยุดเรียน',1,'2026-02-07 10:56:35');
/*!40000 ALTER TABLE `monthly_enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_subtypes`
--

DROP TABLE IF EXISTS `monthly_subtypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_subtypes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subtype_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `subtype_name` (`subtype_name`),
  KEY `idx_active` (`is_active`),
  KEY `idx_display_order` (`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_subtypes`
--

LOCK TABLES `monthly_subtypes` WRITE;
/*!40000 ALTER TABLE `monthly_subtypes` DISABLE KEYS */;
INSERT INTO `monthly_subtypes` VALUES (1,'จันทร์-ศุกร์',1,1,'2026-01-04 15:06:15'),(2,'อังคาร-พฤหัส',1,2,'2026-01-04 15:06:15'),(3,'เสาร์',1,3,'2026-01-04 15:06:15'),(4,'อาทิตย์',1,4,'2026-01-04 15:06:15');
/*!40000 ALTER TABLE `monthly_subtypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification_history`
--

DROP TABLE IF EXISTS `notification_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notification_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ชื่อ field ที่แก้ไข (NULL ถ้าเป็น INSERT/DELETE)',
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL COMMENT 'user ที่ทำการแก้ไข',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_notification` (`notification_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  KEY `idx_field_name` (`field_name`),
  CONSTRAINT `notification_history_ibfk_1` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`) ON DELETE CASCADE,
  CONSTRAINT `notification_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification_history`
--

LOCK TABLES `notification_history` WRITE;
/*!40000 ALTER TABLE `notification_history` DISABLE KEYS */;
INSERT INTO `notification_history` VALUES (1,3,'UPDATE','receipt_code',NULL,'123456',1,'2026-01-04 21:52:07'),(2,4,'UPDATE','receipt_code',NULL,'123456',1,'2026-01-04 21:54:35'),(3,5,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-04 21:55:43'),(4,6,'UPDATE','receipt_code',NULL,'123457',1,'2026-01-04 22:01:59'),(5,7,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-04 22:03:13'),(6,8,'UPDATE','receipt_code',NULL,'123789',1,'2026-01-04 22:03:17'),(7,9,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-04 22:15:09'),(8,9,'UPDATE','receipt_code','000000','015782',1,'2026-01-04 22:24:14'),(9,1,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-04 22:32:00'),(10,1,'UPDATE','receipt_code','000000','579856',1,'2026-01-04 22:32:10'),(11,2,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-04 22:32:47'),(12,2,'UPDATE','receipt_code','000000','000004',1,'2026-01-04 22:32:53'),(13,10,'UPDATE','receipt_code',NULL,'000545',1,'2026-01-04 22:35:08'),(14,10,'UPDATE','receipt_code','000545','000546',1,'2026-01-04 22:35:34'),(15,2,'UPDATE','receipt_code','000004','000578',1,'2026-01-05 08:30:41'),(16,3,'UPDATE','receipt_code','123456','123488',1,'2026-01-05 08:31:11'),(17,9,'UPDATE','receipt_code','015782','015784',1,'2026-01-05 09:43:43'),(18,9,'UPDATE','receipt_code','015784','111111',1,'2026-01-05 09:47:19'),(19,23,'UPDATE','receipt_code',NULL,'145786',1,'2026-01-05 09:49:09'),(20,23,'UPDATE','receipt_code','145786','145787',1,'2026-01-05 09:49:20'),(21,2,'UPDATE','receipt_code','000578','000579',1,'2026-01-05 09:49:43'),(22,3,'UPDATE','receipt_code','123488','123489',1,'2026-01-06 08:51:33'),(23,15,'UPDATE','receipt_code',NULL,'145789',1,'2026-01-06 08:51:46'),(24,25,'UPDATE','receipt_code',NULL,'123789',1,'2026-01-06 09:20:21'),(25,25,'UPDATE','receipt_code','123789','123780',1,'2026-01-06 09:21:03'),(26,24,'UPDATE','receipt_code',NULL,'454615',1,'2026-01-06 09:21:14'),(27,24,'UPDATE','receipt_code','454615','454614',1,'2026-01-06 09:21:16'),(28,21,'UPDATE','receipt_code',NULL,'147258',1,'2026-01-06 09:30:53'),(29,21,'UPDATE','receipt_code','147258','147257',1,'2026-01-06 09:30:57'),(30,32,'UPDATE','receipt_code',NULL,'457981',1,'2026-01-07 00:04:10'),(31,32,'UPDATE','receipt_code','457981','457982',1,'2026-01-07 00:04:25'),(32,37,'UPDATE','receipt_code',NULL,'123456',1,'2026-01-10 23:27:04'),(33,36,'UPDATE','receipt_code',NULL,'000000',1,'2026-01-10 23:27:47'),(34,37,'UPDATE','receipt_code','123456','123458',1,'2026-01-10 23:28:13');
/*!40000 ALTER TABLE `notification_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notification_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HOURS_MILESTONE, GRADE_MISMATCH, SYSTEM',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `class_id` bigint DEFAULT NULL,
  `student_id` bigint DEFAULT NULL,
  `subject_id` bigint DEFAULT NULL,
  `hours_completed` decimal(7,2) DEFAULT NULL,
  `hours_target` decimal(7,2) DEFAULT NULL,
  `milestone_reached` int DEFAULT NULL COMMENT 'ครั้งที่ครบ (1, 2, 3...)',
  `receipt_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'รหัสใบเสร็จที่ admin กรอกตอนอ่าน notification',
  `is_read` tinyint(1) DEFAULT '0',
  `read_by` bigint DEFAULT NULL,
  `read_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `subject_id` (`subject_id`),
  KEY `read_by` (`read_by`),
  KEY `idx_unread` (`is_read`,`created_at`),
  KEY `idx_class` (`class_type`,`class_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_type` (`notification_type`),
  KEY `idx_created_at` (`created_at` DESC),
  KEY `idx_receipt_code` (`receipt_code`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `notifications_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE SET NULL,
  CONSTRAINT `notifications_ibfk_3` FOREIGN KEY (`read_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'HOURS_MILESTONE','? b เรียนครบชั่วโมงแล้ว!','b เรียนวิชาคณิตศาสตร์ แบบกลุ่มรวม ครบ 18.0 ชั่วโมงตามที่ซื้อมาแล้ว (เป้าหมาย 18.0 ชม.)','hourly_group',1,2,1,NULL,NULL,1,'579856',1,1,'2026-01-04 21:47:21','2026-01-04 08:24:34'),(2,'HOURS_MILESTONE','? b เรียนครบชั่วโมงครั้งที่ 2!','b เรียนวิชาคณิตศาสตร์ แบบกลุ่มรวม ไปแล้ว 36.0 ชั่วโมง (ครบเป้าหมายครั้งที่ 2)','hourly_group',1,2,1,NULL,NULL,2,'000579',1,1,'2026-01-04 21:47:21','2026-01-04 20:43:18'),(3,'HOURS_MILESTONE','? d เรียนครบชั่วโมงแล้ว!','d เรียนวิชาคณิตศาสตร์ แบบกลุ่มรวม ครบ 18.0 ชั่วโมงตามที่ซื้อมาแล้ว (เป้าหมาย 18.0 ชม.)','hourly_group',1,4,1,NULL,NULL,1,'123489',1,1,'2026-01-04 21:52:07','2026-01-04 21:47:44'),(4,'HOURS_MILESTONE','? d เรียนครบชั่วโมงแล้ว!','d เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ครบ 12.0 ชั่วโมงตามที่ซื้อมาแล้ว (เป้าหมาย 12.0 ชม.)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,1,'123456',1,1,'2026-01-04 21:54:35','2026-01-04 21:54:19'),(5,'HOURS_MILESTONE','? f เรียนครบชั่วโมงแล้ว!','f เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ครบ 12.0 ชั่วโมงตามที่ซื้อมาแล้ว (เป้าหมาย 12.0 ชม.)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,1,'000000',1,1,'2026-01-04 21:55:43','2026-01-04 21:54:19'),(6,'HOURS_MILESTONE','? f เรียนครบชั่วโมงแล้ว!','f เรียนวิชาคณิตศาสตร์ แบบเดี่ยว ครบ 12.0 ชั่วโมงตามที่ซื้อมาแล้ว (เป้าหมาย 12.0 ชม.)','hourly_individual',2,6,1,NULL,NULL,1,'123457',1,1,'2026-01-04 22:01:59','2026-01-04 22:01:48'),(7,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 2!','f เรียนวิชาคณิตศาสตร์ แบบเดี่ยว ไปแล้ว 24.0 ชั่วโมง (ครบเป้าหมายครั้งที่ 2)','hourly_individual',2,6,1,NULL,NULL,2,'000000',1,1,'2026-01-04 22:03:13','2026-01-04 22:02:10'),(8,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 2!','f เรียนวิชาคณิตศาสตร์ แบบเดี่ยว ไปแล้ว 24.0 ชั่วโมง (ครบเป้าหมายครั้งที่ 2)','hourly_individual',2,6,1,NULL,NULL,2,'123789',1,1,'2026-01-04 22:03:17','2026-01-04 22:03:03'),(9,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 2!','d เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 28.5 ชั่วโมง (ครบเป้าหมายครั้งที่ 2)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,2,'111111',1,1,'2026-01-04 22:15:09','2026-01-04 22:12:49'),(10,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 2!','f เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 24.5 ชั่วโมง (ครบเป้าหมายครั้งที่ 2)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,2,'000546',1,1,'2026-01-04 22:22:02','2026-01-04 22:12:49'),(11,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 3!','d เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 36.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 3)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,3,NULL,0,NULL,NULL,'2026-01-05 09:16:24'),(12,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 4!','d เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 48.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 4)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,4,NULL,0,NULL,NULL,'2026-01-05 09:17:28'),(13,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 3!','f เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 44.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 3)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,3,NULL,0,NULL,NULL,'2026-01-05 09:17:28'),(14,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 3!','f เรียนวิชาคณิตศาสตร์ แบบPV-เดี่ยว ไปแล้ว 36.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 3)','hourly_individual',2,6,1,NULL,NULL,3,NULL,0,NULL,NULL,'2026-01-05 09:26:06'),(15,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 4!','f เรียนวิชาคณิตศาสตร์ แบบPV-เดี่ยว ไปแล้ว 48.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 4)','hourly_individual',2,6,1,NULL,NULL,4,'145789',1,1,'2026-01-06 08:51:47','2026-01-05 09:27:16'),(16,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 4!','f เรียนวิชาคณิตศาสตร์ แบบINDIVIDUAL_GROUP ไปแล้ว 49.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 4)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,4,NULL,0,NULL,NULL,'2026-01-05 09:28:24'),(17,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 5!','d เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 65.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 5)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,5,NULL,0,NULL,NULL,'2026-01-05 09:34:21'),(18,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 5!','f เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 61.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 5)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,5,NULL,0,NULL,NULL,'2026-01-05 09:34:21'),(19,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 6!','d เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 77.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 6)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,6,NULL,0,NULL,NULL,'2026-01-05 09:36:47'),(20,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 6!','f เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 73.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 6)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,6,NULL,0,NULL,NULL,'2026-01-05 09:36:47'),(21,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 7!','d เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 89.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 7)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,7,'147257',1,1,'2026-01-06 09:30:53','2026-01-05 09:38:49'),(22,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 7!','f เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 85.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 7)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,7,NULL,0,NULL,NULL,'2026-01-05 09:38:49'),(23,'HOURS_MILESTONE','? d เรียนครบชั่วโมงครั้งที่ 8!','d เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 101.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 8)','INDIVIDUAL_GROUP',1,4,1,NULL,NULL,8,'145787',1,1,'2026-01-05 09:49:09','2026-01-05 09:46:53'),(24,'HOURS_MILESTONE','? f เรียนครบชั่วโมงครั้งที่ 8!','f เรียนวิชาคณิตศาสตร์ PV-กลุ่ม ไปแล้ว 97.5 ชั่วโมง (ครบชั่วโมงครั้งที่ 8)','INDIVIDUAL_GROUP',1,6,1,NULL,NULL,8,'454614',1,1,'2026-01-06 09:21:14','2026-01-05 09:46:53'),(25,'MONTHLY_EXPIRATION','? เทส หมดคอร์สรายเดือนแล้ว','เทส หมดคอร์สรายเดือนคณิตศาสตร์แล้ว','monthly',1,8,1,NULL,NULL,NULL,'123780',1,1,'2026-01-06 09:20:21','2026-01-06 09:17:00'),(26,'MONTHLY_EXPIRATION','? i หมดคอร์สรายเดือนแล้ว','i หมดคอร์สรายเดือนคณิตศาสตร์แล้ว','monthly',1,10,1,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 09:36:00'),(27,'MONTHLY_EXPIRATION','? เทส หมดคอร์สรายเดือนแล้ว','เทส หมดคอร์สรายเดือนTPAT1แล้ว','monthly',3,8,6,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 09:59:00'),(28,'MONTHLY_EXPIRATION','? h หมดคอร์สรายเดือนแล้ว','h หมดคอร์สรายเดือนTPAT1แล้ว','monthly',3,9,6,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 09:59:00'),(29,'MONTHLY_EXPIRATION','? i หมดคอร์สรายเดือนแล้ว','i หมดคอร์สรายเดือนTPAT1แล้ว','monthly',3,10,6,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 09:59:00'),(30,'MONTHLY_EXPIRATION','? j หมดคอร์สรายเดือนแล้ว','j หมดคอร์สรายเดือนTPAT1แล้ว','monthly',3,11,6,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 10:10:00'),(31,'MONTHLY_EXPIRATION','? k หมดคอร์สรายเดือนแล้ว','k หมดคอร์สรายเดือนTPAT1แล้ว','monthly',3,12,6,NULL,NULL,NULL,NULL,0,NULL,NULL,'2026-01-06 10:12:00'),(32,'HOURS_MILESTONE','? b เรียนครบชั่วโมงครั้งที่ 3!','b เรียนวิชาคณิตศาสตร์ กลุ่มรวม ไปแล้ว 62.0 ชั่วโมง (ครบชั่วโมงครั้งที่ 3)','hourly_group',1,2,1,NULL,NULL,3,'457982',1,1,'2026-01-07 00:04:10','2026-01-06 12:13:45'),(33,'HOURS_MILESTONE','? ccc เรียนครบชั่วโมงแล้ว!','ccc เรียนวิชาTPAT1 กลุ่มรวม ครบ 18.0 ชั่วโมงแล้ว (เป้าหมาย 18.0 ชม.)','hourly_group',2,17,6,NULL,NULL,1,NULL,0,NULL,NULL,'2026-01-07 09:10:05'),(34,'HOURS_MILESTONE','? dddd เรียนครบชั่วโมงแล้ว!','dddd เรียนวิชาTPAT1 กลุ่มรวม ครบ 18.0 ชั่วโมงแล้ว (เป้าหมาย 18.0 ชม.)','hourly_group',2,22,6,NULL,NULL,1,NULL,0,NULL,NULL,'2026-01-07 09:10:05'),(35,'HOURS_MILESTONE','? dddd เรียนครบชั่วโมงครั้งที่ 2!','dddd เรียนวิชาTPAT1 กลุ่มรวม ไปแล้ว 36.0 ชั่วโมง (ครบชั่วโมงครั้งที่ 2)','hourly_group',4,22,6,NULL,NULL,2,NULL,0,NULL,NULL,'2026-01-07 09:14:26'),(36,'HOURS_MILESTONE','? ccc เรียนครบชั่วโมงครั้งที่ 2!','ccc เรียนวิชาTPAT1 กลุ่มรวม ไปแล้ว 36.0 ชั่วโมง (ครบชั่วโมงครั้งที่ 2)','hourly_group',4,17,6,NULL,NULL,2,'000000',1,1,'2026-01-10 23:27:47','2026-01-07 09:14:26'),(37,'HOURS_MILESTONE','? i เรียนครบชั่วโมงแล้ว!','i เรียนวิชาTPAT1 กลุ่มรวม ครบ 18.0 ชั่วโมงแล้ว (เป้าหมาย 18.0 ชม.)','hourly_group',4,10,6,NULL,NULL,1,'123458',1,1,'2026-01-10 23:27:04','2026-01-07 09:36:20'),(38,'HOURS_MILESTONE','? ccc เรียนครบชั่วโมงครั้งที่ 3!','ccc เรียนวิชา TPAT1 กลุ่มรวม ไปแล้ว 54.0 ชั่วโมง (ครบชั่วโมงครั้งที่ 3)','hourly_group',2,17,6,NULL,NULL,3,NULL,0,NULL,NULL,'2026-02-07 11:12:50');
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipt_history`
--

DROP TABLE IF EXISTS `receipt_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipt_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `receipt_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_receipt` (`receipt_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `receipt_history_ibfk_1` FOREIGN KEY (`receipt_id`) REFERENCES `receipts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `receipt_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipt_history`
--

LOCK TABLES `receipt_history` WRITE;
/*!40000 ALTER TABLE `receipt_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `receipt_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipts`
--

DROP TABLE IF EXISTS `receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint DEFAULT NULL COMMENT 'นักเรียนที่จ่ายเงิน (optional)',
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'hourly_group, hourly_individual, monthly',
  `class_id` bigint DEFAULT NULL COMMENT 'ID ของคลาส (optional)',
  `amount` decimal(10,2) NOT NULL,
  `payment_date` date NOT NULL,
  `image_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT 'pending, paid, cancelled',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `idx_student` (`student_id`),
  KEY `idx_class` (`class_type`,`class_id`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_date` (`payment_date`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `receipts_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE SET NULL,
  CONSTRAINT `receipts_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipts`
--

LOCK TABLES `receipts` WRITE;
/*!40000 ALTER TABLE `receipts` DISABLE KEYS */;
/*!40000 ALTER TABLE `receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `science_form_history`
--

DROP TABLE IF EXISTS `science_form_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `science_form_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `science_form_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_science_form` (`science_form_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  CONSTRAINT `science_form_history_ibfk_1` FOREIGN KEY (`science_form_id`) REFERENCES `science_forms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `science_form_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `science_form_history`
--

LOCK TABLES `science_form_history` WRITE;
/*!40000 ALTER TABLE `science_form_history` DISABLE KEYS */;
INSERT INTO `science_form_history` VALUES (1,1,'UPDATE','content','เคมีอินทรีย์นะจ๊ะ','เคมีอินทรีย์นะจ๊ะrrrr',3,'2026-01-18 02:49:57'),(2,1,'UPDATE','hoursTaught','4.00','4',3,'2026-01-18 02:49:57'),(3,1,'UPDATE','remark','อิอิ','อิอิกกก',3,'2026-01-18 02:54:32'),(4,2,'UPDATE','hoursTaught','2.00','3',3,'2026-01-18 08:13:24'),(5,3,'UPDATE','studentsPresent','1','5',10,'2026-01-18 08:14:24'),(6,3,'UPDATE','remark','','กกก',10,'2026-01-18 08:14:24'),(7,2,'UPDATE','studentsPresent','1','4',3,'2026-01-18 08:28:57'),(8,2,'UPDATE','hoursTaught','3.00','5',3,'2026-01-18 08:29:25'),(9,2,'UPDATE','content','หหหหหหห','หหหหหหห กดกดกดกดดก',3,'2026-02-07 10:33:55');
/*!40000 ALTER TABLE `science_form_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `science_forms`
--

DROP TABLE IF EXISTS `science_forms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `science_forms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tutor_id` bigint NOT NULL,
  `class_id` bigint NOT NULL COMMENT 'ต้องเป็น monthly_classes เท่านั้น',
  `subject_id` bigint NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `teaching_date` date NOT NULL,
  `hours_taught` decimal(7,2) NOT NULL,
  `students_present` int NOT NULL DEFAULT '0',
  `students_absent` int NOT NULL DEFAULT '0',
  `remark` text COLLATE utf8mb4_unicode_ci,
  `is_admin_created` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `subject_id` (`subject_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_tutor` (`tutor_id`),
  KEY `idx_class` (`class_id`),
  KEY `idx_teaching_date` (`teaching_date`),
  KEY `idx_tutor_date` (`tutor_id`,`teaching_date`),
  CONSTRAINT `science_forms_ibfk_1` FOREIGN KEY (`tutor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `science_forms_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `monthly_classes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `science_forms_ibfk_3` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `science_forms_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `chk_science_hours_taught_positive` CHECK ((`hours_taught` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `science_forms`
--

LOCK TABLES `science_forms` WRITE;
/*!40000 ALTER TABLE `science_forms` DISABLE KEYS */;
INSERT INTO `science_forms` VALUES (1,3,2,3,'เคมีอินทรีย์นะจ๊ะrrrr','2026-01-18',4.00,6,2,'อิอิกกก',0,'2026-01-18 02:01:43','2026-01-18 02:54:32',3),(2,3,2,3,'หหหหหหห กดกดกดกดดก','2026-01-18',5.00,4,0,'',0,'2026-01-18 08:12:59','2026-02-07 10:33:55',3),(3,10,1,1,'ดดดดดด','2026-01-18',4.00,5,0,'กกก',0,'2026-01-18 08:13:49','2026-01-18 08:14:24',10);
/*!40000 ALTER TABLE `science_forms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_course_purchases`
--

DROP TABLE IF EXISTS `student_course_purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_course_purchases` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `class_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'hourly_group, hourly_individual, hourly_individual_group, monthly, NULL=ซื้อแบบไม่ระบุคลาส',
  `class_id` bigint DEFAULT NULL,
  `monthly_subtype_id` bigint DEFAULT NULL COMMENT 'สำหรับคลาสรายเดือน (nullable)',
  `hours_purchased` decimal(7,2) DEFAULT NULL COMMENT 'จำนวนชั่วโมงที่ซื้อ (NULL สำหรับคลาสรายเดือน)',
  `hours_completed` decimal(7,2) DEFAULT '0.00' COMMENT 'จำนวนชั่วโมงที่เรียนไปแล้ว',
  `purchase_date` date NOT NULL,
  `receipt_id` bigint DEFAULT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `deleted_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลในการลบคอร์ส (soft delete)',
  `edit_reason` text COLLATE utf8mb4_unicode_ci COMMENT 'เหตุผลในการแก้ไขคอร์ส',
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receipt_id` (`receipt_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_student` (`student_id`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_active` (`is_active`),
  KEY `idx_purchase_date` (`purchase_date`),
  KEY `idx_class_lookup` (`class_type`,`class_id`),
  KEY `idx_student_subject_active` (`student_id`,`subject_id`,`is_active`),
  KEY `idx_student_class` (`student_id`,`class_type`,`class_id`,`is_active`),
  KEY `idx_monthly_subtype` (`monthly_subtype_id`),
  CONSTRAINT `fk_course_monthly_subtype` FOREIGN KEY (`monthly_subtype_id`) REFERENCES `monthly_subtypes` (`id`) ON DELETE SET NULL,
  CONSTRAINT `student_course_purchases_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_course_purchases_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `student_course_purchases_ibfk_3` FOREIGN KEY (`receipt_id`) REFERENCES `receipts` (`id`) ON DELETE SET NULL,
  CONSTRAINT `student_course_purchases_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `chk_hours_not_exceed` CHECK ((`hours_completed` <= (`hours_purchased` * 100))),
  CONSTRAINT `chk_hours_valid` CHECK ((((`hours_purchased` is null) or (`hours_purchased` > 0)) and (`hours_completed` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_course_purchases`
--

LOCK TABLES `student_course_purchases` WRITE;
/*!40000 ALTER TABLE `student_course_purchases` DISABLE KEYS */;
INSERT INTO `student_course_purchases` VALUES (1,2,1,'hourly_group',NULL,NULL,18.00,64.00,'2026-01-04',NULL,NULL,NULL,'น้องขอจบคอร์ส',1,'2026-01-04 08:07:22','2026-01-10 23:12:01',1),(2,3,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:09:00','2026-01-04 08:09:00',1),(3,4,1,'individual_group',NULL,NULL,12.00,103.50,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:09:08','2026-01-06 12:13:38',1),(4,5,1,'monthly',NULL,1,NULL,0.00,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:09:16','2026-01-04 08:09:16',1),(5,6,1,'hourly_group',NULL,NULL,18.00,8.00,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:10:16','2026-01-04 08:21:50',1),(6,6,1,'hourly_individual',NULL,NULL,12.00,48.50,'2026-01-04',NULL,NULL,NULL,'น้องกลับมาเรียน',1,'2026-01-04 08:10:16','2026-01-05 09:27:16',1),(7,6,1,'individual_group',NULL,NULL,12.00,97.50,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:10:16','2026-01-05 09:46:53',1),(8,6,1,'monthly',NULL,1,NULL,0.00,'2026-01-04',NULL,NULL,NULL,NULL,1,'2026-01-04 08:10:16','2026-01-04 08:10:16',1),(9,4,1,'hourly_group',NULL,NULL,18.00,18.00,'2026-01-05',NULL,NULL,NULL,NULL,1,'2026-01-04 20:39:32','2026-01-04 21:47:44',1),(10,7,1,'monthly',NULL,1,NULL,0.00,'2026-01-06',NULL,NULL,NULL,NULL,1,'2026-01-06 09:11:00','2026-01-06 09:11:00',1),(11,9,1,'monthly',NULL,2,NULL,0.00,'2026-01-06',NULL,NULL,NULL,NULL,1,'2026-01-06 09:33:50','2026-01-06 09:33:50',1),(12,3,1,'individual_group',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-06 10:31:35','2026-01-06 10:31:35',1),(13,4,2,'individual_group',NULL,NULL,12.00,2.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-06 11:49:19','2026-01-07 07:20:55',1),(14,6,2,'individual_group',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-06 11:49:29','2026-01-06 11:49:29',1),(15,14,1,'hourly_group',NULL,NULL,18.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-06 22:44:52','2026-01-06 22:44:52',1),(16,16,3,'hourly_group',NULL,NULL,18.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:00','2026-01-07 08:36:00',1),(17,17,5,'monthly',NULL,3,NULL,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:10','2026-01-07 08:36:10',1),(18,18,1,'hourly_group',NULL,NULL,18.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:32','2026-01-07 08:36:32',1),(19,18,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:32','2026-01-07 08:36:32',1),(20,18,1,'individual_group',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:32','2026-01-07 08:36:32',1),(21,18,1,'monthly',NULL,1,NULL,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:36:32','2026-01-07 08:36:32',1),(22,20,2,'monthly',NULL,3,NULL,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:56:22','2026-01-07 08:56:22',1),(23,21,5,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:56:29','2026-01-07 08:56:29',1),(24,22,2,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 08:59:14','2026-01-07 08:59:14',1),(25,17,6,'hourly_group',NULL,NULL,18.00,54.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 09:03:41','2026-02-07 11:12:50',1),(26,22,6,'hourly_group',NULL,NULL,18.00,36.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 09:04:01','2026-01-07 09:14:26',1),(27,13,6,'hourly_group',NULL,NULL,18.00,2.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 09:05:56','2026-01-07 09:09:08',1),(28,23,6,'hourly_group',NULL,NULL,18.00,2.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 09:12:11','2026-01-07 09:13:13',1),(29,10,6,'hourly_group',NULL,NULL,18.00,20.00,'2026-01-07',NULL,NULL,NULL,NULL,1,'2026-01-07 09:32:12','2026-01-07 09:37:42',1),(30,24,1,'hourly_group',NULL,NULL,18.00,4.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-07 10:15:20','2026-01-07 11:21:16',1),(31,25,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-08 00:13:32','2026-01-08 00:13:32',1),(32,25,3,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-08 00:13:32','2026-01-08 00:13:32',1),(33,25,4,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-08 00:13:32','2026-01-08 00:13:32',1),(34,25,2,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-08 00:13:32','2026-01-08 00:13:32',1),(35,26,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-08',NULL,NULL,NULL,NULL,1,'2026-01-08 00:13:59','2026-01-08 00:13:59',1),(36,27,1,'hourly_group',NULL,NULL,18.00,0.00,'2026-01-11',NULL,NULL,NULL,NULL,1,'2026-01-10 23:01:50','2026-01-10 23:01:50',1),(37,27,2,'hourly_group',NULL,NULL,18.00,17.40,'2026-01-11',NULL,NULL,NULL,'แอดมินกรอกผิด',1,'2026-01-10 23:04:30','2026-01-18 08:27:46',1),(38,7,2,'hourly_group',NULL,NULL,18.00,11.00,'2026-01-11',NULL,NULL,NULL,NULL,1,'2026-01-10 23:19:31','2026-01-18 08:27:46',1),(39,28,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-30',NULL,NULL,NULL,NULL,1,'2026-01-30 03:25:00','2026-01-30 03:25:00',1),(40,29,1,'hourly_individual',NULL,NULL,12.00,0.00,'2026-01-30',NULL,NULL,NULL,NULL,1,'2026-01-30 03:25:14','2026-01-30 03:25:14',1);
/*!40000 ALTER TABLE `student_course_purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_history`
--

DROP TABLE IF EXISTS `student_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ชื่อ field ที่แก้ไข (NULL ถ้าเป็น INSERT/DELETE)',
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_student` (`student_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  KEY `idx_action` (`action`),
  CONSTRAINT `student_history_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_history`
--

LOCK TABLES `student_history` WRITE;
/*!40000 ALTER TABLE `student_history` DISABLE KEYS */;
INSERT INTO `student_history` VALUES (1,2,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-04 08:07:22'),(2,3,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-04 08:09:00'),(3,4,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-04 08:09:08'),(4,5,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ รายเดือน จันทร์-ศุกร์',1,'2026-01-04 08:09:16'),(5,6,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-04 08:10:16'),(6,6,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-04 08:10:16'),(7,6,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-04 08:10:16'),(8,6,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ รายเดือน จันทร์-ศุกร์',1,'2026-01-04 08:10:16'),(9,6,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง | เหตุผล: แอดมินกรอกผิด',1,'2026-01-04 08:11:13'),(10,6,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง | เหตุผล: แอดมินกรอกผิด','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง | เหตุผล: น้องกลับมาเรียน',1,'2026-01-04 08:11:25'),(11,1,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-04 08:13:21'),(12,2,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-04 08:13:41'),(13,2,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-04 08:20:35'),(14,2,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)',1,'2026-01-04 08:21:17'),(15,2,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-04 08:22:19'),(16,2,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: น้องขอจบคอร์ส',1,'2026-01-04 08:22:53'),(17,2,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)',1,'2026-01-04 08:24:01'),(18,6,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-04 08:25:07'),(19,6,'UPDATE','firstName','F','Fโโ',1,'2026-01-04 09:05:02'),(20,4,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-04 20:39:32'),(21,7,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ รายเดือน จันทร์-ศุกร์',1,'2026-01-06 09:11:00'),(22,9,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ รายเดือน อังคาร-พฤหัส',1,'2026-01-06 09:33:50'),(23,12,'DELETE','classRemoved','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)','',1,'2026-01-06 09:41:30'),(24,4,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-06 09:42:00'),(25,6,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 09:44:26'),(26,11,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 09:50:09'),(27,1,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:50:51'),(28,11,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 09:51:10'),(29,2,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:51:32'),(30,2,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:53:10'),(31,1,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:56:10'),(32,3,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:58:32'),(33,8,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 09:59:20'),(34,11,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 09:59:32'),(35,5,'DELETE','classRemoved','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)','',1,'2026-01-06 10:07:05'),(36,12,'UPDATE','classReactivated','','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)',1,'2026-01-06 10:11:25'),(37,3,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 10:17:15'),(38,2,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 10:17:18'),(39,6,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 10:23:59'),(40,6,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 10:27:58'),(41,3,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-06 10:31:35'),(42,4,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 10:32:41'),(43,4,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 10:46:45'),(44,6,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 10:52:26'),(45,3,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (คณิตศาสตร์) (คณิตศาสตร์)','',1,'2026-01-06 11:34:54'),(46,1,'UPDATE','classReactivated','','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)',1,'2026-01-06 11:39:30'),(47,1,'UPDATE','firstName','A','Afd',1,'2026-01-06 11:39:36'),(48,1,'UPDATE','nickname','a','agg',1,'2026-01-06 11:39:43'),(49,4,'INSERT','coursePurchaseAdded','','ฟิสิกส์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-06 11:49:19'),(50,6,'INSERT','coursePurchaseAdded','','ฟิสิกส์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-06 11:49:29'),(51,6,'DELETE','classRemoved','คลาสรายชั่วโมง (เดี่ยว) - PV-กลุ่ม - d, f (ฟิสิกส์) (ฟิสิกส์)','',1,'2026-01-06 11:50:24'),(52,1,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-06 11:55:49'),(53,1,'DELETE','classRemoved','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)','',1,'2026-01-06 11:58:23'),(54,1,'DELETE','classRemoved','คลาสรายเดือน - เคมี ม.4 - อาทิตย์ (เคมี)','',1,'2026-01-06 11:58:31'),(55,2,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-06 12:17:02'),(56,2,'UPDATE','classReactivated','','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)',1,'2026-01-06 12:19:17'),(57,13,'UPDATE','schoolName','','วัดหนอง',1,'2026-01-06 12:52:58'),(58,11,'UPDATE','schoolName','','การัน',1,'2026-01-06 12:53:28'),(59,14,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-06 22:44:52'),(60,2,'UPDATE','phoneParent','011111111','022222222',1,'2026-01-06 22:45:54'),(61,2,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: น้องขอจบคอร์ส','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: แอดมินกรอกผิด',1,'2026-01-06 22:48:20'),(62,2,'DELETE','classRemoved','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)','',1,'2026-01-06 22:53:46'),(63,2,'UPDATE','classReactivated','','คลาสรายเดือน - TPAT1 ม.4 - เสาร์ (TPAT1)',1,'2026-01-06 22:54:23'),(64,2,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)',1,'2026-01-06 23:00:03'),(65,2,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)','',1,'2026-01-07 07:48:18'),(66,16,'INSERT','coursePurchaseAdded','','เคมี กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 08:36:00'),(67,17,'INSERT','coursePurchaseAdded','','ภาษาต่างประเทศ รายเดือน เสาร์',1,'2026-01-07 08:36:10'),(68,18,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 08:36:32'),(69,18,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-07 08:36:32'),(70,18,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-กลุ่ม 12.0 ชั่วโมง',1,'2026-01-07 08:36:32'),(71,18,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ รายเดือน จันทร์-ศุกร์',1,'2026-01-07 08:36:32'),(72,15,'DELETE','classRemoved','คลาสรายเดือน - ภาษาต่างประเทศ ม.4 - จันทร์-ศุกร์ (ภาษาต่างประเทศ)','',1,'2026-01-07 08:43:52'),(73,17,'DELETE','classRemoved','คลาสรายเดือน - ภาษาต่างประเทศ ม.4 - จันทร์-ศุกร์ (ภาษาต่างประเทศ)','',1,'2026-01-07 08:43:52'),(74,18,'DELETE','classRemoved','คลาสรายเดือน - ภาษาต่างประเทศ ม.4 - จันทร์-ศุกร์ (ภาษาต่างประเทศ)','',1,'2026-01-07 08:43:52'),(75,16,'DELETE','classRemoved','คลาสรายเดือน - ภาษาต่างประเทศ ม.4 - จันทร์-ศุกร์ (ภาษาต่างประเทศ)','',1,'2026-01-07 08:43:52'),(76,2,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-07 08:46:52'),(77,20,'INSERT','coursePurchaseAdded','','ฟิสิกส์ รายเดือน เสาร์',1,'2026-01-07 08:56:22'),(78,21,'INSERT','coursePurchaseAdded','','ภาษาต่างประเทศ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-07 08:56:29'),(79,20,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.4 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 08:58:16'),(80,19,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.4 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 08:58:16'),(81,21,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.4 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 08:58:16'),(82,22,'INSERT','coursePurchaseAdded','','ฟิสิกส์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-07 08:59:14'),(83,19,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.5 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 08:59:59'),(84,22,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.5 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:00:20'),(85,20,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.5 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:00:20'),(86,21,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.5 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:00:20'),(87,17,'INSERT','coursePurchaseAdded','','TPAT1 กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 09:03:41'),(88,22,'INSERT','coursePurchaseAdded','','TPAT1 กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 09:04:01'),(89,17,'UPDATE','firstName','CCC','CCCv',1,'2026-01-07 09:04:53'),(90,13,'INSERT','coursePurchaseAdded','','TPAT1 กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 09:05:56'),(91,13,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.5 - A (TPAT1)','',1,'2026-01-07 09:09:26'),(92,22,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.5 - A (TPAT1)','',1,'2026-01-07 09:10:52'),(93,17,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.5 - A (TPAT1)','',1,'2026-01-07 09:10:52'),(94,23,'INSERT','coursePurchaseAdded','','TPAT1 กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 09:12:11'),(95,23,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.6 - A (TPAT1)','',1,'2026-01-07 09:14:21'),(96,21,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.6 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:28:39'),(97,20,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.6 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:28:39'),(98,22,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.6 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-07 09:28:39'),(99,22,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.6 - A (TPAT1)','',1,'2026-01-07 09:30:55'),(100,17,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.6 - A (TPAT1)','',1,'2026-01-07 09:30:55'),(101,10,'INSERT','coursePurchaseAdded','','TPAT1 กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 09:32:12'),(102,10,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.5 - A (TPAT1)','',1,'2026-01-07 09:32:47'),(103,19,'UPDATE','classReactivated','','คลาสรายเดือน - ชีววิทยา ม.4 - อังคาร-พฤหัส (ชีววิทยา)',1,'2026-01-07 09:56:47'),(104,15,'UPDATE','firstName','AAA','AAAกหกหก',1,'2026-01-07 10:00:19'),(105,16,'UPDATE','firstName','BBB','BBBฟดหกดหกดกห',1,'2026-01-07 10:00:32'),(106,17,'UPDATE','firstName','CCCv','CCCvหฟหฟหฟ',1,'2026-01-07 10:00:46'),(107,22,'UPDATE','nickname','dddd','ddddหฟห',1,'2026-01-07 10:01:08'),(108,1,'UPDATE','classReactivated','','คลาสรายเดือน - คณิตศาสตร์ ม.4 - จันทร์-ศุกร์ (คณิตศาสตร์)',1,'2026-01-07 10:10:20'),(109,24,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-07 10:15:20'),(110,25,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-08 00:13:32'),(111,25,'INSERT','coursePurchaseAdded','','เคมี PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-08 00:13:32'),(112,25,'INSERT','coursePurchaseAdded','','ชีววิทยา PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-08 00:13:32'),(113,25,'INSERT','coursePurchaseAdded','','ฟิสิกส์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-08 00:13:32'),(114,26,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-08 00:13:59'),(115,27,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-10 23:01:50'),(116,27,'UPDATE','phoneParent','','0111111111',1,'2026-01-10 23:02:55'),(117,27,'INSERT','coursePurchaseAdded','','ฟิสิกส์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-10 23:04:30'),(118,27,'UPDATE','แก้ไขคอร์ส','ฟิสิกส์ กลุ่มรวม 18.0 ชั่วโมง','ฟิสิกส์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: แอดมินกรอกผิด',1,'2026-01-10 23:05:15'),(119,2,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: แอดมินกรอกผิด','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: อื่น ๆ (แจ้งหัวหน้า)',1,'2026-01-10 23:06:38'),(120,2,'UPDATE','แก้ไขคอร์ส','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: อื่น ๆ (แจ้งหัวหน้า)','คณิตศาสตร์ กลุ่มรวม 18.0 ชั่วโมง | เหตุผล: น้องขอจบคอร์ส',1,'2026-01-10 23:06:50'),(121,2,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)',1,'2026-01-10 23:07:32'),(122,2,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)','',1,'2026-01-10 23:07:39'),(123,2,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - คณิตศาสตร์ ม.4 - A (คณิตศาสตร์)',1,'2026-01-10 23:11:13'),(124,7,'INSERT','coursePurchaseAdded','','ฟิสิกส์ กลุ่มรวม 18.0 ชั่วโมง',1,'2026-01-10 23:19:31'),(125,19,'DELETE','classRemoved','คลาสรายเดือน - ชีววิทยา ม.4 - อังคาร-พฤหัส (ชีววิทยา)','',1,'2026-01-10 23:23:23'),(126,15,'UPDATE','schoolName','','้้้้้้้44',1,'2026-01-18 08:11:35'),(127,28,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-30 03:25:00'),(128,29,'INSERT','coursePurchaseAdded','','คณิตศาสตร์ PV-เดี่ยว 12.0 ชั่วโมง',1,'2026-01-30 03:25:14'),(129,27,'DELETE','classRemoved','คลาสรายชั่วโมง (กลุ่ม) - ฟิสิกส์ ม.4 - C (ฟิสิกส์)','',1,'2026-01-30 03:34:40'),(130,2,'DELETE','classRemoved','คลาสรายเดือน - คณิตศาสตร์ ม.5 - อาทิตย์ (คณิตศาสตร์)','',1,'2026-02-07 10:56:35'),(131,17,'UPDATE','classReactivated','','คลาสรายชั่วโมง (กลุ่ม) - TPAT1 ม.5 - A (TPAT1)',1,'2026-02-07 11:11:57');
/*!40000 ALTER TABLE `student_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_hour_form_override_history`
--

DROP TABLE IF EXISTS `student_hour_form_override_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_hour_form_override_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `override_id` bigint DEFAULT NULL,
  `hour_form_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `action` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'UPDATE',
  PRIMARY KEY (`id`),
  KEY `idx_override_id` (`override_id`),
  KEY `idx_hour_form_id` (`hour_form_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_hour_form_student` (`hour_form_id`,`student_id`),
  KEY `idx_updated_at` (`updated_at`),
  KEY `idx_updated_by` (`updated_by`),
  CONSTRAINT `student_hour_form_override_history_ibfk_1` FOREIGN KEY (`override_id`) REFERENCES `student_hour_form_overrides` (`id`) ON DELETE SET NULL,
  CONSTRAINT `student_hour_form_override_history_ibfk_2` FOREIGN KEY (`hour_form_id`) REFERENCES `hour_forms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_hour_form_override_history_ibfk_3` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_hour_form_override_history_ibfk_4` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_hour_form_override_history`
--

LOCK TABLES `student_hour_form_override_history` WRITE;
/*!40000 ALTER TABLE `student_hour_form_override_history` DISABLE KEYS */;
INSERT INTO `student_hour_form_override_history` VALUES (1,88,64,27,'hoursTaught','2.00','4',1,'2026-01-18 07:40:44','UPDATE'),(2,88,64,27,'hoursTaught','4.00','5.4',1,'2026-01-18 07:42:27','UPDATE'),(3,90,65,27,'hoursTaught','2.00','0',1,'2026-01-18 07:46:06','UPDATE'),(4,90,65,27,'hoursTaught','0.00','2',1,'2026-01-18 07:47:32','UPDATE'),(5,90,65,27,'hoursTaught','2.00','0',1,'2026-01-18 07:50:29','UPDATE'),(6,90,65,27,'remark','แก้วันเรียน','ครูกรอกเกิน',1,'2026-01-18 07:50:29','UPDATE'),(7,89,65,7,'hoursTaught','2.00','4',1,'2026-01-18 07:51:01','UPDATE'),(8,89,65,7,'remark',NULL,'น้องขอจบคอร์ส',1,'2026-01-18 07:51:01','UPDATE'),(9,89,65,7,'hoursTaught','4.00','4',1,'2026-01-18 07:51:15','UPDATE'),(10,89,65,7,'remark','น้องขอจบคอร์ส','แอดมินนำเด็กออกจากคลาสไม่ทัน',1,'2026-01-18 07:51:15','UPDATE'),(11,89,65,7,'remark','แอดมินนำเด็กออกจากคลาสไม่ทัน','อื่น ๆ แจ้งหัวหน้า',1,'2026-01-18 07:53:52','UPDATE'),(12,89,65,7,'hoursTaught','4.00','5',1,'2026-01-18 07:54:06','UPDATE'),(13,89,65,7,'remark','อื่น ๆ แจ้งหัวหน้า','น้องขอจบคอร์ส',1,'2026-01-18 07:54:11','UPDATE');
/*!40000 ALTER TABLE `student_hour_form_override_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_hour_form_overrides`
--

DROP TABLE IF EXISTS `student_hour_form_overrides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_hour_form_overrides` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `hour_form_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `hours_override` decimal(5,2) DEFAULT NULL,
  `remark_override` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_override` (`hour_form_id`,`student_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `student_hour_form_overrides_ibfk_1` FOREIGN KEY (`hour_form_id`) REFERENCES `hour_forms` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_hour_form_overrides_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_hour_form_overrides`
--

LOCK TABLES `student_hour_form_overrides` WRITE;
/*!40000 ALTER TABLE `student_hour_form_overrides` DISABLE KEYS */;
INSERT INTO `student_hour_form_overrides` VALUES (1,1,2,0.00,'ครูกรอกเกิน'),(2,1,6,2.00,NULL),(3,2,6,2.00,NULL),(4,3,2,2.00,NULL),(5,3,6,2.00,NULL),(6,4,2,0.00,'แอดมินนำเด็กออกจากคลาสไม่ทัน'),(7,4,6,2.00,NULL),(8,5,2,4.00,'น้องขอจบคอร์ส'),(9,6,2,2.00,NULL),(10,7,2,0.00,'ครูกรอกเกิน'),(11,8,2,4.00,NULL),(12,9,2,2.00,NULL),(13,10,4,2.00,NULL),(14,10,6,2.00,NULL),(15,11,6,2.00,NULL),(16,12,2,2.00,NULL),(17,13,2,2.00,NULL),(18,14,2,2.00,NULL),(19,15,2,2.00,NULL),(20,15,4,2.00,NULL),(21,16,2,8.00,NULL),(22,16,4,8.00,NULL),(23,17,2,2.00,NULL),(24,17,4,2.00,NULL),(25,18,2,2.00,'แอดมินนำเด็กเข้าคลาสไม่ทัน'),(26,19,2,2.00,NULL),(27,19,4,2.00,NULL),(28,20,2,4.00,NULL),(29,20,4,4.00,NULL),(30,21,4,8.00,NULL),(31,21,6,8.00,NULL),(32,22,4,2.00,NULL),(33,22,6,0.00,'ครูกรอกเกิน'),(34,23,4,2.00,NULL),(35,23,6,0.00,'น้องขอจบคอร์ส'),(36,24,4,2.50,NULL),(37,24,6,2.50,NULL),(38,25,6,8.00,NULL),(39,26,6,2.00,NULL),(40,27,6,10.00,'ครูกรอกเกิน'),(41,28,6,2.00,NULL),(42,29,4,12.00,NULL),(43,29,6,12.00,NULL),(44,30,6,2.00,NULL),(45,31,4,2.00,NULL),(46,31,6,2.00,NULL),(47,32,4,6.00,NULL),(48,32,6,6.00,NULL),(49,33,4,12.00,NULL),(50,33,6,12.00,NULL),(51,34,6,8.50,NULL),(52,35,6,2.00,NULL),(53,36,6,12.00,NULL),(54,37,4,5.00,NULL),(55,37,6,5.00,NULL),(56,38,4,12.00,NULL),(57,38,6,12.00,NULL),(58,39,4,12.00,NULL),(59,39,6,12.00,NULL),(60,40,4,12.00,NULL),(61,40,6,12.00,NULL),(62,41,4,12.00,NULL),(63,41,6,12.00,NULL),(64,42,4,2.00,NULL),(65,43,2,20.00,NULL),(66,45,2,2.00,'แอดมินนำเด็กเข้าคลาสไม่ทัน'),(67,46,2,0.00,'ครูกรอกเกิน'),(68,47,4,2.00,'แอดมินนำเด็กเข้าคลาสไม่ทัน'),(69,48,13,2.00,NULL),(70,48,17,2.00,NULL),(71,48,22,2.00,NULL),(72,49,17,14.00,NULL),(73,49,22,14.00,NULL),(74,50,17,2.00,NULL),(75,50,22,2.00,NULL),(76,52,17,2.00,NULL),(77,52,22,2.00,NULL),(78,52,23,2.00,NULL),(79,53,22,16.00,NULL),(80,53,17,16.00,NULL),(81,54,10,4.00,NULL),(82,55,10,14.00,NULL),(83,56,10,2.00,'น้องขอจบคอร์ส'),(84,57,24,2.00,NULL),(85,58,24,2.00,NULL),(86,63,2,2.00,'แอดมินนำเด็กเข้าคลาสไม่ทัน'),(87,64,7,0.00,'แก้วันเรียน'),(88,64,27,5.40,NULL),(89,65,7,5.00,'น้องขอจบคอร์ส'),(90,65,27,0.00,'ครูกรอกเกิน'),(91,66,7,2.00,NULL),(92,66,27,2.00,NULL),(93,67,7,2.00,NULL),(94,67,27,2.00,NULL),(95,68,27,4.00,'น้องขอจบคอร์ส'),(96,69,27,2.00,'น้องขอจบคอร์ส'),(97,70,7,2.00,NULL),(98,70,27,2.00,NULL),(99,71,17,18.00,NULL);
/*!40000 ALTER TABLE `student_hour_form_overrides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `school_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `grade_id` bigint NOT NULL,
  `phone_student` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_parent` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `study_plan` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_code` (`student_code`),
  KEY `created_by` (`created_by`),
  KEY `idx_name` (`first_name`,`last_name`),
  KEY `idx_grade_active` (`grade_id`,`is_active`),
  KEY `idx_active` (`is_active`),
  KEY `idx_school` (`school_name`(50)),
  KEY `idx_student_code` (`student_code`),
  KEY `idx_student_active` (`is_active`,`first_name`,`last_name`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`grade_id`) REFERENCES `grades` (`id`),
  CONSTRAINT `students_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'M4-20260104-0001','Afd','','agg','',5,'','','',1,'2026-01-04 08:06:38','2026-01-07 16:15:05',1),(2,'M4-20260104-0002','B','บี','b','บางปะกอกวิทยาคม',5,'0111111111','022222222','วิทย์-คณิต',1,'2026-01-04 08:07:22','2026-01-07 16:15:05',1),(3,'M4-20260104-0003','C','','c','',5,'','','',1,'2026-01-04 08:09:00','2026-01-07 16:15:05',1),(4,'M4-20260104-0004','D','','d','',5,'','','',1,'2026-01-04 08:09:08','2026-01-07 16:15:05',1),(5,'M4-20260104-0005','E','','e','',5,'','','',1,'2026-01-04 08:09:16','2026-01-07 16:15:05',1),(6,'M4-20260104-0006','Fโโ','','f','',5,'','','',1,'2026-01-04 08:10:16','2026-01-07 16:15:05',1),(7,'M3-20260106-0007','G','','g','',4,'','','',1,'2026-01-06 09:11:00','2026-01-07 16:15:05',1),(8,'TEST_MONTHLY_001','ทดสอบรายเดือน','รายเดือน','เทส',NULL,2,NULL,NULL,NULL,1,'2024-12-15 03:00:00','2026-01-07 16:15:05',1),(9,'M4-20260106-0009','H','','h','',5,'','','',1,'2025-09-09 03:00:00','2026-01-07 16:15:05',1),(10,'M4-20260106-0010','I','','i','',5,'','','',1,'2025-09-09 03:00:00','2026-01-07 16:15:05',1),(11,'M4-20260106-0011','J','','j','การัน',5,'','','',1,'2025-01-01 03:00:00','2026-01-07 16:15:05',1),(12,'M5-20260106-0012','K','','k','',6,'','','',1,'2025-01-01 03:00:00','2026-01-07 16:15:05',1),(13,'M5-20260107-0013','JJ','','j','วัดหนอง',6,'0111111111','','',1,'2026-01-06 11:28:46','2026-01-07 16:15:05',1),(14,'M4-20260107-0014','T','','t','',5,'','','',1,'2026-01-06 22:44:52','2026-01-07 16:15:05',1),(15,'M4-20260107-0015','AAAกหกหก','','aaa','้้้้้้้44',5,'','','',1,'2026-01-07 08:35:50','2026-01-18 08:11:35',1),(16,'M4-20260107-0016','BBBฟดหกดหกดกห','','bbb','',5,'','','',1,'2026-01-07 08:36:00','2026-01-07 10:00:32',1),(17,'M5-20260107-0017','CCCvหฟหฟหฟ','','ccc','',6,'','','',1,'2026-01-07 08:36:10','2026-01-07 10:00:46',1),(18,'M4-20260107-0018','DDD','','ddd','',5,'','','',1,'2026-01-07 08:36:32','2026-01-07 16:15:05',1),(19,'M4-20260107-0019','AAAA','','aaaa','',5,'','','',1,'2026-01-07 08:56:13','2026-01-07 16:15:05',1),(20,'M4-20260107-0020','BBBB','','bbbb','',5,'','','',1,'2026-01-07 08:56:22','2026-01-07 16:15:05',1),(21,'M4-20260107-0021','CCCC','','cccc','',5,'','','',1,'2026-01-07 08:56:29','2026-01-07 16:15:05',1),(22,'M5-20260107-0022','DDDD','','ddddหฟห','',6,'','','',1,'2026-01-07 08:59:14','2026-01-07 10:01:08',1),(23,'M6-20260107-0023','jjjjjjjj','','jjj','',7,'','','',1,'2026-01-07 09:12:11','2026-01-07 16:15:06',1),(24,'M4-20260108-0024','sfvd','d','d','',4,'','','',1,'2026-01-07 10:15:20','2026-01-07 10:15:20',1),(25,'M5-20260108-0025','ดีเซ็ม','','ดีเซ็ม','',5,'','','',1,'2026-01-08 00:13:32','2026-01-08 00:13:32',1),(26,'M5-20260108-0026','พลอย','','พลอย','',5,'','','',1,'2026-01-08 00:13:59','2026-01-08 00:13:59',1),(27,'M4-20260111-0027','AAA','','aaa','',4,'','0111111111','',1,'2026-01-10 23:01:50','2026-01-10 23:02:55',1),(28,'M4-20260130-0028','แก้ม','','แก้ม','',4,'','','',1,'2026-01-30 03:25:00','2026-01-30 03:25:00',1),(29,'M4-20260130-0029','แก้ม','','แก้ม','',4,'','','',1,'2026-01-30 03:25:14','2026-01-30 03:25:14',1);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `display_order` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `subject_name` (`subject_name`),
  KEY `idx_active` (`is_active`),
  KEY `idx_display_order` (`display_order`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (1,'คณิตศาสตร์',1,1,'2026-01-04 15:06:15'),(2,'ฟิสิกส์',1,2,'2026-01-04 15:06:15'),(3,'เคมี',1,3,'2026-01-04 15:06:15'),(4,'ชีววิทยา',1,4,'2026-01-04 15:06:15'),(5,'ภาษาต่างประเทศ',1,5,'2026-01-04 15:06:15'),(6,'TPAT1',1,6,'2026-01-04 08:12:23'),(7,'TPAT2',1,7,'2026-01-10 23:24:23');
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_history`
--

DROP TABLE IF EXISTS `user_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
  `field_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `updated_by` bigint NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `updated_by` (`updated_by`),
  KEY `idx_user` (`user_id`),
  KEY `idx_updated_at` (`updated_at` DESC),
  CONSTRAINT `user_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_history_ibfk_2` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_history`
--

LOCK TABLES `user_history` WRITE;
/*!40000 ALTER TABLE `user_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `login_at` timestamp NOT NULL,
  `logout_at` timestamp NULL DEFAULT NULL,
  `session_duration` int DEFAULT NULL COMMENT 'ระยะเวลา (วินาที)',
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `logout_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'manual, auto, timeout',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_login_at` (`login_at` DESC),
  KEY `idx_logout_at` (`logout_at`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sessions`
--

LOCK TABLES `user_sessions` WRITE;
/*!40000 ALTER TABLE `user_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'admin, tutor',
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'mod','$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.','admin','มด','2026-01-04 15:06:15','2026-01-04 15:06:15',1),(2,'bam','$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.','tutor','แบม','2026-01-04 15:06:15','2026-01-04 15:06:15',1),(3,'nueng','$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.','tutor','หนึ่ง','2026-01-04 15:06:15','2026-01-04 15:06:15',1),(4,'year','$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.','tutor','เยียร์','2026-01-04 15:06:15','2026-01-04 15:06:15',1),(5,'pol','$2a$12$xxaPGxFYJviSYaSHeI5U0uqC/Erj/Y0OKu10KvRl3etm6hszLvcR.','tutor','พล','2026-01-04 15:06:15','2026-01-04 15:06:15',1),(6,'erik','$2a$10$ACRGoCkq6WmeEt8GbjUPMuGytCtDkywRJxhPxdz4DqtnGfCPce8h6','tutor','erik','2026-01-04 20:36:07','2026-01-04 20:36:07',1),(7,'ABC','$2a$10$3PAuolIdkGu9id3lTOG8z..PbeKEyH5ikU4kordTiLzXQToSIec9y','tutor','abc (คณิต)','2026-01-06 23:03:50','2026-01-06 23:04:35',1),(8,'noon','$2a$10$jR0p3DItQbfdln7oezDHneI7oPFkpJi6WPVP/NHyoyUIeG1GSH.w6','tutor','นุ่น','2026-01-08 00:10:31','2026-01-08 00:10:31',1),(9,'tar','$2a$10$1.LMomTTv6WcYppu937h0O19mIMnFEaJUGlMclExkEMVZlzoQGEmK','tutor','ต้า','2026-01-08 00:10:50','2026-01-08 00:10:50',1),(10,'fahsai','$2a$10$SAlIGOg3ye/c6UwNi7VZI.qHKLZVDuuozoO/8pQ5DAf58J/an5bRy','tutor','ฟ้าใส','2026-01-10 23:13:42','2026-01-10 23:13:42',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_hourly_group_class_summary`
--

DROP TABLE IF EXISTS `v_hourly_group_class_summary`;
/*!50001 DROP VIEW IF EXISTS `v_hourly_group_class_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_hourly_group_class_summary` AS SELECT 
 1 AS `class_id`,
 1 AS `class_name`,
 1 AS `subject_name`,
 1 AS `grade_name`,
 1 AS `subtype_name`,
 1 AS `total_students`,
 1 AS `total_sessions`,
 1 AS `total_hours_taught`,
 1 AS `is_active`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_hourly_individual_class_summary`
--

DROP TABLE IF EXISTS `v_hourly_individual_class_summary`;
/*!50001 DROP VIEW IF EXISTS `v_hourly_individual_class_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_hourly_individual_class_summary` AS SELECT 
 1 AS `class_id`,
 1 AS `class_name`,
 1 AS `subject_name`,
 1 AS `grade_name`,
 1 AS `student_first_name`,
 1 AS `student_last_name`,
 1 AS `student_nickname`,
 1 AS `total_sessions`,
 1 AS `total_hours_taught`,
 1 AS `is_active`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_math_forms_summary`
--

DROP TABLE IF EXISTS `v_math_forms_summary`;
/*!50001 DROP VIEW IF EXISTS `v_math_forms_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_math_forms_summary` AS SELECT 
 1 AS `tutor_id`,
 1 AS `username`,
 1 AS `tutor_nickname`,
 1 AS `class_id`,
 1 AS `class_name`,
 1 AS `subject_name`,
 1 AS `teaching_date`,
 1 AS `hours_taught`,
 1 AS `students_present`,
 1 AS `students_absent`,
 1 AS `content`,
 1 AS `remark`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_science_forms_summary`
--

DROP TABLE IF EXISTS `v_science_forms_summary`;
/*!50001 DROP VIEW IF EXISTS `v_science_forms_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_science_forms_summary` AS SELECT 
 1 AS `tutor_id`,
 1 AS `username`,
 1 AS `tutor_nickname`,
 1 AS `class_id`,
 1 AS `class_name`,
 1 AS `subject_name`,
 1 AS `teaching_date`,
 1 AS `hours_taught`,
 1 AS `students_present`,
 1 AS `students_absent`,
 1 AS `content`,
 1 AS `remark`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_student_course_summary`
--

DROP TABLE IF EXISTS `v_student_course_summary`;
/*!50001 DROP VIEW IF EXISTS `v_student_course_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_student_course_summary` AS SELECT 
 1 AS `id`,
 1 AS `student_id`,
 1 AS `student_code`,
 1 AS `first_name`,
 1 AS `last_name`,
 1 AS `nickname`,
 1 AS `grade_name`,
 1 AS `subject_name`,
 1 AS `class_type`,
 1 AS `class_id`,
 1 AS `monthly_subtype_id`,
 1 AS `monthly_subtype_name`,
 1 AS `hours_purchased`,
 1 AS `hours_completed`,
 1 AS `hours_remaining`,
 1 AS `completion_percentage`,
 1 AS `milestones_completed`,
 1 AS `purchase_date`,
 1 AS `notes`,
 1 AS `deleted_reason`,
 1 AS `is_active`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_tutor_hour_forms_summary`
--

DROP TABLE IF EXISTS `v_tutor_hour_forms_summary`;
/*!50001 DROP VIEW IF EXISTS `v_tutor_hour_forms_summary`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_tutor_hour_forms_summary` AS SELECT 
 1 AS `tutor_id`,
 1 AS `username`,
 1 AS `tutor_nickname`,
 1 AS `class_type`,
 1 AS `class_id`,
 1 AS `subject_name`,
 1 AS `teaching_date`,
 1 AS `hours_taught`,
 1 AS `students_present`,
 1 AS `students_absent`,
 1 AS `content`,
 1 AS `remark`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_hourly_group_class_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_hourly_group_class_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_hourly_group_class_summary` AS select `c`.`id` AS `class_id`,`c`.`class_name` AS `class_name`,`sub`.`subject_name` AS `subject_name`,`g`.`grade_name` AS `grade_name`,`hgs`.`subtype_name` AS `subtype_name`,count(distinct `e`.`student_id`) AS `total_students`,count(distinct `hf`.`id`) AS `total_sessions`,coalesce(sum(`hf`.`hours_taught`),0) AS `total_hours_taught`,`c`.`is_active` AS `is_active`,`c`.`created_at` AS `created_at` from (((((`hourly_group_classes` `c` join `subjects` `sub` on((`c`.`subject_id` = `sub`.`id`))) left join `grades` `g` on((`c`.`grade_id` = `g`.`id`))) join `hourly_group_subtypes` `hgs` on((`c`.`subtype_id` = `hgs`.`id`))) left join `hourly_group_enrollments` `e` on(((`c`.`id` = `e`.`class_id`) and (`e`.`is_active` = true)))) left join `hour_forms` `hf` on(((`hf`.`class_type` = 'hourly_group') and (`hf`.`class_id` = `c`.`id`)))) group by `c`.`id`,`c`.`class_name`,`sub`.`subject_name`,`g`.`grade_name`,`hgs`.`subtype_name`,`c`.`is_active`,`c`.`created_at` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_hourly_individual_class_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_hourly_individual_class_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_hourly_individual_class_summary` AS select `c`.`id` AS `class_id`,`c`.`class_name` AS `class_name`,`sub`.`subject_name` AS `subject_name`,`g`.`grade_name` AS `grade_name`,`s`.`first_name` AS `student_first_name`,`s`.`last_name` AS `student_last_name`,`s`.`nickname` AS `student_nickname`,count(`hf`.`id`) AS `total_sessions`,coalesce(sum(`hf`.`hours_taught`),0) AS `total_hours_taught`,`c`.`is_active` AS `is_active`,`c`.`created_at` AS `created_at` from ((((`hourly_individual_classes` `c` join `subjects` `sub` on((`c`.`subject_id` = `sub`.`id`))) left join `grades` `g` on((`c`.`grade_id` = `g`.`id`))) join `students` `s` on((`c`.`student_id` = `s`.`id`))) left join `hour_forms` `hf` on(((`hf`.`class_type` = 'hourly_individual') and (`hf`.`class_id` = `c`.`id`)))) group by `c`.`id`,`c`.`class_name`,`sub`.`subject_name`,`g`.`grade_name`,`s`.`first_name`,`s`.`last_name`,`s`.`nickname`,`c`.`is_active`,`c`.`created_at` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_math_forms_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_math_forms_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_math_forms_summary` AS select `mf`.`tutor_id` AS `tutor_id`,`u`.`username` AS `username`,`u`.`nickname` AS `tutor_nickname`,`mf`.`class_id` AS `class_id`,`mc`.`class_name` AS `class_name`,`sub`.`subject_name` AS `subject_name`,`mf`.`teaching_date` AS `teaching_date`,`mf`.`hours_taught` AS `hours_taught`,`mf`.`students_present` AS `students_present`,`mf`.`students_absent` AS `students_absent`,`mf`.`content` AS `content`,`mf`.`remark` AS `remark`,`mf`.`created_at` AS `created_at` from (((`math_forms` `mf` join `users` `u` on((`mf`.`tutor_id` = `u`.`id`))) join `monthly_classes` `mc` on((`mf`.`class_id` = `mc`.`id`))) join `subjects` `sub` on((`mf`.`subject_id` = `sub`.`id`))) order by `mf`.`teaching_date` desc,`mf`.`created_at` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_science_forms_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_science_forms_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_science_forms_summary` AS select `sf`.`tutor_id` AS `tutor_id`,`u`.`username` AS `username`,`u`.`nickname` AS `tutor_nickname`,`sf`.`class_id` AS `class_id`,`mc`.`class_name` AS `class_name`,`sub`.`subject_name` AS `subject_name`,`sf`.`teaching_date` AS `teaching_date`,`sf`.`hours_taught` AS `hours_taught`,`sf`.`students_present` AS `students_present`,`sf`.`students_absent` AS `students_absent`,`sf`.`content` AS `content`,`sf`.`remark` AS `remark`,`sf`.`created_at` AS `created_at` from (((`science_forms` `sf` join `users` `u` on((`sf`.`tutor_id` = `u`.`id`))) join `monthly_classes` `mc` on((`sf`.`class_id` = `mc`.`id`))) join `subjects` `sub` on((`sf`.`subject_id` = `sub`.`id`))) order by `sf`.`teaching_date` desc,`sf`.`created_at` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_student_course_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_student_course_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_student_course_summary` AS select `scp`.`id` AS `id`,`scp`.`student_id` AS `student_id`,`s`.`student_code` AS `student_code`,`s`.`first_name` AS `first_name`,`s`.`last_name` AS `last_name`,`s`.`nickname` AS `nickname`,`g`.`grade_name` AS `grade_name`,`sub`.`subject_name` AS `subject_name`,`scp`.`class_type` AS `class_type`,`scp`.`class_id` AS `class_id`,`scp`.`monthly_subtype_id` AS `monthly_subtype_id`,`ms`.`subtype_name` AS `monthly_subtype_name`,`scp`.`hours_purchased` AS `hours_purchased`,`scp`.`hours_completed` AS `hours_completed`,(case when (`scp`.`hours_purchased` is not null) then (`scp`.`hours_purchased` - `scp`.`hours_completed`) else NULL end) AS `hours_remaining`,(case when ((`scp`.`hours_purchased` is not null) and (`scp`.`hours_purchased` > 0)) then round(((`scp`.`hours_completed` / `scp`.`hours_purchased`) * 100),2) else NULL end) AS `completion_percentage`,(case when ((`scp`.`hours_purchased` is not null) and (`scp`.`hours_purchased` > 0)) then floor((`scp`.`hours_completed` / `scp`.`hours_purchased`)) else NULL end) AS `milestones_completed`,`scp`.`purchase_date` AS `purchase_date`,`scp`.`notes` AS `notes`,`scp`.`deleted_reason` AS `deleted_reason`,`scp`.`is_active` AS `is_active`,`scp`.`created_at` AS `created_at` from ((((`student_course_purchases` `scp` join `students` `s` on((`scp`.`student_id` = `s`.`id`))) join `subjects` `sub` on((`scp`.`subject_id` = `sub`.`id`))) left join `grades` `g` on((`s`.`grade_id` = `g`.`id`))) left join `monthly_subtypes` `ms` on((`scp`.`monthly_subtype_id` = `ms`.`id`))) where (`scp`.`is_active` = true) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_tutor_hour_forms_summary`
--

/*!50001 DROP VIEW IF EXISTS `v_tutor_hour_forms_summary`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_tutor_hour_forms_summary` AS select `hf`.`tutor_id` AS `tutor_id`,`u`.`username` AS `username`,`u`.`nickname` AS `tutor_nickname`,`hf`.`class_type` AS `class_type`,`hf`.`class_id` AS `class_id`,`sub`.`subject_name` AS `subject_name`,`hf`.`teaching_date` AS `teaching_date`,`hf`.`hours_taught` AS `hours_taught`,`hf`.`students_present` AS `students_present`,`hf`.`students_absent` AS `students_absent`,`hf`.`content` AS `content`,`hf`.`remark` AS `remark`,`hf`.`created_at` AS `created_at` from ((`hour_forms` `hf` join `users` `u` on((`hf`.`tutor_id` = `u`.`id`))) join `subjects` `sub` on((`hf`.`subject_id` = `sub`.`id`))) order by `hf`.`teaching_date` desc,`hf`.`created_at` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-08 20:24:37
