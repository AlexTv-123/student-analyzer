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






    // Feature 3: stubs, will implement later

    public String predictGPATrend(String studentId) {
        return "not implemented yet";
    }

    public HashMap<String, String> predictCategoryTrends(String studentId) {
        return new HashMap<>();
    }

    public ArrayList<String> getSemesterGPAList(String studentId) {
        return new ArrayList<>();
    }







}
