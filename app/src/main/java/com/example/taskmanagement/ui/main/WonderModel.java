package com.example.taskmanagement.ui.main;

import java.io.Serializable;

public class WonderModel implements Serializable {

    String title, Deadline_Date, assignedBy, Deadline_Time, description;

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

}

