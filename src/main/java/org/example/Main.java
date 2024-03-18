package org.example;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void atomicIncriment(String text) {
        switch (text.length()) {
            case 3:
                length3.getAndIncrement();
                break;
            case 4:
                length4.getAndIncrement();
                break;
            case 5:
                length5.getAndIncrement();
                break;
        }
    }

    public static AtomicInteger length3 = new AtomicInteger();
    public static AtomicInteger length4 = new AtomicInteger();
    public static AtomicInteger length5 = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        String[] texts = new String[100000];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("abc", 3 + random.nextInt(3));
        }

        Thread palindrome = new Thread(() -> {
            for (String text : texts) {
                if (text.equals(new StringBuilder(text).reverse().toString())) {
                    atomicIncriment(text);
                }
            }
        });

        palindrome.start();
        Thread oneChar = new Thread(() -> {
            for (String text : texts) {
                if (text.replaceAll(text.substring(1), "").length() == 0) {
                    atomicIncriment(text);
                }
            }
        });
        oneChar.start();
        Thread sortString = new Thread(() -> {
            for (String text : texts) {
                var chars = text.toCharArray();
                Arrays.sort(chars);
                if (text.equals(new String(chars))) {
                    atomicIncriment(text);
                }
            }
        });
        sortString.start();

        palindrome.join();
        oneChar.join();
        sortString.join();
        System.out.println(String.format("Красивых слов с длиной 3: %s шт", length3.get()));
        System.out.println(String.format("Красивых слов с длиной 4: %s шт", length4.get()));
        System.out.println(String.format("Красивых слов с длиной 5: %s шт", length5.get()));
    }
}