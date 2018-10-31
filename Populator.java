package com.patientkeeper;

import java.util.ArrayList;
import java.util.Random;

public class Populator {

    // Assumed Normal Distribution for case time, priority number of cases
    private double avgTime;
    private double stdDevTime;

    private double[] priorities; // [Probability low, Probability medium, Probability high, Probability critical]

    private double avgNumCases; //Expected Cases Created Per Time Step
    private double stdDevNumCases;

    private Random random;

    private int numCasesDay;
    private int dailyCaseNum;
    private double dailyTotal;
    private int totalCaseNum;

    // Simulation only tracks Working hours (12 hours a day, no weekends)
    public Populator( double avgNumCases, double stdDevNumCases, double avgTime, double stdDevTime, double[] priorities ) {
        this.avgNumCases = avgNumCases;
        this.stdDevNumCases = stdDevNumCases;
        this.avgTime = avgTime;
        this.stdDevTime = stdDevTime;
        this.priorities = priorities;
        random = new Random();
        totalCaseNum = 0;
        numCasesDay = 0;
        dailyCaseNum = 0;
        dailyTotal = 0.0;
    }

    // PDF from Case Data
    public int calcPriority() {
        double tmpPrty = random.nextDouble();
        double sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += priorities[i];
            if (tmpPrty < sum) {
                return i;
            }
        }
        return 3;
    }

    // Normal Distribution
    public double calcWorkTimeInitial() {
        return stdDevTime * random.nextGaussian() + avgTime;
    }

    // Uniform Random 50 - 50
    public boolean calcHotAcc() {
        return random.nextInt() % 2 == 0;
    }

    // Uniform Random 5 %
    public boolean calcMgmtEsc() {
        return random.nextDouble() > 0.95;
    }

    // Poisson Distribution
    public int calcNumCasesPoisson() {
        double L = Math.exp(avgNumCases);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return k - 1;
    }

    public void newDay() {
        numCasesDay = calcNumCasesDay();
        dailyCaseNum = 0;
        dailyTotal = 0.0;
    }

    // PDF from Case Data
    public int calcNumCasesDay() {
        int[] caseDstrbtn = new int[] { 0, 0, 1, 2, 1, 4, 11, 21, 17, 22, 17, 15, 10, 16, 14, 14, 7, 3, 2, 3, 4, 1, 0, 2, 1, 1};
        double caseDstrbtnProb = 0.0;
        double prob = random.nextDouble();
        for(int i = 0; i < 25; i++) {
            caseDstrbtnProb += caseDstrbtn[i]/189.0;
            if (prob <= caseDstrbtnProb) {
                return i;
            }
        }
        return 25; // Max number
    }

    public ArrayList<Case> createCases() {
        ArrayList<Case> cases = new ArrayList<Case>();
        dailyTotal +=  (numCasesDay/12.0 + .01); //Adjust for double rounding errors
        int numCases = (int)(dailyTotal - dailyCaseNum);
        for (int i = 0; i < numCases; i++) {
            Case c = new Case(totalCaseNum + i, calcPriority(), calcWorkTimeInitial(), calcHotAcc(), calcMgmtEsc());
            cases.add(c);
        }
        dailyCaseNum += cases.size();
        totalCaseNum += cases.size();
        return cases;
    }

    public int getTotalCaseNum() { return totalCaseNum; }

}
