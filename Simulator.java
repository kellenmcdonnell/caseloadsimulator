package com.patientkeeper;

public class Simulator {

    private int numSims;
    private int simLength;

    private Simulation simulation;

    private int formation;
    private double avgCasesPerStep;
    private double stdDevCasesPerStep;
    private double avgCaseTime;
    private double stdDevCaseTime;
    private double[] casePrtyProb;

    private double[] avgBacklog;
    private double[] avgTTC;
    private double[] varTTC;
    private double[][] avgPrtyTTC;
    private double[][] varPrtyTTC;

    public Simulator(int numSims, int simLength, double avgCasesPerStep, double stdDevCasesPerStep, double avgCaseTime, double stdDevCaseTime, double[] casePrtyProb, int formation ) {
        this.numSims = numSims;
        this.simLength = simLength;
        this.formation = formation;
        this.avgCasesPerStep = avgCasesPerStep;
        this.stdDevCasesPerStep = stdDevCasesPerStep;
        this.avgCaseTime = avgCaseTime;
        this.stdDevCaseTime = stdDevCaseTime;
        this.casePrtyProb = casePrtyProb;
        avgBacklog = new double[simLength];
        for (int i = 0; i < avgBacklog.length; i++) {
            avgBacklog[i] = 0.0;
        }
        avgTTC = new double[numSims];
        varTTC = new double[numSims];
        avgPrtyTTC = new double[4][numSims];
        varPrtyTTC = new double [4][numSims];
    }

    public void setNumSims(int numSims) {
        this.numSims = numSims;
    }

    public int getNumSims(int numSims) {
        return numSims;
    }

    public int getSimLength() {
        return simLength;
    }

    public void setSimLength(int simLength) {
        this.simLength = simLength;
    }

    public double getAvgCasesPerStep() {
        return avgCasesPerStep;
    }

    public void setAvgCasesPerStep(double avgCasesPerStep) {
        this.avgCasesPerStep = avgCasesPerStep;
    }

    public double getStdDevCasesPerStep() {
        return stdDevCasesPerStep;
    }

    public void setStdDevCasesPerStep(double stdDevCasesPerStep) {
        this.stdDevCasesPerStep = stdDevCasesPerStep;
    }

    public double getAvgCaseTime() {
        return avgCaseTime;
    }

    public void setAvgCaseTime(double avgCaseTime) {
        this.avgCaseTime = avgCaseTime;
    }

    public double getStdDevCaseTime() {
        return stdDevCaseTime;
    }

    public void setStdDevCaseTime(double stdDevCaseTime) {
        this.stdDevCaseTime = stdDevCaseTime;
    }

    public double[] getCasePrtyProb() {
        return casePrtyProb;
    }

    public void setCasePrtyProb(double[] casePrtyProb) {
        this.casePrtyProb = casePrtyProb;
    }

    public double[] getAvgBacklog() { return avgBacklog; }

    public void runSimulations() {
        for(int i = 0; i < numSims; i++) {
            simulation = new Simulation(simLength, avgCasesPerStep, stdDevCasesPerStep, avgCaseTime, stdDevCaseTime, casePrtyProb, formation);
            simulation.run();
            //String simBack = "Sim Backlog: [";
            //String avgBack = "Avg Backlog: [";
            for (int j = 0; j < simLength; j++) {
                avgBacklog[j] += (simulation.getBacklog()[j] + 0.0)/numSims;
                /*simBack += simulation.getBacklog()[j];
                avgBack += avgBacklog[j];
                if (!(j == simLength - 1)) {
                    simBack += ", ";
                    avgBack += ", ";
                }
                else {
                    simBack += "]";
                    avgBack += "]";
                }*/
            }
            //System.out.println(simBack);
            //System.out.println(avgBack);
            //System.out.println(simulation);
            avgTTC[i] = simulation.getAvgTimeToClose();
            varTTC[i] = simulation.getVarTimeToClose();
            for (int j = 0; j < 4; j++) {
                avgPrtyTTC[j][i] = simulation.getAvgTimeToClose(j);
                varPrtyTTC[j][i] = simulation.getVarTimeToClose(j);
            }
        }
    }

    public double getAvgCloseTime() {
        return new Statistics(avgTTC).getMean();
    }

    public double getAvgCloseTime(int priority) {
        return new Statistics(avgPrtyTTC[priority]).getMean();
    }

    public double getStdDevCloseTime() {
        return Math.sqrt(new Statistics(varTTC).getMean());
    }

    public double getStdDevCloseTime(int priority) {
        return Math.sqrt(new Statistics(varPrtyTTC[priority]).getMean());
    }

    public String toString() {
        String str = "";
        str += "Total Average Time to Close: " + new Statistics(avgTTC).getMean() + "\n";
        str += "Total Std Dev Time to Close: " + Math.sqrt(new Statistics(varTTC).getMean()) + "\n";

        str += "--LOW Average Time to Close: " + new Statistics(avgPrtyTTC[0]).getMean() + "\n";
        str += "--LOW Std Dev Time to Close: " + Math.sqrt(new Statistics(varPrtyTTC[0]).getMean()) + "\n";

        str += "--MED Average Time to Close: " + new Statistics(avgPrtyTTC[1]).getMean() + "\n";
        str += "--MED Std Dev Time to Close: " + Math.sqrt(new Statistics(varPrtyTTC[1]).getMean()) + "\n";

        str += "--HIGH Average Time to Close: " + new Statistics(avgPrtyTTC[2]).getMean() + "\n";
        str += "--HIGH Std Dev Time to Close: " + Math.sqrt(new Statistics(varPrtyTTC[2]).getMean()) + "\n";

        str += "--CRIT Average Time to Close: " + new Statistics(avgPrtyTTC[3]).getMean() + "\n";
        str += "--CRIT Std Dev Time to Close: " + Math.sqrt(new Statistics(varPrtyTTC[3]).getMean());

        return str;
    }

    public static void main(String[] args) {
        Simulator sim = new Simulator(100, 760, 0.991, 4.828, 3, 2, new double[] { 0.008, 0.521, 0.372, 0.098 }, 0);
        sim.runSimulations();
        System.out.println(sim);

        sim = new Simulator(100, 760, 0.991, 4.828, 3, 2, new double[] { 0.008, 0.521, 0.372, 0.098 }, 1);
        sim.runSimulations();
        System.out.println(sim);
    }
}
