package studentanalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        // create the analyzer and load the data
        StudentAnalyzerImpl analyzer = new StudentAnalyzerImpl();
        analyzer.loadFromCSV("data/students_dataset.csv");

        // sanity check
        System.out.println("Loaded " + analyzer.getStudentCount() + " students.");
        System.out.println();


        //feature 1 demo

        //average and median for one specific course
        String courseToCheck = "CS040";
        System.out.println("Course " + courseToCheck + ":");
        System.out.println("  average grade: " + String.format("%.2f", analyzer.getAverageGradeForCourse(courseToCheck)));
        System.out.println("  median grade:  " + String.format("%.2f", analyzer.getMedianGradeForCourse(courseToCheck)));
        System.out.println();



        // top-5 hardest courses
        System.out.println("Top 5 hardest courses (lowest average):");
        ArrayList<String> hardest = analyzer.getHardestCourses(5);
        for (int i = 0; i < hardest.size(); i++) {
            String code = hardest.get(i);
            double avg = analyzer.getAverageGradeForCourse(code);
            System.out.println("  " + (i + 1) + ". " + code + " — average: " + String.format("%.2f", avg));
        }
        System.out.println();

        //top-5 easiest courses
        System.out.println("Top 5 easiest courses (highest average):");
        ArrayList<String> easiest = analyzer.getEasiestCourses(5);
        for (int i = 0; i < easiest.size(); i++) {
            String code = easiest.get(i);
            double avg = analyzer.getAverageGradeForCourse(code);
            System.out.println("  " + (i + 1) + ". " + code + " — average: " + String.format("%.2f", avg));
        
        
        }



        // Feature 2 demo


        System.out.println();
        String demoStudent = "S0001";
        System.out.println("Comparison for student " + demoStudent + " (first 5 courses):");

        ArrayList<String> coursesTaken = analyzer.getStudentCourses(demoStudent);
        int howMany = Math.min(5, coursesTaken.size());


        for (int i = 0; i < howMany; i++) {
            String code = coursesTaken.get(i);
            double diffAvg = analyzer.compareStudentToCourseAverage(demoStudent, code);
            double diffMed = analyzer.compareStudentToCourseMedian(demoStudent, code);

            //short status for readability
            String status;
            if (diffAvg > 0) {
                status = "above average";
            } else if (diffAvg < 0) {
                status = "below average";
            } else {
                status = "exactly average";
            }

            System.out.println("  " + code
                    + ": diff from avg = " + String.format("%.2f", diffAvg)
                    + ", diff from median = " + String.format("%.2f", diffMed)
                    + " (" + status + ")");
        }


        // feature 3 demo

        System.out.println();

        System.out.println("Feature 3: GPA trend prediction for student " + demoStudent);

        //list of GPAs by semester
        ArrayList<String> semesterGPAs = analyzer.getSemesterGPAList(demoStudent);
        System.out.println("GPA by semester:");
        for (int i = 0; i < semesterGPAs.size(); i++) {
            System.out.println("  " + semesterGPAs.get(i));
        }

        // Overall trend
        System.out.println("Overall trend: " + analyzer.predictGPATrend(demoStudent));

        // Trends by category
        System.out.println("Trends by category:");
        HashMap<String, String> categoryTrends = analyzer.predictCategoryTrends(demoStudent);
        ArrayList<String> categories = new ArrayList<>(categoryTrends.keySet());
        Collections.sort(categories);
        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            System.out.println("  " + cat + ": " + categoryTrends.get(cat));
        }
    }
}