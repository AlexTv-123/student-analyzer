package studentanalyzer;

//one record from the CSV: one student, one course, one semester, one grade

public class CourseRecord {

    // fields
    private String studentId;     // for example "S0001"
    private String yearLevel;     // Senior, Junior, etc.
    private String semester;      // Fall_2022, Spring_2023
    private String courseCode;    // CS040, ART010
    private String courseName;    // full course name
    private String category;      // CS, Math, Arts
    private double grade;         // grade on a 0.0–4.0 scale


    //constructor takes all fields and fills the object
    public CourseRecord(String studentId, String yearLevel, String semester,
                        String courseCode, String courseName, String category,
                        double grade) {
        this.studentId = studentId;
        this.yearLevel = yearLevel;
        this.semester = semester;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.category = category;
        this.grade = grade;
    }


    // getters — fields can be read from outside, but not changed

    public String getStudentId() {
        return studentId;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public String getSemester() {
        return semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCategory() {
        return category;
    }

    public double getGrade() {
        return grade;
    }


    //for clean printing to the console
    public String toString() {
        return studentId + " | " + semester + " | " + courseCode
               + " (" + courseName + ") | grade: " + grade;
    }
}