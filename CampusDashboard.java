import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;

class LoginScreen extends JFrame {
    private JComboBox<String> roleBox;

    public LoginScreen() {
        setTitle("Smart Campus - Login");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel title   = new JLabel("Smart Campus Management System");
        JLabel authors = new JLabel("By: Muhammad Talal Noor ");
        JLabel roleLabel = new JLabel("Select Your Role:");
        roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        JButton loginBtn = new JButton("Login");

        add(title); add(authors); add(roleLabel); add(roleBox); add(loginBtn);

        loginBtn.addActionListener(e -> {
            String role = (String) roleBox.getSelectedItem();
            dispose();
            new CampusDashboard(role).setVisible(true);
        });
        setVisible(true);
    }
}
interface Notifiable {
    void sendNotification();
}
interface Schedulable {
    void generateSchedule();
}
interface Reportable {
    String generateReport();
}
abstract class CampusEntity implements Serializable {
    protected String entityID;
    protected String name;
    protected String location;
    public static int totalStudents = 0;
    public static int totalCourses = 0;
    public static int totalFacilityUsage = 0;
    public CampusEntity(String id, String name, String location) {
        this.entityID = id;
        this.name = name;
        this.location = location;
    }
    public abstract double calculateOperationalCost();
    public String getEntityID() { return entityID; }
    public String getName()     { return name; }
    public String getLocation() { return location; }
}

abstract class AcademicUnit extends CampusEntity {
    public AcademicUnit(String id, String name, String loc) {
        super(id, name, loc);
    }
}
class Department extends AcademicUnit implements Reportable {
    private int numStudents;
    private int numEquipment;
    private ArrayList<Course> courses = new ArrayList<>();
    public Department(String id, String name, String loc, int students, int equip) {
        super(id, name, loc);
        this.numStudents = students;
        this.numEquipment = equip;
    }
    public void addCourse(Course c)       { courses.add(c); }
    public ArrayList<Course> getCourses() { return courses; }
    public int getNumStudents()           { return numStudents; }
    public int getNumEquipment()          { return numEquipment; }
    @Override
    public double calculateOperationalCost() {
        return numStudents * 500.0 + numEquipment * 200.0;
    }
    @Override
    public String generateReport() {
        return "DEPARTMENT REPORT\n"
                + "-----------------\n"
                + "Name     : " + name + "\n"
                + "Location : " + location + "\n"
                + "Students : " + numStudents + "\n"
                + "Equipment: " + numEquipment + "\n"
                + "Courses  : " + courses.size() + "\n"
                + "Op. Cost : $" +  calculateOperationalCost();
    }
    @Override
    public String toString() {
        return "Department | " + name + " | Location: " + location
                + " | Students: " + numStudents
                + " | Equipment: " + numEquipment
                + " | Courses: " + courses.size()
                + " | Cost: $ " + calculateOperationalCost();
    }
}

