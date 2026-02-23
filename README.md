# student_result_management_system
Student Result Management System

A robust desktop application built using Java Swing and Oracle Database (JDBC). This system allows educational institutions to manage student academic records securely, providing subject-specific access to faculty members and a transparent result portal for students.

🚀 Features

👨‍🏫 Faculty Portal

Subject-Specific Access: Faculty members are assigned specific subjects (Java, DBMS, Maths, AMSD, or AI). They can only edit marks for their allocated subject.

Search Module: Quickly find a student by their Roll Number to update or view their performance.

Real-time Ledger: A dynamic table showing all student records, averages, and grades.

🎓 Student Portal

Secure Access: Students can view their results by entering their unique Roll Number.

Performance Report Card: A detailed, colorful report card displaying marks for all subjects, aggregate percentage, and final grade.

🛠 Technical Features

Oracle Integration: Persistent storage using Oracle 18c/19c/21c (Pluggable Database: XEPDB1).

JDBC Driver: Secure communication via ojdbc8.jar.

Modern UI: Colorful, responsive Swing interface with "Segoe UI" typography.

📋 Prerequisites

Java Development Kit (JDK): Version 8 or higher.

Oracle Database: Express Edition (XE) preferred.

JDBC Driver: ojdbc8.jar (must be in the project classpath).

IDE: VS Code (with Java Extension Pack) or IntelliJ IDEA.

⚙️ Setup & Installation

1. Database Configuration

Ensure your Oracle Pluggable Database (XEPDB1) is running. You can verify this in SQL*Plus:

ALTER PLUGGABLE DATABASE XEPDB1 OPEN;


2. Project Setup in VS Code

Open the project folder in VS Code.

Download the ojdbc8.jar file.

In the Java Projects view, go to Referenced Libraries and click the + icon to add the ojdbc8.jar.

3. Update Credentials

Open StudentResultManagementApp.java and update the DatabaseHandler class with your local credentials:

private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
private static final String USER = "system";
private static final String PASS = "your_oracle_password";


4. Running the App

Compile and run the application:

javac StudentResultManagementApp.java
java -cp ".;ojdbc8.jar" StudentResultManagementApp


🔑 Test Credentials (Faculty)

Username

Password

Assigned Subject

java_prof

java123

Java

dbms_prof

dbms123

DBMS

maths_prof

maths123

Maths

amsd_prof

amsd123

AMSD

ai_prof

ai123

AI

📂 Database Schema

The system automatically creates the STUDENT_RESULTS table upon the first successful run.

CREATE TABLE student_results (
    roll_no VARCHAR2(20) PRIMARY KEY,
    name    VARCHAR2(100) NOT NULL,
    java    NUMBER DEFAULT 0,
    dbms    NUMBER DEFAULT 0,
    maths   NUMBER DEFAULT 0,
    amsd    NUMBER DEFAULT 0,
    ai      NUMBER DEFAULT 0
);


👥 Contributors

Sanjay Reddy

Bhargav

Jyothi Anand

Jaya Sai Avinash
