-- Script لتحديث جدول الدكاترة وإضافة أعمدة phone و email
-- Update script for doctors table to add phone and email columns

USE clinic_db;

-- إضافة عمود phone
ALTER TABLE doctors
ADD COLUMN phone VARCHAR(20) AFTER specialty;

-- إضافة عمود email
ALTER TABLE doctors
ADD COLUMN email VARCHAR(100) AFTER phone;

-- Update doctors table to add available column
ALTER TABLE doctors ADD COLUMN available BOOLEAN DEFAULT TRUE AFTER email;

-- إضافة فهرس للبحث السريع في الإيميل
ALTER TABLE doctors
ADD INDEX idx_email (email);

-- إضافة فهرس للبحث السريع في الهاتف
ALTER TABLE doctors
ADD INDEX idx_phone (phone);

-- التحقق من البنية الجديدة للجدول
DESCRIBE doctors;

-- عرض البيانات الحالية
SELECT * FROM doctors;