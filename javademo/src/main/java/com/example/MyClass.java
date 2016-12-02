package com.example;

public class MyClass {
    public static void main(String[] args){
        System.out.printf("hello world");
        Class cla = runnable.getClass();
        System.out.println("name:"+cla.getName());
        System.out.println("isAnonymousClass:"+cla.isAnonymousClass());

    }
    private static  Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
}
