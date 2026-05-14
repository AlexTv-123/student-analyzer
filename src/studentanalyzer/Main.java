package studentanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // figure out which CSV to load
        String path = "data/students_dataset.csv";   // default

        if (args.length > 0) {
            File f = new File(args[0]);
            if (f.exists()) {
                path = args[0];                       // use the provided path
            } else {
                System.out.println("File not found at: " + args[0]);
                System.out.println("Using default dataset instead.");
            }
        }

        // load data
        StudentAnalyzerImpl analyzer = new StudentAnalyzerImpl();
        analyzer.loadFromCSV(path);
        System.out.println("Loaded " + analyzer.getStudentCount() + " students.");

        Scanner input = new Scanner(System.in);

        //main menu loop
        while (true) {
            printMenu();
            String choice = input.nextLine().trim();

            if (choice.equals("1")) {
                runFeature1(analyzer, input);
            } else if (choice.equals("2")) {
                runFeature2(analyzer, input);
            } else if (choice.equals("3")) {
                runFeature3(analyzer, input);
            } else if (choice.equals("0")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        input.close();
    }


    // prints the main menu with short feature descriptions
    private static void printMenu() {
        System.out.println();
        System.out.println("=== Student Analyzer ===");
        System.out.println("1. Course difficulty  — average and median grade for a course");
        System.out.println("2. Student vs course  — compare a student's grade to the class average");
        System.out.println("3. GPA trend          — predict where a student's GPA is heading");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }


    //Feature 1: course difficulty
    private static void runFeature1(StudentAnalyzerImpl analyzer, Scanner input) {
        System.out.print("Enter course code (e.g. CS040): ");
        String code = input.nextLine().trim();

        double avg = analyzer.getAverageGradeForCourse(code);
        double med = analyzer.getMedianGradeForCourse(code);

        if (avg == 0.0) {
            System.out.println("No data for course " + code + " (course not found).");
            return;
        }

        System.out.println("Course " + code + ":");
        System.out.println("  average grade: " + String.format("%.2f", avg));
        System.out.println("  median grade:  " + String.format("%.2f", med));

        //also show top hardest/easiest for context
        System.out.println();
        System.out.println("Top 5 hardest courses (lowest average):");
        ArrayList<String> hardest = analyzer.getHardestCourses(5);
        for (int i = 0; i < hardest.size(); i++) {
            String c = hardest.get(i);
            System.out.println("  " + (i + 1) + ". " + c + " — average: " + String.format("%.2f", analyzer.getAverageGradeForCourse(c)));
        }

        System.out.println();
        System.out.println("Top 5 easiest courses (highest average):");
        ArrayList<String> easiest = analyzer.getEasiestCourses(5);
        for (int i = 0; i < easiest.size(); i++) {
            String c = easiest.get(i);
            System.out.println("  " + (i + 1) + ". " + c + " — average: " + String.format("%.2f", analyzer.getAverageGradeForCourse(c)));
        }
    }


    // Feature 2: student vs course
    private static void runFeature2(StudentAnalyzerImpl analyzer, Scanner input) {
        System.out.print("Enter student ID (e.g. S0001): ");
        String studentId = input.nextLine().trim();

        System.out.print("Enter course code (e.g. CS040): ");
        String courseCode = input.nextLine().trim();

        //check that the student exists and took this course
        ArrayList<String> taken = analyzer.getStudentCourses(studentId);
        if (taken.isEmpty()) {
            System.out.println("No data for student " + studentId);
            return;
        }
        if (!taken.contains(courseCode)) {
            System.out.println("Student " + studentId + " did not take course " + courseCode);
            return;
        }

        double diffAvg = analyzer.compareStudentToCourseAverage(studentId, courseCode);
        double diffMed = analyzer.compareStudentToCourseMedian(studentId, courseCode);

        //short status
        String status;
        if (diffAvg > 0) {
            status = "above average";
        } else if (diffAvg < 0) {
            status = "below average";
        } else {
            status = "exactly average";
        }

        System.out.println("Student " + studentId + " in course " + courseCode + ":");
        System.out.println("  diff from average: " + String.format("%.2f", diffAvg));
        System.out.println("  diff from median:  " + String.format("%.2f", diffMed));
        System.out.println("  status: " + status);
    }


    //Feature 3: GPA trend prediction
    private static void runFeature3(StudentAnalyzerImpl analyzer, Scanner input) {
        System.out.print("Enter student ID (e.g. S0001): ");
        String studentId = input.nextLine().trim();

        ArrayList<String> semesterGPAs = analyzer.getSemesterGPAList(studentId);
        if (semesterGPAs.isEmpty()) {
            System.out.println("No data for student " + studentId);
            return;
        }

        System.out.println("GPA by semester for " + studentId + ":");
        for (int i = 0; i < semesterGPAs.size(); i++) {
            System.out.println("  " + semesterGPAs.get(i));
        }

        System.out.println("Overall trend: " + analyzer.predictGPATrend(studentId));

        System.out.println("Trends by category:");
        HashMap<String, String> categoryTrends = analyzer.predictCategoryTrends(studentId);
        ArrayList<String> categories = new ArrayList<>(categoryTrends.keySet());
        Collections.sort(categories);
        for (int i = 0; i < categories.size(); i++) {
            String cat = categories.get(i);
            System.out.println("  " + cat + ": " + categoryTrends.get(cat));
        }
    }
}