package studentanalyzer;

import java.util.ArrayList;

// represents one student and all their course records

public class Student {

    // fields
    private String id;                          // for example "S0001"
    private ArrayList<CourseRecord> records;    // all courses this student took


    //constructor takes id and creates an empty list of records
    public Student(String id) {
        this.id = id;
        this.records = new ArrayList<>();
    }


    //adds a new record to the student's list
    public void addRecord(CourseRecord record) {
        records.add(record);
    }


    // getters

    public String getId() {
        return id;
    }

    public ArrayList<CourseRecord> getRecords() {
        return records;
    }


    //for clean printing to the console
    public String toString() {
        return "Student " + id + " (" + records.size() + " records)";
    }
}
