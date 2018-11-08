package com.exp;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Data {

    public synchronized static void main(String[] args) {
        int i = 4;
        double d = 4.0;
        String s = "HackerRank ";

//        Scanner scan = new Scanner(System.in);
//
//        int a = scan.nextInt();
//        double b = scan.nextDouble();
//        scan.nextLine();
//        String msg = scan.nextLine();
//
//        System.out.println(i + a);
//        System.out.println(d + b);
//        System.out.println(s + msg);
//
//
//        "asdas".charAt(1);
//
//        scan.close();



        List<B> list = new ArrayList<B>();
        Data data = new Data();
        data.f(list);
    }

    public void f(List<? extends A> aList){
        System.out.println(aList.size());
    }


    class A{}

    class B extends A{}
}