package com.hjrpc;

import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) {
        System.out.println(args.length);
        Arrays.stream(args).forEach(System.out::println);
    }
}
