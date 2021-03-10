package com.eudycontreras.chartastic;

/**
 * Created by eudycontreras.
 */
public class Printer {

    private static void printName(String name, int times) {
        int start = 1;
        printName(name, start, times);
    }

    private static void printName(String name, int count, int times) {
        System.out.println(name + " " + count);
        if(count < times) {
            printName(name, count + 1);
        }
    }

    public static void main(String[] args) {
        String name = "Eddie";
        int times = 1000;
        printName(name, times);
    }
}
