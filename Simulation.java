package com.patientkeeper;

import java.util.ArrayList;

public class Simulation {

    private Populator populator;
    private Manager manager;

    private int[] backlog;
    private int numTimeSteps;
    private double timePerStep;
    private int curStep;

    public Simulation(int numTimeSteps, double avgCasesPerStep, double stdDevCasesPerStep, double avgCaseTime, double stdDevCaseTime, double[] casePrtyProb, int formation ) {
        this.numTimeSteps = numTimeSteps;
        populator = new Populator(avgCasesPerStep, stdDevCasesPerStep, avgCaseTime, stdDevCaseTime, casePrtyProb);
        manager = new Manager(formation);
        backlog = new int[numTimeSteps];
        timePerStep = 1;
        curStep = 0;
    }

    public void newDay() {
        populator.newDay();
        manager.resetNumCasesDay();
        for (Resource r : manager.getResources()) {
            r.newDay();
        }
    }

    public void newWeek() {
        manager.resetNumCasesWeek();
        for (Resource r : manager.getResources()) {
            r.newWeek();
        }
    }

    public void advanceTimeStep() {
        if (timePerStep * curStep % 12 == 0) {
            newDay();
        }
        if (timePerStep * curStep % 60 == 0) {
            newWeek();
        }
        manager.addCases(populator.createCases());
        if (timePerStep * curStep % 12 == 0 || timePerStep * curStep % 12 >= 4) {
            manager.assignCases();
        }
        manager.workCases(timePerStep);
        backlog[curStep++] = manager.getBacklog();
    }

    public int[] getBacklog() { return backlog; }

    public double getAvgTimeToClose() {
        return new Statistics(manager.getClosedCases()).getMean();
    }

    public double getAvgTimeToClose(int priority) {
        return new Statistics(manager.getClosedCases(priority)).getMean();
    }

    public double getVarTimeToClose() {
        return new Statistics(manager.getClosedCases()).getVariance();
    }

    public double getVarTimeToClose(int priority) {
        return new Statistics(manager.getClosedCases(priority)).getVariance();
    }

    public double getStdDevTimeToClose() {
        return new Statistics(manager.getClosedCases()).getStdDev();
    }

    public double getStdDevTimeToClose(int priority) {
        return new Statistics(manager.getClosedCases(priority)).getStdDev();
    }

    public void run() {
        run(false);
    }

    public void run(boolean print) {
        for (int i = 0; i < numTimeSteps; i++) {
            advanceTimeStep();
            //System.out.print(backlog[i] + ", ");
            if (print) {
                System.out.println(this);
                for (Case c : manager.getClosedCases()) {
                    System.out.println("CLOSED: "+ c);
                }
            }

        }
        /*if (print)  {
            System.out.println(manager.getClosedCases().size());
            for (Case c : manager.getClosedCases()) {
                System.out.println(c);
            }
         }*/
    }

    public String toString() {
        String str = "TimeStep: " + curStep + "-------------------------------\n";
        str += "Backlog: " + manager.getBacklog() + "\n";
        str += "Backlog[]: " + backlog[curStep-1] + "\n";
        for (Resource r : manager.getResources()) {
            str += (r) + "\n";
        }
        return str;
    }

    public static void main(String[] args) {
        Simulation sim = new Simulation(10, 0.991, 4.828, 3, 2, new double[] { 0.008, 0.521, 0.372, 0.098 }, 1);
        sim.run(true);

        String str = "";
        str += "Total Average Time to Close: " + sim.getAvgTimeToClose() + "\n";
        str += "Total Std Dev Time to Close: " + sim.getStdDevTimeToClose() + "\n";

        str += "--LOW Average Time to Close: " + sim.getAvgTimeToClose(0) + "\n";
        str += "--LOW Std Dev Time to Close: " + sim.getStdDevTimeToClose(0) + "\n";

        str += "--MED Average Time to Close: " + sim.getAvgTimeToClose(1) + "\n";
        str += "--MED Std Dev Time to Close: " + sim.getStdDevTimeToClose(1) + "\n";

        str += "--HIGH Average Time to Close: " + sim.getAvgTimeToClose(2) + "\n";
        str += "--HIGH Std Dev Time to Close: " + sim.getStdDevTimeToClose(2) + "\n";

        str += "--CRIT Average Time to Close: " + sim.getAvgTimeToClose(3) + "\n";
        str += "--CRIT Std Dev Time to Close: " + sim.getStdDevTimeToClose(3);

        /*System.out.println(str);
        str = "";
        for (int i = 0; i < 4320; i++) {
            str += sim.getBacklog()[i] + "\n";
        }
        System.out.println(str);*/
    }
}
