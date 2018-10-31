package com.patientkeeper;

import java.util.ArrayList;

public class Manager {

    private ArrayList<Case> unassignedCases;
    private ArrayList<Case> assignedCases; // ArrayList vs HashMap?
    private Resource[] resources;

    private int curResource;

    private int numCasesDay;
    private int numCasesWeek;
    private int formation;

    public Manager(int formation) {
        unassignedCases = new ArrayList<Case>();
        assignedCases = new ArrayList<Case>(700); //based on estimated number of cases for 3 months
        curResource = 0;
        this.formation = formation;
        createResources(formation);
    }

    public void createResources(int formation) {
        switch(formation) {
            case 0:
                this.resources = new Resource[6];
                resources[0] = new Resource(0, 0, 0.85106383);
                resources[1] = new Resource(1, 0, 1.010638298);
                resources[2] = new Resource(2, 0, 1.138297872);
                resources[3] = new Resource(3, 0, 1.074468085);
                resources[4] = new Resource(4, 0, 0.914893617);
                resources[5] = new Resource(5, 0, 1.010638298);
                break;
            default:
                this.resources = new Resource[6];
                for (int i = 0; i < 6; i++ ) {
                    resources[i] = new Resource(i, 0, 1.0);
                }
        }
    }

    public Resource[] getResources() { return resources; }

    public void addCases(ArrayList<Case> cases) {
        this.unassignedCases.addAll(cases);
        numCasesDay += cases.size();
        numCasesWeek += cases.size();
    }

    public void assignCases() {
        if (unassignedCases.size() == 0) {
            return;
        }

        // Each Resource has gotten their cases
        if (curResource >= resources.length) {
            assignCasesOverflow();
        }
        boolean needsRecalc = !(curResource == 0);

        for (int i = curResource; i < resources.length; i++) {
            if (resources[i].isElligible()) {
                if (resources[i].getNumCasesWeek() < 10 && resources[i].getNumCasesDay() < 4) {
                    if (unassignedCases.get(0).getPriority() >= 2) {
                        if (resources[i].getNumCasesDay(3) + resources[i].getNumCasesDay(2) < 2) {
                            // One case object, references in assignedCases and resource queue.
                            // Potentially switch to maintaining hashmap of cases and giving each resource the id to interact with directly
                            Case c = unassignedCases.remove(0);
                            assignedCases.add(c);
                            resources[i].addCase(c);
                            break;
                        }
                    }
                    Case c = unassignedCases.remove(0);
                    assignedCases.add(c);
                    resources[i].addCase(c);
                    break;
                }
            }
            curResource++;
        }
        if (curResource == resources.length && needsRecalc) {
            curResource = 0;
        }
        assignCases();

    }

    public void assignCasesOverflow() {
        if (unassignedCases.size() == 0) {
            return;
        }
        Case c = unassignedCases.remove(0);
        assignedCases.add(c);
        resources[curResource++ % resources.length].addCase(c);
    }

    public int getNumCasesDay() { return numCasesDay; }

    public void resetNumCasesDay() { numCasesDay = 0; }

    public int getNumCasesWeek() { return numCasesWeek; }

    public void resetNumCasesWeek() { numCasesWeek = 0; }

    public ArrayList<Case> getClosedCases() {
        ArrayList<Case> closedCases = new ArrayList<Case>();
        for (Case c : assignedCases) {
            if (c.isClosed()) {
                closedCases.add(c);
            }
        }

        return closedCases;
    }

    public ArrayList<Case> getClosedCases(int priority) {
        ArrayList<Case> closedCases = new ArrayList<Case>();
        for (Case c : assignedCases) {
            if (c.isClosed() && c.getPriority() == priority) {
                closedCases.add(c);
            }
        }

        return closedCases;
    }

    public void workCases(double time) {
        for (Resource r : resources) {
            r.work(time);
        }
        for (Case c : unassignedCases) {
            c.ageCase(time);
        }
    }

    public int getBacklog() {
        int backlog = 0;
        for (Resource r : resources) {
            backlog += r.getQueueSize();
        }
        return backlog;
    }
}
