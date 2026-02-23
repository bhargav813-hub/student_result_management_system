import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class StudentResultManagementApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Faculty currentFaculty = null;

    // Faculty Credentials
    private final List<Faculty> facultyList = List.of(
        new Faculty("java_prof", "java123", "Java"),
        new Faculty("dbms_prof", "dbms123", "DBMS"),
        new Faculty("maths_prof", "maths123", "Maths"),
        new Faculty("amsd_prof", "amsd123", "AMSD"),
        new Faculty("ai_prof", "ai123", "AI")
    );

    public StudentResultManagementApp() {
        // Initialize Database
        DatabaseHandler.initializeDatabase();

        setTitle("Online Student Result Management System - Oracle Integrated");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createWelcomeScreen(), "Welcome");
        mainPanel.add(createFacultyLoginScreen(), "FacultyLogin");
        mainPanel.add(createFacultyDashboard(), "FacultyDashboard");
        mainPanel.add(createStudentLoginScreen(), "StudentLogin");
        mainPanel.add(createStudentResultView(), "StudentResultView");

        add(mainPanel);
    }

    // --- SCREEN: Welcome ---
    private JPanel createWelcomeScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("Student Result Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JButton facultyBtn = createStyledButton("Faculty Login", new Color(52, 152, 219));
        facultyBtn.addActionListener(e -> cardLayout.show(mainPanel, "FacultyLogin"));

        JButton studentBtn = createStyledButton("Student Portal", new Color(46, 204, 113));
        studentBtn.addActionListener(e -> cardLayout.show(mainPanel, "StudentLogin"));

        gbc.gridx = 0; btnPanel.add(facultyBtn, gbc);
        gbc.gridx = 1; btnPanel.add(studentBtn, gbc);

        panel.add(btnPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- SCREEN: Faculty Login ---
    private JPanel createFacultyLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel head = new JLabel("Faculty Authentication");
        head.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(head, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1; panel.add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1; panel.add(passField, gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton login = new JButton("Login");
        login.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            Optional<Faculty> auth = facultyList.stream()
                .filter(f -> f.username.equals(user) && f.password.equals(pass))
                .findFirst();

            if (auth.isPresent()) {
                currentFaculty = auth.get();
                setupDashboardForFaculty();
                cardLayout.show(mainPanel, "FacultyDashboard");
                userField.setText(""); passField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Faculty Credentials", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(login, gbc);

        JButton back = new JButton("Back");
        back.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        gbc.gridy = 4; panel.add(back, gbc);

        return panel;
    }

    // --- SCREEN: Faculty Dashboard ---
    private DefaultTableModel tableModel;
    private JTextField rollField, nameField, javaField, dbmsField, mathsField, amsdField, aiField;
    private JTextField searchBox;
    private JLabel facultyInfoLabel;

    private JPanel createFacultyDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 230, 230));
        
        facultyInfoLabel = new JLabel("Logged in as: ", JLabel.LEFT);
        facultyInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        facultyInfoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchBox = new JTextField(12);
        JButton searchBtn = new JButton("Search Roll No");
        JButton refreshBtn = new JButton("Show All");

        searchBtn.addActionListener(e -> handleSearchAction());
        refreshBtn.addActionListener(e -> refreshTable());

        searchPanel.add(new JLabel("Search Student: "));
        searchPanel.add(searchBox);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        headerPanel.add(facultyInfoLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Entry Form
        JPanel form = new JPanel(new GridLayout(9, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Marks Entry (Restricted Access)"));
        form.setPreferredSize(new Dimension(350, 0));
        
        rollField = new JTextField();
        nameField = new JTextField();
        javaField = new JTextField();
        dbmsField = new JTextField();
        mathsField = new JTextField();
        amsdField = new JTextField();
        aiField = new JTextField();

        form.add(new JLabel("Student Roll No:")); form.add(rollField);
        form.add(new JLabel("Student Name:")); form.add(nameField);
        form.add(new JLabel("Java:")); form.add(javaField);
        form.add(new JLabel("DBMS:")); form.add(dbmsField);
        form.add(new JLabel("Maths:")); form.add(mathsField);
        form.add(new JLabel("AMSD:")); form.add(amsdField);
        form.add(new JLabel("AI:")); form.add(aiField);

        JButton saveBtn = new JButton("Save/Update Subject Marks");
        saveBtn.addActionListener(e -> handleSaveAction());
        form.add(new JLabel("")); form.add(saveBtn);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Roll No", "Name", "Java", "DBMS", "Maths", "AMSD", "AI", "Avg", "Grade"}, 0);
        JTable table = new JTable(tableModel);
        
        panel.add(form, BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            currentFaculty = null;
            cardLayout.show(mainPanel, "Welcome");
        });
        panel.add(logout, BorderLayout.SOUTH);

        return panel;
    }

    private void handleSearchAction() {
        String roll = searchBox.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Roll Number to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StudentResult s = DatabaseHandler.getStudent(roll);
        if (s != null) {
            tableModel.setRowCount(0);
            tableModel.addRow(new Object[]{
                s.rollNo, s.name, s.m1, s.m2, s.m3, s.m4, s.m5, 
                String.format("%.1f", s.getAvg()), s.getGrade()
            });
            // Auto-fill form with found data for quick editing
            rollField.setText(s.rollNo);
            nameField.setText(s.name);
            javaField.setText(String.valueOf(s.m1));
            dbmsField.setText(String.valueOf(s.m2));
            mathsField.setText(String.valueOf(s.m3));
            amsdField.setText(String.valueOf(s.m4));
            aiField.setText(String.valueOf(s.m5));
        } else {
            JOptionPane.showMessageDialog(this, "No record found for Roll Number: " + roll, "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setupDashboardForFaculty() {
        facultyInfoLabel.setText("Faculty: " + currentFaculty.username + " | Allocated Subject: " + currentFaculty.subject);
        
        javaField.setEnabled(false);
        dbmsField.setEnabled(false);
        mathsField.setEnabled(false);
        amsdField.setEnabled(false);
        aiField.setEnabled(false);
        
        switch (currentFaculty.subject) {
            case "Java" -> javaField.setEnabled(true);
            case "DBMS" -> dbmsField.setEnabled(true);
            case "Maths" -> mathsField.setEnabled(true);
            case "AMSD" -> amsdField.setEnabled(true);
            case "AI" -> aiField.setEnabled(true);
        }

        refreshTable();
    }

    private void handleSaveAction() {
        try {
            String roll = rollField.getText().trim();
            String name = nameField.getText().trim();
            if (roll.isEmpty() || name.isEmpty()) throw new Exception("Metadata missing");

            StudentResult student = DatabaseHandler.getStudent(roll);
            if (student == null) {
                student = new StudentResult(roll, name, 0, 0, 0, 0, 0);
             }

            switch (currentFaculty.subject) {
                case "Java" -> student.m1 = Integer.parseInt(javaField.getText());
                case "DBMS" -> student.m2 = Integer.parseInt(dbmsField.getText());
                case "Maths" -> student.m3 = Integer.parseInt(mathsField.getText());
                case "AMSD" -> student.m4 = Integer.parseInt(amsdField.getText());
                case "AI" -> student.m5 = Integer.parseInt(aiField.getText());
            }

            DatabaseHandler.saveOrUpdateStudent(student);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Success: " + currentFaculty.subject + " marks updated in Oracle DB.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter numeric marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- SCREEN: Student Portal ---
    private JPanel createStudentLoginScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lbl = new JLabel("View Student Report Card");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gbc.gridwidth = 2; panel.add(lbl, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        panel.add(new JLabel("Enter Roll No:"), gbc);
        JTextField rollInput = new JTextField(15);
        gbc.gridx = 1; panel.add(rollInput, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton viewBtn = new JButton("Get Report");
        viewBtn.addActionListener(e -> {
            String roll = rollInput.getText();
            StudentResult s = DatabaseHandler.getStudent(roll);
            if (s != null) {
                showStudentResult(s);
                cardLayout.show(mainPanel, "StudentResultView");
            } else {
                JOptionPane.showMessageDialog(this, "Roll No not found in database.");
            }
        });
        panel.add(viewBtn, gbc);

        JButton back = new JButton("Back");
        back.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        gbc.gridy = 3; panel.add(back, gbc);
        return panel;
    }

    private JLabel resName, resRoll, resM1, resM2, resM3, resM4, resM5, resAvg, resGrade;
    private JPanel createStudentResultView() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel display = new JPanel(new GridLayout(9, 2, 15, 15));
        display.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        resRoll = new JLabel(); resName = new JLabel();
        resM1 = new JLabel(); resM2 = new JLabel();
        resM3 = new JLabel(); resM4 = new JLabel();
        resM5 = new JLabel(); resAvg = new JLabel();
        resGrade = new JLabel();

        display.add(new JLabel("Roll Number:")); display.add(resRoll);
        display.add(new JLabel("Student Name:")); display.add(resName);
        display.add(new JLabel("Java Marks:")); display.add(resM1);
        display.add(new JLabel("DBMS Marks:")); display.add(resM2);
        display.add(new JLabel("Maths Marks:")); display.add(resM3);
        display.add(new JLabel("AMSD Marks:")); display.add(resM4);
        display.add(new JLabel("AI Marks:")); display.add(resM5);
        display.add(new JLabel("Aggregate Average:")); display.add(resAvg);
        display.add(new JLabel("Final Grade:")); display.add(resGrade);

        panel.add(new JLabel("OFFICIAL PERFORMANCE REPORT", JLabel.CENTER), BorderLayout.NORTH);
        panel.add(display, BorderLayout.CENTER);
        JButton back = new JButton("Close Report");
        back.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        panel.add(back, BorderLayout.SOUTH);
        return panel;
    }

    private void showStudentResult(StudentResult s) {
        resRoll.setText(s.rollNo); resName.setText(s.name);
        resM1.setText(String.valueOf(s.m1)); resM2.setText(String.valueOf(s.m2));
        resM3.setText(String.valueOf(s.m3)); resM4.setText(String.valueOf(s.m4));
        resM5.setText(String.valueOf(s.m5));
        resAvg.setText(String.format("%.2f%%", s.getAvg()));
        resGrade.setText(s.getGrade());
    }

    private void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        searchBox.setText("");
        List<StudentResult> students = DatabaseHandler.getAllStudents();
        for (StudentResult s : students) {
            tableModel.addRow(new Object[]{s.rollNo, s.name, s.m1, s.m2, s.m3, s.m4, s.m5, String.format("%.1f", s.getAvg()), s.getGrade()});
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(220, 70));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setFocusPainted(false);
        return btn;
    }

    // --- DATABASE HANDLER (JDBC) ---
    static class DatabaseHandler {
        private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
        private static final String USER = "system";
        private static final String PASS = "root";

        static void initializeDatabase() {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                     Statement stmt = conn.createStatement()) {
                    
                    String sql = "CREATE TABLE student_results (" +
                                 "roll_no VARCHAR2(20) PRIMARY KEY, " +
                                 "name VARCHAR2(100), " +
                                 "java NUMBER, dbms NUMBER, maths NUMBER, amsd NUMBER, ai NUMBER)";
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        // Table exists
                    }
                }
            } catch (Exception e) {
                System.err.println("Database Initialization Error: " + e.getMessage());
            }
        }

        static List<StudentResult> getAllStudents() {
            List<StudentResult> list = new ArrayList<>();
            String sql = "SELECT * FROM student_results ORDER BY roll_no ASC";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(new StudentResult(
                        rs.getString("roll_no"), rs.getString("name"),
                        rs.getInt("java"), rs.getInt("dbms"), rs.getInt("maths"), 
                        rs.getInt("amsd"), rs.getInt("ai")
                    ));
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return list;
        }

        static StudentResult getStudent(String roll) {
            String sql = "SELECT * FROM student_results WHERE roll_no = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, roll);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new StudentResult(
                            rs.getString("roll_no"), rs.getString("name"),
                            rs.getInt("java"), rs.getInt("dbms"), rs.getInt("maths"), 
                            rs.getInt("amsd"), rs.getInt("ai")
                        );
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return null;
        }

        static void saveOrUpdateStudent(StudentResult s) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                String checkSql = "SELECT count(*) FROM student_results WHERE roll_no = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, s.rollNo);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            String updateSql = "UPDATE student_results SET name=?, java=?, dbms=?, maths=?, amsd=?, ai=? WHERE roll_no=?";
                            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                                pstmt.setString(1, s.name);
                                pstmt.setInt(2, s.m1); pstmt.setInt(3, s.m2); pstmt.setInt(4, s.m3);
                                pstmt.setInt(5, s.m4); pstmt.setInt(6, s.m5);
                                pstmt.setString(7, s.rollNo);
                                pstmt.executeUpdate();
                            }
                        } else {
                            String insertSql = "INSERT INTO student_results VALUES (?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                                pstmt.setString(1, s.rollNo);
                                pstmt.setString(2, s.name);
                                pstmt.setInt(3, s.m1); pstmt.setInt(4, s.m2); pstmt.setInt(5, s.m3);
                                pstmt.setInt(6, s.m4); pstmt.setInt(7, s.m5);
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // --- INNER CLASSES ---
    static class Faculty {
        String username, password, subject;
        Faculty(String u, String p, String s) { this.username = u; this.password = p; this.subject = s; }
    }

    static class StudentResult {
        String rollNo, name;
        int m1, m2, m3, m4, m5;

        public StudentResult(String rollNo, String name, int m1, int m2, int m3, int m4, int m5) {
            this.rollNo = rollNo; this.name = name; 
            this.m1 = m1; this.m2 = m2; this.m3 = m3; this.m4 = m4; this.m5 = m5;
        }
        double getAvg() { return (m1 + m2 + m3 + m4 + m5) / 5.0; }
        String getGrade() {
            double avg = getAvg();
            if (avg >= 90) return "O (Outstanding)";
            if (avg >= 80) return "A+";
            if (avg >= 70) return "A";
            if (avg >= 60) return "B";
            return "C";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentResultManagementApp().setVisible(true));
    }
}