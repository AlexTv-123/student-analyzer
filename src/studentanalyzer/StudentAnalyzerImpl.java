package studentanalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Scanner;

// implementation of the StudentAnalyzer interface

public class StudentAnalyzerImpl implements StudentAnalyzer {

    // main data structure: student id -> Student object
    private HashMap<String, Student> students;

    //secondary structure for fast Feature 1: course code -> list of all grades for that course
    private HashMap<String, ArrayList<Double>> courseGrades;


    //constructor creates empty maps
    public StudentAnalyzerImpl() {
        this.students = new HashMap<>();
        this.courseGrades = new HashMap<>();
    }


    // data loading

    public void loadFromCSV(String filename) {
        // try-catch is needed because Scanner can throw FileNotFoundException
        try {


            Scanner scanner = new Scanner(new File(filename));
            //skip the header line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            // read each remaining line

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                //skip empty or broken lines
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                // each line is: student_id, year_level, semester, course_code, course_name, category, grade
                String studentId = parts[0].trim();
                String yearLevel = parts[1].trim();
                String semester = parts[2].trim();
                String courseCode = parts[3].trim();
                String courseName = parts[4].trim();
                String category = parts[5].trim();
                double grade = Double.parseDouble(parts[6].trim());

                //create a record
                CourseRecord record = new CourseRecord(studentId, yearLevel, semester,
                        courseCode, courseName, category, grade);

                // find the student or create a new one if it's the first time we see this id
                Student student = students.get(studentId);
                if (student == null) {
                    student = new Student(studentId);
                    students.put(studentId, student);
                }
                student.addRecord(record);

                //also add the grade to the secondary structure for the course
                ArrayList<Double> grades = courseGrades.get(courseCode);
                if (grades == null) {
                    grades = new ArrayList<>();
                    courseGrades.put(courseCode, grades);
                }
                grades.add(grade);


            }


            scanner.close();

        } catch (FileNotFoundException e) {
            //if the file is missing, just print an error
            System.out.println("Error: file not found at path " + filename);
        }
    }

    public int getStudentCount() {
        return students.size();
    }

    public ArrayList<String> getStudentCourses(String studentId) {
        ArrayList<String> result = new ArrayList<>();

        //if the student is not in the database, return an empty list
        Student student = students.get(studentId);
        if (student == null) {
            return result;
        }

        // copy all course codes from the student's records
        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            result.add(records.get(i).getCourseCode());
        }
        return result;
    }





    // Feature 1: course difficulty analysis

    public double getAverageGradeForCourse(String courseCode) {
        ArrayList<Double> grades = courseGrades.get(courseCode);

        //if the course doesn't exist or has no grades, return 0
        if (grades == null || grades.size() == 0) {
            return 0.0;
        }

        // sum all grades and divide by count
    
        double sum = 0;
        for (int i = 0; i < grades.size(); i++) {
            sum = sum + grades.get(i);
        }
        return sum / grades.size();
    }

    public double getMedianGradeForCourse(String courseCode) {
        ArrayList<Double> grades = courseGrades.get(courseCode);

        if (grades == null || grades.size() == 0) {
            return 0.0;
        }




        //copy the list so we don't change the original order

        ArrayList<Double> sorted = new ArrayList<>(grades);
        Collections.sort(sorted);

        int size = sorted.size();

        // if size is odd, the median is the middle element
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        }

        //if size is even, the median is the average of the two middle elements
        double middle1 = sorted.get(size / 2 - 1);
        double middle2 = sorted.get(size / 2);
        return (middle1 + middle2) / 2.0;



    }




    public ArrayList<String> getHardestCourses(int n) {
        //get all course codes from the secondary structure
        ArrayList<String> codes = new ArrayList<>(courseGrades.keySet());

        // sort codes by average grade, ascending (lowest = hardest first)
        Collections.sort(codes, new Comparator<String>() {
            public int compare(String a, String b) {
                double avgA = getAverageGradeForCourse(a);
                double avgB = getAverageGradeForCourse(b);
                return Double.compare(avgA, avgB);
            }
        });

        //return only the first n (or all if there are fewer)
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < n && i < codes.size(); i++) {
            result.add(codes.get(i));
        }
        return result;
    }





    public ArrayList<String> getEasiestCourses(int n) {
        ArrayList<String> codes = new ArrayList<>(courseGrades.keySet());

        //sort codes by average grade, descending (highest = easiest first)
        Collections.sort(codes, new Comparator<String>() {
            public int compare(String a, String b) {
                double avgA = getAverageGradeForCourse(a);
                double avgB = getAverageGradeForCourse(b);
                return Double.compare(avgB, avgA);  // b, a — reversed for descending order
            }
        });

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < n && i < codes.size(); i++) {
            result.add(codes.get(i));
        }
        return result;
    }


    // Feature 2: student vs course comparison

    public double compareStudentToCourseAverage(String studentId, String courseCode) {
        Student student = students.get(studentId);
        if (student == null) {
            return 0.0;
        }

        //find the student's grade for this specific course
        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getCourseCode().equals(courseCode)) {
                double studentGrade = records.get(i).getGrade();
                double courseAverage = getAverageGradeForCourse(courseCode);
                return studentGrade - courseAverage;
            }
        }

        //student did not take this course
        return 0.0;
    }

    public double compareStudentToCourseMedian(String studentId, String courseCode) {
        Student student = students.get(studentId);
        if (student == null) {
            return 0.0;
        }

        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getCourseCode().equals(courseCode)) {
                double studentGrade = records.get(i).getGrade();
                double courseMedian = getMedianGradeForCourse(courseCode);
                return studentGrade - courseMedian;
            }
        }

        return 0.0;
    }






    /// Feature 3: GPA trend prediction

    public String predictGPATrend(String studentId) {
        Student student = students.get(studentId);
        if (student == null) return "student not found";

        ArrayList<String> semesters = getStudentSemestersSorted(studentId);
        if (semesters.size() < 2) return "not enough data";

        //build the list of GPAs for each semester
        ArrayList<Double> gpas = new ArrayList<>();
        for (int i = 0; i < semesters.size(); i++) {
            gpas.add(getStudentSemesterGPA(studentId, semesters.get(i), ""));
        }

        return computeTrend(gpas);
    }

    public HashMap<String, String> predictCategoryTrends(String studentId) {
        HashMap<String, String> result = new HashMap<>();
        Student student = students.get(studentId);
        if (student == null) return result;

        ArrayList<String> semesters = getStudentSemestersSorted(studentId);

        // collect all unique categories the student has touched
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            String cat = records.get(i).getCategory();
            if (!categories.contains(cat)) categories.add(cat);
        }

        //for each category we build a per-semester GPA list and run the trend logic
        for (int c = 0; c < categories.size(); c++) {
            String category = categories.get(c);
            ArrayList<Double> gpas = new ArrayList<>();

            for (int i = 0; i < semesters.size(); i++) {
                double gpa = getStudentSemesterGPA(studentId, semesters.get(i), category);
                // include only semesters where the student actually took a course in this category
                if (gpa > 0) gpas.add(gpa);
            }

            //need at least 3 semesters with this category per the project plan
            if (gpas.size() < 3) {
                result.put(category, "not enough data");
            } else {
                result.put(category, computeTrend(gpas));
            }
        }

        return result;
    }

    public ArrayList<String> getSemesterGPAList(String studentId) {
        ArrayList<String> result = new ArrayList<>();
        Student student = students.get(studentId);
        if (student == null) return result;

        ArrayList<String> semesters = getStudentSemestersSorted(studentId);
        for (int i = 0; i < semesters.size(); i++) {
            String sem = semesters.get(i);
            double gpa = getStudentSemesterGPA(studentId, sem, "");
            result.add(sem + ": " + String.format("%.2f", gpa));
        }
        return result;
    }



