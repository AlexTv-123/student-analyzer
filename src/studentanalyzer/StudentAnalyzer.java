package studentanalyzer;

import java.util.ArrayList;
import java.util.HashMap;

//describes all methods the implementation must provide

public interface StudentAnalyzer {

    // loads the CSV file into the data structure
    void loadFromCSV(String filename);

    //returns total number of students loaded
    int getStudentCount();


    // Feature 1: course difficulty analysis

    // average grade across all students who took this course
    double getAverageGradeForCourse(String courseCode);

    // median grade for the course
    double getMedianGradeForCourse(String courseCode);

    // top-N hardest courses (lowest average grade), sorted hardest first
    ArrayList<String> getHardestCourses(int n);

    // top-N easiest courses (highest average grade), sorted easiest first
    ArrayList<String> getEasiestCourses(int n);


    // Feature 2: student vs course comparison

    // difference between student's grade and the course average
    // positive = above average, negative = below average
    double compareStudentToCourseAverage(String studentId, String courseCode);

    //same but compared to the median
    double compareStudentToCourseMedian(String studentId, String courseCode);


    // Feature 3: GPA trend prediction

    // overall GPA trend across semesters
    //  "very likely to grow" or "probably going to decline"
    String predictGPATrend(String studentId);

    // for each category, predicts the trend. returns map: category -> trend description
    HashMap<String, String> predictCategoryTrends(String studentId);

    // formatted list of GPAs per semester
    // for example ["Fall_2022: 3.50", "Spring_2023: 3.30"]
    ArrayList<String> getSemesterGPAList(String studentId);
}