class Classroom extends AcademicUnit implements Schedulable {
    private boolean isAvailable = true;
    public Classroom(String id, String name, String loc) {
        super(id, name, loc);
    }
    public boolean isAvailable()           { return isAvailable; }
    public void setAvailable(boolean val)  { isAvailable = val; }
    @Override
    public double calculateOperationalCost() { return 2000.0; }
    @Override
    public void generateSchedule() {
        System.out.println("Schedule generated for: " + name);
    }
    @Override
    public String toString() {
        return "Classroom | " + name + " | Location: " + location
                + " | Available: " + isAvailable + " | Cost: $2000";
    }
}
class Lab extends AcademicUnit {
    public Lab(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() { return 5000.0; }
    @Override
    public String toString() {
        return "Lab | " + name + " | Location: " + location + " | Cost: $5000";
    }
}
abstract class Facility extends CampusEntity {
    public Facility(String id, String name, String loc) {
        super(id, name, loc);
    }
}
class Book implements Serializable {
    private String isbn;
    private String title;
    private String author;
    public Book(String isbn, String title, String author) {
        this.isbn   = isbn;
        this.title  = title;
        this.author = author;
    }
    public String getIsbn()   { return isbn; }
    public String getTitle()  { return title; }
    public String getAuthor() { return author; }
    @Override
    public String toString() {
        return "Book | " + title + " | Author: " + author + " | ISBN: " + isbn;
    }
}
class Library extends Facility implements Reportable {
    private ArrayList<Book> books = new ArrayList<>();
    public Library(String id, String name, String loc) {
        super(id, name, loc);
    }
    public void addBook(Book b)       { books.add(b); }
    public ArrayList<Book> getBooks() { return books; }
    @Override
    public double calculateOperationalCost() {
        return 500.0 + books.size() * 1.5;
    }
    @Override
    public String generateReport() {
        return "LIBRARY REPORT\n"
                + "--------------\n"
                + "Name          : " + name + "\n"
                + "Total Books   : " + books.size() + "\n"
                + "Facility Uses : " + CampusEntity.totalFacilityUsage + "\n"
                + "Op. Cost      : $" + calculateOperationalCost();
    }
    @Override
    public String toString() {
        return "Library | " + name + " | Location: " + location
                + " | Books: " + books.size()
                + " | Cost: $" + calculateOperationalCost();
    }
}
class Cafeteria extends Facility {
    private int dailyCustomers = 200;
    public Cafeteria(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() {
        return 1000.0 + dailyCustomers * 10.0;
    }
    @Override
    public String toString() {
        return "Cafeteria | " + name + " | Location: " + location
                + " | Customers/day: " + dailyCustomers
                + " | Cost: $" + calculateOperationalCost();
    }
}
class Hostel extends Facility {
    private int numRooms = 50;
    public Hostel(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() { return numRooms * 200.0; }
    @Override
    public String toString() {
        return "Hostel | " + name + " | Location: " + location
                + " | Rooms: " + numRooms
                + " | Cost: $" + calculateOperationalCost();
    }
}
abstract class ServiceUnit extends CampusEntity implements Notifiable {
    public ServiceUnit(String id, String name, String loc) {
        super(id, name, loc);
    }
}
class SecurityService extends ServiceUnit {
    private int numGuards = 10;
    public SecurityService(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() { return numGuards * 800.0; }
    @Override
    public void sendNotification() {
        JOptionPane.showMessageDialog(null, "SECURITY ALERT!\nIncident reported at: " + location + "\nAll guards on duty!",
                "Security Alert", JOptionPane.WARNING_MESSAGE);
    }
    @Override
    public String toString() {
        return "Security | " + name + " | Location: " + location
                + " | Guards: " + numGuards
                + " | Cost: $" + calculateOperationalCost();
    }
}
class HealthCenter extends ServiceUnit {
    private int numDoctors = 3;
    private int numNurses  = 5;
    public HealthCenter(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() {
        return numDoctors * 5000.0 + numNurses * 2000.0;
    }
    @Override
    public void sendNotification() {
        JOptionPane.showMessageDialog(null,
                "HEALTH EMERGENCY!\nPlease clear the area immediately.",
                "Health Center", JOptionPane.ERROR_MESSAGE);
    }
    @Override
    public String toString() {
        return "HealthCenter | " + name + " | Location: " + location
                + " | Doctors: " + numDoctors + " | Nurses: " + numNurses
                + " | Cost: $" + calculateOperationalCost();
    }
}
class AdminService extends ServiceUnit {
    private int numStaff = 8;
    public AdminService(String id, String name, String loc) {
        super(id, name, loc);
    }
    @Override
    public double calculateOperationalCost() { return numStaff * 3000.0; }
    @Override
    public void sendNotification() {
        JOptionPane.showMessageDialog(null, "ADMIN BROADCAST\nSystem maintenance tonight.\nPlease save your work.",
                "Administration", JOptionPane.INFORMATION_MESSAGE);
    }
    @Override
    public String toString() {
        return "Admin | " + name + " | Location: " + location
                + " | Staff: " + numStaff
                + " | Cost: $" + calculateOperationalCost();
    }
}
class TransportService extends ServiceUnit implements Schedulable {
    private int peakHours;
    public TransportService(String id, String name, String loc, int peakHours) {
        super(id, name, loc);
        this.peakHours = peakHours;
    }
    public int getPeakHours()        { return peakHours; }
    public void setPeakHours(int h)  { this.peakHours = h; }
    @Override
    public double calculateOperationalCost() { return 500.0 + peakHours * 150.0; }
    @Override
    public void generateSchedule() {
        System.out.println("Transport routes adjusted for " + peakHours + " peak hours.");
    }
    @Override
    public void sendNotification() {
        JOptionPane.showMessageDialog(null, "TRANSPORT UPDATE\nBus timing changed.\nPeak Hours: " + peakHours,
                "Transport Service", JOptionPane.INFORMATION_MESSAGE);
    }
    @Override
    public String toString() {
        return "Transport | " + name + " | Location: " + location
                + " | Peak Hours: " + peakHours
                + " | Cost: $" + calculateOperationalCost();
    }
}
class Student implements Serializable {
    String id;
    String name;
    String role;
    public Student(String id, String name, String role) {
        this.id   = id;
        this.name = name;
        this.role = role;
    }
    @Override
    public String toString() {
        return "Student | " + name + " | Roll No: " + id + " | Role: " + role;
    }
}
class Assignment implements Serializable {
    String title;
    int maxScore;
    public Assignment(String title, int maxScore) {
        this.title    = title;
        this.maxScore = maxScore;
    }
    @Override
    public String toString() {
        return "Assignment | " + title + " | Max Score: " + maxScore;
    }
}
class Course implements Serializable, Schedulable {
    String courseId;
    String title;
    int creditHours;
    String timing;
    String assignedRoom = "TBA";
    ArrayList<Student> students       = new ArrayList<>();
    ArrayList<Assignment> assignments = new ArrayList<>();
    public Course(String id, String title, int creditHours, String timing) {
        this.courseId    = id;
        this.title       = title;
        this.creditHours = creditHours;
        this.timing      = timing;
    }
    public void addStudent(Student s)       { students.add(s); }
    public void addAssignment(Assignment a) { assignments.add(a); }
    public void reschedule(String room)     { this.assignedRoom = room; }
    @Override
    public void generateSchedule() {
        System.out.println("Schedule generated for course: " + title);
    }
    @Override
    public String toString() {
        return "Course | " + title + " | ID: " + courseId
                + " | Credits: " + creditHours
                + " | Timing: " + timing
                + " | Students: " + students.size()
                + " | Room: " + assignedRoom;
    }
}
class CampusZone implements Serializable {
    String zoneName;
    ArrayList<Facility> facilities    = new ArrayList<>();
    ArrayList<ServiceUnit> services   = new ArrayList<>();
    public CampusZone(String name) { this.zoneName = name; }
    public void addFacility(Facility f)   { facilities.add(f); }
    public void addService(ServiceUnit s) { services.add(s); }
    @Override
    public String toString() {
        return "Zone: " + zoneName
                + " | Facilities: " + facilities.size()
                + " | Services: " + services.size();
    }
}
class CampusRepository<T> implements Serializable {
    private ArrayList<T> items = new ArrayList<>();
    public void add(T item) {
        items.add(item);
        if (item instanceof Student)  CampusEntity.totalStudents++;
        if (item instanceof Course)   CampusEntity.totalCourses++;
        if (item instanceof Facility) CampusEntity.totalFacilityUsage++;
    }
    public void remove(T item) {
        boolean removed = items.remove(item);
        if (removed) {
            if (item instanceof Student)  CampusEntity.totalStudents--;
            if (item instanceof Course)   CampusEntity.totalCourses--;
            if (item instanceof Facility) CampusEntity.totalFacilityUsage--;
        }
    }
    public ArrayList<T> getAll() { return items; }
    public int size()            { return items.size(); }
}
class FileManager {
    public static <T extends Serializable> void save(T obj, String fileName) {
        if (obj == null) return;
        try {
            ObjectOutputStream oOS = new ObjectOutputStream(new FileOutputStream(fileName));
            oOS.writeObject(obj);
            oOS.close();
        } catch (IOException e) {
            System.out.println("Failed to save file! " + e.getMessage());
        }
    }
    public static <T> T load(String fileName) {
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            System.out.println("File not available or empty!");
            return null;
        }
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(fileName));
            T object = (T) inp.readObject();
            inp.close();
            return object;
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found! " + e.getMessage());
            return null;
        }
    }
    public static <T> void autoSave(CampusRepository<T> rep, String fileName) {
        save(rep, fileName);
        System.out.println("Auto Saved! " + fileName);
    }
    public static <T> void backup(CampusRepository<T> rep, String fileName) {
        save(rep, fileName + "_backup");
        System.out.println("Backup saved to " + fileName + "_backup");
    }
}

public class CampusDashboard extends JFrame {