//  helpers for Feature 3 

    //returns season order: Spring = 0, Fall = 1 (Spring comes before Fall in the same year)
    private int seasonOrder(String season) {
        if (season.equals("Spring")) return 0;
        return 1;
    }

    // compares two semester strings like "Fall_2022" and "Spring_2023" chronologically
    private int compareSemesters(String s1, String s2) {
        String[] p1 = s1.split("_");
        String[] p2 = s2.split("_");
        int year1 = Integer.parseInt(p1[1]);
        int year2 = Integer.parseInt(p2[1]);
        if (year1 != year2) return Integer.compare(year1, year2);
        //same year: Spring < Fall
        return Integer.compare(seasonOrder(p1[0]), seasonOrder(p2[0]));
    }

    //returns student's semesters sorted chronologically (no duplicates)
    private ArrayList<String> getStudentSemestersSorted(String studentId) {
        ArrayList<String> result = new ArrayList<>();
        Student student = students.get(studentId);
        if (student == null) return result;

        // collect unique semesters
        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            String sem = records.get(i).getSemester();
            if (!result.contains(sem)) {
                result.add(sem);
            }
        }

        // sort using our semester comparator
        Collections.sort(result, new Comparator<String>() {
            public int compare(String a, String b) {
                return compareSemesters(a, b);
            }
        });
        return result;
    }

    // calculates a student's GPA for one semester
    // if categoryFilter is empty (""), we take all courses. otherwise only the given category
    private double getStudentSemesterGPA(String studentId, String semester, String categoryFilter) {
        Student student = students.get(studentId);
        if (student == null) return 0.0;

        double sum = 0;
        int count = 0;
        ArrayList<CourseRecord> records = student.getRecords();
        for (int i = 0; i < records.size(); i++) {
            CourseRecord r = records.get(i);
            if (!r.getSemester().equals(semester)) continue;
            //if filter is not empty, skip courses from other categories
            if (!categoryFilter.isEmpty() && !r.getCategory().equals(categoryFilter)) continue;
            sum = sum + r.getGrade();
            count++;
        }

        if (count == 0) return 0.0;
        return sum / count;
    }

    //main trend logic - takes chronological list of GPAs and returns prediction string
    private String computeTrend(ArrayList<Double> gpas) {
        if (gpas.size() < 2) return "not enough data";

        // build the directions: 1 = up, -1 = down, 0 = equal
        ArrayList<Integer> directions = new ArrayList<>();
        for (int i = 1; i < gpas.size(); i++) {
            double diff = gpas.get(i) - gpas.get(i - 1);
            if (diff > 0) directions.add(1);
            else if (diff < 0) directions.add(-1);
            else directions.add(0);
        }

        //check if the last 3 changes are all in the same direction ( strong prediction if true)
        int n = directions.size();
        if (n >= 3) {
            int d1 = directions.get(n - 3);
            int d2 = directions.get(n - 2);
            int d3 = directions.get(n - 1);
            if (d1 == 1 && d2 == 1 && d3 == 1) return "very likely to grow";
            if (d1 == -1 && d2 == -1 && d3 == -1) return "very likely to decline";
        }

        // otherwise count ups vs downs overall, it would be our weak prediction
        int ups = 0;
        int downs = 0;
        for (int i = 0; i < directions.size(); i++) {
            if (directions.get(i) == 1) ups++;
            else if (directions.get(i) == -1) downs++;
        }

        if (ups > downs) return "probably going to grow";
        if (downs > ups) return "probably going to decline";
        return "stable / unclear";
    }



}
