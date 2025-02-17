package com.sobolev.spring;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Введите число дней: ");
        int n = sc.nextInt();
        sc.close();

        String s = countDate(n);

        System.out.println(s);
    }

    private static String countDate(int date){
        int month = date % 360;
        int year = date / 360;
        int day = month % 30;
        month /= 30;

        String yearName;
        String monthName;
        String dayName;

        if (year % 10 == 1 && year % 100 != 11) {
            yearName = "год";
        } else if ((year % 10 >= 2 && year % 10 <= 4) && !(year % 100 >= 12 && year % 100 <= 14)) {
            yearName = "года";
        } else {
            yearName = "лет";
        }

        if (month % 10 == 1 && month % 100 != 11) {
            monthName = "месяц";
        } else if (month % 10 >= 2 && month % 10 <= 4) {
            monthName = "месяца";
        } else {
            monthName = "месяцев";
        }

        if (day % 10 == 1 && day % 100 != 11) {
            dayName = "день";
        } else if ((day % 10 >= 2 && day % 10 <= 4) && !(day % 100 >= 12 && day % 100 <= 14)) {
            dayName = "дня";
        } else {
            dayName = "дней";
        }

        String result = year + " " + yearName + ", " + month + " " + monthName + ", " + day + " " + dayName;

        return result;
    }
}