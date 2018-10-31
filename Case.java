package com.patientkeeper;

public class Case implements Comparable<Case>{

    private int id;
    private double age;
    private int priority; // 3 - Critical, 2 - High, 1 - Medium, 0 - Low
    private boolean closed;
    private boolean reqSLA;
    private boolean hotAcc;
    private boolean mgmtEsc;
    private boolean reqCommComp;
    private double workTimeInitial;
    private double workTimeRemaining;

    public Case(int id, int priority, double workTimeInitial, boolean hotAcc, boolean mgmtEsc) {
        this.id = id;
        age = 0.0;
        this.priority = priority;
        closed = false;
        reqSLA = true;
        this.hotAcc = hotAcc;
        this.mgmtEsc = mgmtEsc;
        reqCommComp = false;
        this.workTimeInitial = workTimeInitial;
        workTimeRemaining = workTimeInitial;
    }

    public int getId() { return id; }

    public double getAge() { return age; }

    public void ageCase(double time) { age += time; }

    public int getPriority() { return priority; }

    public boolean isClosed() { return closed; }

    public void setClosed(boolean closed) { this.closed = closed; }

    public double getWorkTimeInitial() { return workTimeInitial; }

    public double getWorkTimeRemaining() { return workTimeRemaining; }

    // Returns remaining work time if case completed, 0 if case not completed, and -1 if there are legitimately 0
    public double workCase(double time) {
        if(reqSLA) { reqSLA = false; }
        workTimeRemaining -= time;
        return -1*workTimeRemaining;
    }

    public int calcPtryCtgry() {
        if (priority == 3) { return 0; }
        else if (reqSLA) { return 1; }
        else if (mgmtEsc) { return 2; }
        else if (reqCommComp) { return 3; }
        else if (age >= 336) { return 4; } // Older than 14 days
        else if (priority == 2 && hotAcc) { return 5; }
        else if (priority == 2) { return 6; }
        else if (hotAcc) { return 7; }
        else if (priority == 1) { return 8; }
        else { return 9; }
    }

    public int compareTo(Case other) {
        return Integer.compare(calcPtryCtgry(), other.calcPtryCtgry());
    }

    @Override
    public String toString() {
        return "Case{" +
                "id=" + id +
                ", age=" + age +
                ", category=" + calcPtryCtgry() +
                ", priority=" + priority +
                ", closed=" + closed +
                ", reqSLA=" + reqSLA +
                ", hotAcc=" + hotAcc +
                ", mgmtEsc=" + mgmtEsc +
                ", reqCommComp=" + reqCommComp +
                ", workTimeInitial=" + workTimeInitial +
                ", workTimeRemaining=" + workTimeRemaining +
                '}';
    }
}