    private String currentRole;
    private CampusRepository<Student> studentRepo = new CampusRepository<>();
    private CampusRepository<Course> courseRepo = new CampusRepository<>();
    private CampusRepository<Facility> facilityRepo = new CampusRepository<>();
    private Library campusLibrary;
    private Department csDepartment;
    private CampusZone mainZone;
    private JLabel statusLabel;
    private DefaultTableModel studentModel;
    private DefaultTableModel courseModel;
    private DefaultTableModel facilityModel;
    private DefaultTableModel libraryModel;
private javax.swing.Timer autoSaveTimer;
    public CampusDashboard(String role) {
        this.currentRole = role;
        setTitle("Smart Campus Management System  —  Logged in as: " + role);
        setSize(1050, 680);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (autoSaveTimer != null) autoSaveTimer.stop();
                saveAll();
                System.exit(0);
            }
        });
        setLayout(new BorderLayout(5, 5));
        buildTopBar();
        loadData();
        buildCenterTabs();
        fillStudentTable(studentModel);
        fillCourseTable(courseModel);
        fillFacilityTable(facilityModel);
        fillLibraryTable(libraryModel);
        setLocationRelativeTo(null);
        startAutoSave();
    }

    private void saveAll() {
        FileManager.save(studentRepo, "students.dat");
        FileManager.save(courseRepo, "courses.dat");
        FileManager.save(facilityRepo, "facilities.dat");
        FileManager.save(campusLibrary, "library.dat");
        FileManager.save(csDepartment, "department.dat");
    }


    private void startAutoSave() {
        autoSaveTimer = new javax.swing.Timer(60000, e -> {
            FileManager.autoSave(studentRepo,  "students.dat");
            FileManager.autoSave(courseRepo,   "courses.dat");
            FileManager.autoSave(facilityRepo, "facilities.dat");
            FileManager.save(campusLibrary,    "library.dat");
            FileManager.save(csDepartment,     "department.dat");
            if (statusLabel != null)
                statusLabel.setText("Auto-saved  " + new java.util.Date() + buildStatusText());
        });
        autoSaveTimer.start();
    }

    private void buildTopBar() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        JLabel title = new JLabel("Smart Campus System   |   Role: " + currentRole);
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        top.add(title);
        statusLabel = new JLabel(buildStatusText());
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        top.add(statusLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            saveAll();
            dispose();
            new LoginScreen();
        });
        top.add(logoutBtn);
        add(top, BorderLayout.NORTH);
    }

    private String buildStatusText() {
        return "   |   Students: " + CampusEntity.totalStudents
                + "   Courses: " + CampusEntity.totalCourses
                + "   Facility Uses: " + CampusEntity.totalFacilityUsage;
    }
    private void refreshStatus() {
        if (statusLabel != null)
            statusLabel.setText(buildStatusText());
    }

    private void buildCenterTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Campus Map", buildMapPanel());
        tabs.addTab("Students", buildStudentPanel());
        tabs.addTab("Courses", buildCoursePanel());
        tabs.addTab("Departments", buildDepartmentPanel());
        tabs.addTab("Classrooms/Labs", buildClassroomLabPanel());
        tabs.addTab("Facilities", buildFacilityPanel());
        tabs.addTab("Services", buildServicePanel());
        tabs.addTab("Library", buildLibraryPanel());
        tabs.addTab("Timetable", buildTimetablePanel());
        tabs.addTab("Reports", buildReportsPanel());
        tabs.addTab("Notifications", buildNotificationsPanel());
        add(tabs, BorderLayout.CENTER);
    }
    private JPanel buildMapPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JPanel map = new JPanel(new GridLayout(3, 3, 10, 10));
        map.setBackground(new Color(100, 200, 30));

        String[] names  = {"Library", "Cafeteria", "Hostel", "Department", "Lab", "Medicalcenter", "Expo", "Old Faculty", "Sports"};

        String[] status = {"Active", "Busy", "Closed", "Busy", "Active", "Busy", "Closed", "Busy", "Active"};

        Color[] colour  = {
                new Color(46, 125, 50),
                new Color(230, 145, 56),
                new Color(183, 28, 28),
                new Color(230, 145, 56),
                new Color(46, 125, 50),
                new Color(230, 145, 56),
                new Color(183, 28, 28),
                new Color(230, 145, 56),
                new Color(46, 125, 50)
        };

        for (int i = 0; i < names.length; i++) {
            JButton j = new JButton(names[i] + "---" + status[i]);
            j.setBackground(colour[i]);
            j.setForeground(Color.WHITE);
            map.add(j);
        }

        p.add(map, BorderLayout.CENTER);
        return p;
    }
    private JPanel buildStudentPanel() {
        JPanel Panel = new JPanel(new BorderLayout(5, 5));
        String[] col = {"ID", "NAME", "ROLE ", "DETAILS"};
        studentModel = new DefaultTableModel(col, 0);
        JTable table = new JTable(studentModel) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField id = new JTextField();
        JTextField names = new JTextField();
        JComboBox<String> rolebox = new JComboBox<>(new String[]{"Student"});
        p.add(new JLabel(" Student id :")); p.add(id);
        p.add(new JLabel(" Name :"));      p.add(names);
        p.add(new JLabel(" Role :"));      p.add(rolebox);

        JPanel Btnpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton addBtn  = new JButton("Add Student");
        JButton DelBtn  = new JButton("DeleteSelected");
        JButton UpdBtn  = new JButton("Update ");
        JButton viewBtn = new JButton("View");
        Btnpanel.add(addBtn); Btnpanel.add(DelBtn); Btnpanel.add(UpdBtn); Btnpanel.add(viewBtn);

        addBtn.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            String ID = id.getText().trim();
            String Name = names.getText().trim();
            if (ID.isEmpty() || Name.isEmpty()) return;
            String role = (String) rolebox.getSelectedItem();
            studentRepo.add(new Student(ID, Name, role));
            FileManager.save(studentRepo, "students.dat");
            fillStudentTable(studentModel);
            refreshStatus();
        });
        DelBtn.addActionListener(e1 -> {
            if (!currentRole.equals("Admin")) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String selid = (String) studentModel.getValueAt(row, 0);
            studentRepo.getAll().removeIf(student -> student.id.equalsIgnoreCase(selid));
            CampusEntity.totalStudents = studentRepo.size();
            FileManager.save(studentRepo, "students.dat");
            fillStudentTable(studentModel);
            refreshStatus();
        });
        UpdBtn.addActionListener(e -> {
            if (currentRole.equalsIgnoreCase("Student")) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String selid = (String) studentModel.getValueAt(row, 0);
            String newName = names.getText().trim();
            for (Student s : studentRepo.getAll())
                if (s.id.equalsIgnoreCase(selid)) { s.name = newName; break; }
            FileManager.save(studentRepo, "students.dat");
            fillStudentTable(studentModel);
            refreshStatus();
        });
        viewBtn.addActionListener(e -> fillStudentTable(studentModel));

        JPanel top = new JPanel(new BorderLayout());
        top.add(p, BorderLayout.CENTER);
        top.add(Btnpanel, BorderLayout.SOUTH);
        Panel.add(top, BorderLayout.NORTH);
        Panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return Panel;
    }

    private void fillStudentTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Student s : studentRepo.getAll())
            model.addRow(new Object[]{s.id, s.name, s.role, s.toString()});
    }

    private JPanel buildCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] col = {"Course ID", "Title", "Credits", "Timing", "Students", "Details"};
        courseModel = new DefaultTableModel(col, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(courseModel);

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Course Details"));
        JTextField Id     = new JTextField();
        JTextField Title  = new JTextField();
        JTextField Credit = new JTextField("3");
        JTextField Timing = new JTextField("Mon 9:00-11:00");
        form.add(new JLabel(" Course ID:"));    form.add(Id);
        form.add(new JLabel(" Title:"));        form.add(Title);
        form.add(new JLabel(" Credit Hours:")); form.add(Credit);
        form.add(new JLabel(" Timing:"));       form.add(Timing);

        JPanel btnPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAdd    = new JButton("Add Course");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnUpdate = new JButton("Update Title");
        JButton btnView   = new JButton("View All");
        btnPanel.add(btnAdd); btnPanel.add(btnDelete); btnPanel.add(btnUpdate); btnPanel.add(btnView);

        btnAdd.addActionListener(e -> {
            if (currentRole.equalsIgnoreCase("Student")) return;
            String id = Id.getText().trim();
            String title = Title.getText().trim();
            if (id.isEmpty() || title.isEmpty()) return;
            int credit = 3;
            try { credit = Integer.parseInt(Credit.getText().trim()); } catch (NumberFormatException ex) {}
            Course c = new Course(id, title, credit, Timing.getText().trim());
            c.addAssignment(new Assignment("Assignment 1", 100));
            courseRepo.add(c);
            FileManager.save(courseRepo, "courses.dat");
            fillCourseTable(courseModel);
            refreshStatus();
        });
        btnDelete.addActionListener(e -> {
            if (!currentRole.equals("Admin")) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String selId = (String) courseModel.getValueAt(row, 0);
            courseRepo.getAll().removeIf(c -> c.courseId.equals(selId));
            CampusEntity.totalCourses = courseRepo.size();
            FileManager.save(courseRepo, "courses.dat");
            fillCourseTable(courseModel);
            refreshStatus();
        });
        btnUpdate.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String selId = (String) courseModel.getValueAt(row, 0);
            String newTitle = Title.getText().trim();
            if (newTitle.isEmpty()) return;
            for (Course c : courseRepo.getAll())
                if (c.courseId.equals(selId)) { c.title = newTitle; break; }
            FileManager.save(courseRepo, "courses.dat");
            fillCourseTable(courseModel);
        });
        btnView.addActionListener(e -> fillCourseTable(courseModel));

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fillCourseTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Course c : courseRepo.getAll())
            model.addRow(new Object[]{c.courseId, c.title, c.creditHours, c.timing, c.students.size(), c.toString()});
    }

    private JPanel buildDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"iD", " Name", " Location ", "students", " Equipment", "Courses", " Cost", "Details  "};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        JTextArea reportArea = new JTextArea(6, 40);
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createTitledBorder("===Department Report==="));

        JPanel btnPanel   = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton(" Refresh");
        JButton btnReport  = new JButton("   Generate Report");
        btnPanel.add(btnRefresh); btnPanel.add(btnReport);
        fillDeptTable(model);
        btnRefresh.addActionListener(e -> fillDeptTable(model));
        btnReport.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            if (csDepartment != null) reportArea.setText(csDepartment.generateReport());
        });

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(reportArea), BorderLayout.SOUTH);
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void fillDeptTable(DefaultTableModel model) {
        model.setRowCount(0);
        if (csDepartment != null)
            model.addRow(new Object[]{
                    csDepartment.getEntityID(), csDepartment.getName(), csDepartment.getLocation(),
                    csDepartment.getNumStudents(), csDepartment.getNumEquipment(),
                    csDepartment.getCourses().size(), csDepartment.calculateOperationalCost(), csDepartment.toString()
            });
    }

    private JPanel buildClassroomLabPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"Type ", " ID", " Name", " Location", " Status", " Details"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        Classroom[] classrooms = {
                new Classroom("RM101", "Hall A", "Block A"), new Classroom("RM102", "CS Lab Room", "Block B"),
                new Classroom("RM103", "Lecture Hall", "Block C"), new Classroom("RM104", "Seminar Room", "Block A"),
                new Classroom("RM105", "Tutorial Room", "Block C")};
        classrooms[1].setAvailable(false);
        Lab[] labs = {
                new Lab("LAB01", "Physics Lab", "Block D"), new Lab("LAB02", "Chemistry Lab", "Block E"),
                new Lab("LAB03", "Computer Lab", "Block F"), new Lab("LAB04", "Bio Lab", "Block D"),
                new Lab("LAB05", "Electronics Lab", "Block G")};
        for (Classroom c : classrooms)
            model.addRow(new Object[]{"Classroom", c.getEntityID(), c.getName(), c.getLocation(),
                    c.isAvailable() ? "Available" : "Unavailable", c.toString()});
        for (Lab l : labs)
            model.addRow(new Object[]{"Lab", l.getEntityID(), l.getName(), l.getLocation(), "Available", l.toString()});
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    private JPanel buildFacilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"Type", "ID", "Name", "Location", "Op. Cost", "Details"};
        facilityModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(facilityModel);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Add Facility"));
        JTextField fId   = new JTextField();
        JTextField fName = new JTextField();
        JTextField fLoc  = new JTextField();
        form.add(new JLabel(" ID:"));       form.add(fId);
        form.add(new JLabel(" Name:"));     form.add(fName);
        form.add(new JLabel(" Location:")); form.add(fLoc);

        JPanel btnPanel    = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> fType = new JComboBox<>(new String[]{"Library", "Cafeteria", "Hostel"});
        JButton btnAdd     = new JButton("Add Facility");
        JButton btnDelete  = new JButton("Delete Selected");
        JButton btnRefresh = new JButton("Refresh");
        btnPanel.add(new JLabel("Type:"));
        btnPanel.add(fType);
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);

        btnAdd.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            String id   = fId.getText().trim();
            String name = fName.getText().trim();
            String loc  = fLoc.getText().trim();
            if (id.isEmpty() || name.isEmpty() || loc.isEmpty()) return;
            String type = (String) fType.getSelectedItem();
            Facility f;
            if (type.equals("Library"))        f = new Library(id, name, loc);
            else if (type.equals("Cafeteria")) f = new Cafeteria(id, name, loc);
            else                               f = new Hostel(id, name, loc);
            facilityRepo.add(f);
            FileManager.save(facilityRepo, "facilities.dat");
            fillFacilityTable(facilityModel);
            refreshStatus();
            fId.setText(""); fName.setText(""); fLoc.setText("");
        });
        btnDelete.addActionListener(e -> {
            if (!currentRole.equals("Admin")) return;
            int row = table.getSelectedRow();
            if (row < 0) return;
            String selName = (String) facilityModel.getValueAt(row, 2);
            facilityRepo.getAll().removeIf(f -> f.getName().equals(selName));
            CampusEntity.totalFacilityUsage = facilityRepo.size();
            FileManager.save(facilityRepo, "facilities.dat");
            fillFacilityTable(facilityModel);
            refreshStatus();
        });
        btnRefresh.addActionListener(e -> fillFacilityTable(facilityModel));

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fillFacilityTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Facility f : facilityRepo.getAll())
            model.addRow(new Object[]{f.getClass().getSimpleName(), f.getEntityID(), f.getName(),
                    f.getLocation(), f.calculateOperationalCost(), f.toString()});
    }

    private JPanel buildServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"Type", "ID", "Name", "Location", "Op. Cost", "Details"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        ServiceUnit[] services = {
                new SecurityService("SEC01", "Campus Security", "Gate 1"),
                new HealthCenter("HC01", "Health Center", "Block D"),
                new AdminService("ADM01", "Administration", "Main Office"),
                new TransportService("TS01", "Campus Bus", "Depot", 8),
                new SecurityService("SEC02", "Night Security", "Gate 2")};
        for (ServiceUnit s : services)
            model.addRow(new Object[]{s.getClass().getSimpleName(), s.getEntityID(), s.getName(),
                    s.getLocation(), s.calculateOperationalCost(), s.toString()});
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLibraryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"ISBN", "Title", "Author", "Details"};
        libraryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(libraryModel);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Add Books"));
        JTextField Isbn   = new JTextField();
        JTextField Title  = new JTextField();
        JTextField Author = new JTextField();
        form.add(new JLabel("ISBN:"));   form.add(Isbn);
        form.add(new JLabel("Title:"));  form.add(Title);
        form.add(new JLabel("Author:")); form.add(Author);

        JPanel btnPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd    = new JButton("Add Book");
        JButton btnReport = new JButton("Library Report");
        JButton btnView   = new JButton("View All");
        btnPanel.add(btnAdd); btnPanel.add(btnReport); btnPanel.add(btnView);

        btnAdd.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            String isbn   = Isbn.getText().trim();
            String title  = Title.getText().trim();
            String author = Author.getText().trim();
            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()) return;
            campusLibrary.addBook(new Book(isbn, title, author));
            CampusEntity.totalFacilityUsage++;
            FileManager.save(campusLibrary, "library.dat");
            Isbn.setText(""); Title.setText(""); Author.setText("");
            fillLibraryTable(libraryModel);
            refreshStatus();
        });
        btnReport.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            JOptionPane.showMessageDialog(this, campusLibrary.generateReport(), "Library Report", JOptionPane.INFORMATION_MESSAGE);
        });
        btnView.addActionListener(e -> fillLibraryTable(libraryModel));

        JPanel topone = new JPanel(new BorderLayout());
        topone.add(form, BorderLayout.CENTER);
        topone.add(btnPanel, BorderLayout.SOUTH);
        panel.add(topone, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fillLibraryTable(DefaultTableModel model) {
        model.setRowCount(0);
        if (campusLibrary != null)
            for (Book b : campusLibrary.getBooks())
                model.addRow(new Object[]{b.getIsbn(), b.getTitle(), b.getAuthor(), b.toString()});
    }

    private JPanel buildTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        String[] cols = {"Course ID", "Title", "Credits", "Timing", "Assigned Room", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        JPanel btnPanel     = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnGenerate  = new JButton("  generate   schedule");
        JButton btnEmergency = new JButton("   Medical Emergency ");
        JButton btnTransport = new JButton("          Update Transport Peak Hours");
        btnPanel.add(btnGenerate); btnPanel.add(btnEmergency); btnPanel.add(btnTransport);

        btnGenerate.addActionListener(e -> {
            model.setRowCount(0);
            Classroom roomA = new Classroom("RM101", "Hall A", "Block A");
            Classroom roomB = new Classroom("RM102", "CS Lab", "Block B");
            roomB.setAvailable(false);
            boolean roomAUsed = false;
            for (Course c : courseRepo.getAll()) {
                c.generateSchedule();
                String room, status;
                if (!roomAUsed && roomA.isAvailable()) {
                    room = roomA.getName(); status = "Active"; roomAUsed = true; c.reschedule(roomA.getName());
                } else if (!roomB.isAvailable()) {
                    room = " GRound      Stage  Hall"; status = "Rescheduled  RM102 unavailable)"; c.reschedule("Ground stage hall ");
                } else {
                    room = roomB.getName(); status = "Active"; c.reschedule(roomB.getName());
                }
                model.addRow(new Object[]{c.courseId, c.title, c.creditHours, c.timing, room, status});
            }
        });
        btnEmergency.addActionListener(e -> {
            ServiceUnit sec    = new SecurityService("SEC01", "Security", "Gate 1");
            ServiceUnit health = new HealthCenter("HC01", "Health Center", "Block D");
            sec.sendNotification();
            health.sendNotification();
        });
        btnTransport.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter peak hours (1-24):", "8");
            if (input == null) return;
            try {
                int peak = Integer.parseInt(input.trim());
                TransportService ts = new TransportService("TS01", "Campus Bus", "Depot", peak);
                ts.generateSchedule();
                ts.sendNotification();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.");
            }
        });
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnFull   = new JButton(" Full Campus Report ");
        JButton btnCost   = new JButton("Operational Costs ");
        JButton btnBackup = new JButton("Backup Data");
        btnPanel.add(btnFull);
        btnPanel.add(btnCost);
        btnPanel.add(btnBackup);

        btnFull.addActionListener(e -> {
            String report = "=== SMART CAMPUS FULL REPORT ===\nGenerated: \n\n";
            report += csDepartment.generateReport() + "\n\n";
            report += campusLibrary.generateReport() + "\n\n";
            report += "--- STUDENTS -----------------------------\n";
            for (Student s : studentRepo.getAll()) report += s;
            report += "\n COURSES ---\n";
            for (Course c : courseRepo.getAll()) {
                report += c + "\n";
                for (Student s : c.students) report += s + "\n";
                for (Assignment a : c.assignments) report += "    " + a;
            }
            report += "\n======================--- STATIC COUNTERS ---=========================\n";
            report += "Total Students  : " + CampusEntity.totalStudents + "\n";
            report += "Total Courses   : " + CampusEntity.totalCourses + "\n";
            report += "Facility Uses   : " + CampusEntity.totalFacilityUsage + "\n";
            reportArea.setText(report);
        });
        btnCost.addActionListener(e -> {
            CampusEntity[] all = {
                    csDepartment, campusLibrary,
                    new Cafeteria("CAF01", "Main Cafeteria", "Block E"),
                    new Hostel("HOS01", "Hostel A", "Block F"),
                    new SecurityService("SEC01", "Security", "Gate 1"),
                    new HealthCenter("HC01", "Health Center", "Block D"),
                    new AdminService("ADM01", "Admin", "Office"),
                    new TransportService("TS01", "Transport", "Depot", 8),
                    new Classroom("RM01", "Hall A", "Block A"),
                    new Lab("LAB01", "Physics Lab", "Block D")
            };
            String report = "=== OPERATIONAL COSTS ===\n\n";
            double total  = 0;
            for (CampusEntity ent : all) {
                double cost = ent.calculateOperationalCost();
                report += ent.getClass().getSimpleName() + " (" + ent.getName() + ") : " + cost + "\n";
                total += cost;
            }
            report += "\nTOTAL CAMPUS COST: " + total;
            reportArea.setText(report);
        });
        btnBackup.addActionListener(e -> {
            if (currentRole.equals("Student")) return;
            FileManager.backup(studentRepo,  "students.dat");
            FileManager.backup(courseRepo,   "courses.dat");
            FileManager.backup(facilityRepo, "facilities.dat");
            FileManager.save(campusLibrary,  "library.dat");
            JOptionPane.showMessageDialog(this,
                    "Backup completed!\nFiles saved as *_backup",
                    "Backup", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildNotificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel info = new JLabel("  All buttons call sendNotification() ");
        panel.add(info, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(2, 2, 12, 12));
        Notifiable[] notifiables = {
                new SecurityService("SEC01", "Campus Security", "Gate 1"),
                new HealthCenter("HC01", "Health Center", "Block D"),
                new AdminService("ADM01", "Administration", "Main Office"),
                new TransportService("TS01", "Campus Bus", "Depot", 8)
        };
        String[] labels = {"Security Alert", "Health Emergency", "Admin Broadcast", "Transport Update"};
        for (int i = 0; i < notifiables.length; i++) {
            final Notifiable n = notifiables[i];
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.addActionListener(e -> n.sendNotification());
            grid.add(btn);
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        csDepartment = new Department("DEPT01", "Computer Science", "Block A", 150, 50);
        studentRepo  = FileManager.load("students.dat");
        if (studentRepo == null) studentRepo = new CampusRepository<>();
        courseRepo = FileManager.load("courses.dat");
        if (courseRepo == null) courseRepo = new CampusRepository<>();
        facilityRepo = FileManager.load("facilities.dat");
        if (facilityRepo == null) facilityRepo = new CampusRepository<>();
        campusLibrary = FileManager.load("library.dat");
        if (studentRepo.size() == 0 && courseRepo.size() == 0 && facilityRepo.size() == 0) {
            createSampleData();
        } else {
            for (Course c : courseRepo.getAll()) csDepartment.addCourse(c);
            CampusEntity.totalStudents = studentRepo.size();
            CampusEntity.totalCourses  = courseRepo.size();
        }
        refreshStatus();
    }

    private void createSampleData() {
        campusLibrary = new Library("LIB01", "Markazi Library", "Block B");
        campusLibrary.addBook(new Book("978-0", "Urdu Adab",             "Jameel Sahab"));
        campusLibrary.addBook(new Book("978-1", "Java Seekho",           "Sir Ahmed"));
        campusLibrary.addBook(new Book("978-2", "programming Ki Kahani", "Hashim"));
        campusLibrary.addBook(new Book("978-3", "Design Seekho",         "Ali"));
        campusLibrary.addBook(new Book("978-4", "Pakistan Studies",      "Hassan"));
        campusLibrary.addBook(new Book("978-5", "Data Structures",       "Bilal"));

        facilityRepo.add(campusLibrary);
        facilityRepo.add(new Cafeteria("CAF01", "Main Cafe",    "Block E"));
        facilityRepo.add(new Hostel("HOS01",    "boys Hostel",  "Block F"));
        facilityRepo.add(new Hostel("HOS02",    "girlsHostel",  "Block G"));
        facilityRepo.add(new Cafeteria("CAF02", "Chota Cafe",   "Block H"));

        Student s1 = new Student("S001", "Ali",   "Student");
        Student s2 = new Student("S002", "Ayesha","Student");
        Student s3 = new Student("S003", "Hamza", "Student");
        Student s4 = new Student("S004", "Zara",  "Student");
        Student s5 = new Student("S005", "Usman", "Student");
        studentRepo.add(s1); studentRepo.add(s2); studentRepo.add(s3);
        studentRepo.add(s4); studentRepo.add(s5);

        Course oop = new Course("CS101", "OOP",        3, "Mon 9-11");
        Course ds  = new Course("CS102", "DSA",        3, "Tue 9-11");
        Course db  = new Course("CS103", "Database",   3, "Wed 9-11");
        Course os  = new Course("CS104", "OS",         3, "Thu 9-11");
        Course cn  = new Course("CS105", "Networking", 3, "Fri 9-11");

        oop.addStudent(s1); oop.addStudent(s2); oop.addStudent(s3);
        ds.addStudent(s3);  ds.addStudent(s4);
        db.addStudent(s4);
        os.addStudent(s5);
        cn.addStudent(s1);  cn.addStudent(s5);

        oop.addAssignment(new Assignment("Inheritance Lab",      100));
        oop.addAssignment(new Assignment("Polymorphism Task",    100));
        oop.addAssignment(new Assignment("Interface Practice",   100));
        ds.addAssignment(new Assignment("Arrays Task",           100));
        ds.addAssignment(new Assignment("Stack Queue Task",      100));
        db.addAssignment(new Assignment("SQL Practice",          100));
        os.addAssignment(new Assignment("CPU Scheduling",        100));
        cn.addAssignment(new Assignment("Network Design",        100));

        courseRepo.add(oop); courseRepo.add(ds); courseRepo.add(db);
        courseRepo.add(os);  courseRepo.add(cn);
        csDepartment.addCourse(oop); csDepartment.addCourse(ds);
        csDepartment.addCourse(db);  csDepartment.addCourse(os);
        csDepartment.addCourse(cn);

        mainZone = new CampusZone("Main Area");
        mainZone.addFacility(campusLibrary);
        mainZone.addService(new SecurityService("SEC01", "Security", "Main Gate"));
        saveAll();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}