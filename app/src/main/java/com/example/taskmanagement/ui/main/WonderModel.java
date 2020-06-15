package com.example.taskmanagement.ui.main;

import java.io.Serializable;
import java.net.URL;

public class WonderModel implements Serializable {

    String title, Deadline_Date, assignedBy, Deadline_Time, description,Created_Date,assignedTo,document_path;

    public String getTitle() {
        return title;
    }

    public String getassignedBy() {
        return assignedBy;
    }

    public String getDeadline_Date() {
        return Deadline_Date;
    }

    public String getDeadline_Time() {
        return Deadline_Time;
    }

    public String getDescription() {
        return description;
    }

    public String getCreated_Date() {
        return Created_Date;
    }

    public String getDocument_path() {
        return document_path;
    }

    public String getAssignedTo() {
        return assignedTo;
    }
}

