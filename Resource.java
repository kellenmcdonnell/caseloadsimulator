package com.patientkeeper;

import java.util.*;

public class Resource {

    private int id;
    private int position;
    private double workSpdModifier;

    private boolean out;

    private int[] casesDay;  // low, med, high, critical
    private int[] casesWeek; // low, med, high, critical

    private PriorityQueue<Case> queue;

    public Resource(int id, int position, double workSpdModifier) {
        this.id = id;
        this.position = position;
        this.workSpdModifier = workSpdModifier;
        out = false;
        casesDay = new int[] {0,0,0,0};
        casesWeek = new int[] {0,0,0,0};
        queue = new PriorityQueue<Case>();
    }

    public int getId() { return id; }

    public boolean isOut() { return out; }

    public void setOut(boolean isOut) { this.out = out; }

    public boolean isWorkingCrit() {
        if (queue.size() == 0) return false;
        return queue.peek().getPriority() == 3;
    }

    public boolean isElligible() { return !(out || isWorkingCrit()); }

    public int getNumCasesDay() {
        return Arrays.stream(casesDay).sum();
    }

    public int getNumCasesDay(int priority) {
        return casesDay[priority];
    }

    public int getNumCasesWeek() {
        return Arrays.stream(casesWeek).sum();
    }

    public int getNumCasesWeek(int priority) {
        return casesWeek[priority];
    }

    public void addCase(Case c) {
        queue.add(c);
        casesDay[c.getPriority()]++;
        casesWeek[c.getPriority()]++;
    }

    public int getQueueSize() { return queue.size(); }

    public void newDay() {
        casesDay = new int[] {0, 0, 0, 0};
        Case[] queueArray = new Case[queue.size()];
        queueArray = queue.toArray(queueArray);
        queue.clear();
        for (Case c : queueArray) {
            c.ageCase(16);
            queue.add(c);
        }
    }

    public void newWeek() {
        casesWeek = new int[] {0, 0, 0, 0};
        Case[] queueArray = new Case[queue.size()];
        queueArray = queue.toArray(queueArray);
        queue.clear();
        for (Case c : queueArray) {
            c.ageCase(64); // 2days + Post-Fri/Pre-Mon Off hours
            queue.add(c);
        }
    }


    public void work(double time) {
        double workTime = time * workSpdModifier;
        while (!out && queue.size() > 0 && workTime > 0) {
            Case c = queue.poll();
            workTime = c.workCase(workTime);
            if (workTime >= 0.0) {
                c.ageCase(time - (workTime / workSpdModifier));
                c.setClosed(true);
            }
            else {
                queue.add(c);
            }
        }

        // Age remaining cases
        for (Case c : queue) {
            c.ageCase(time);
        }
    }

    @Override
    public String toString() {
        String str = "Resource{" +
                "id=" + id +
                ", position=" + position +
                ", workSpdModifier=" + workSpdModifier +
                ", out=" + out +
                ", casesDay=" + Arrays.toString(casesDay) +
                ", casesWeek=" + Arrays.toString(casesWeek);
        Case[] cases = new Case[queue.size()];
        cases = queue.toArray(cases);
        Arrays.sort(cases);
        for (Case c : cases) {
            str += "\n  " + c + ", ";
        }
        return str += "}";
    }
}


