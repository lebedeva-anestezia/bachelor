package ru.ifmo.mailru;

import org.junit.Test;
import ru.ifmo.mailru.core.Spider;
import ru.ifmo.mailru.core.WebURL;
import ru.ifmo.mailru.priority.EmptyPrioritization;
import ru.ifmo.mailru.priority.FICAPrioritization;
import ru.ifmo.mailru.priority.PageRankGetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Anastasia Lebedeva
 */
public class CompTest {

    @Test
    public void spiderRun() {
        WebURL url = new WebURL();
        try {
            url.setUri(new URI("http://pogoda.yandex.ru/saint-petersburg/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        PrintWriter pw1 = null;
        PrintWriter pw2 = null;
        try {
            pw1 = new PrintWriter(new File("output1.txt"));
            pw2 = new PrintWriter(new File("output2.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Set<WebURL> list1 = new HashSet<>();
        list1.add(url);

        Spider spiderSimple = new Spider(new EmptyPrioritization(), list1, pw1);
        Spider spiderFICA = new Spider(new FICAPrioritization(), list1, pw2);
        Thread thread1 = new Thread(spiderSimple);
        Thread thread2 = new Thread(spiderFICA);
        thread1.start();
        thread2.start();
        try {
            Thread.sleep(3600000);
            //Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread1.interrupt();
        thread2.interrupt();
        pw1.close();
        pw2.close();
    }

    @Test
    public void compareResults() {
        PageRankGetter rankGetter = new PageRankGetter();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("output1.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int count1 = 0;
        long pr1 = 0;

        while (scanner.hasNext()) {
            try {
                int pr = rankGetter.getPageRank(scanner.nextLine());
                pr1 += pr;
                count1++;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Mean PR bfs spider: " + (double) pr1 / count1);
        try {
            scanner = new Scanner(new File("output2.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int count2 = 0;
        long pr2 = 0;
        while (scanner.hasNext()) {
            try {
                int pr = rankGetter.getPageRank(scanner.nextLine());
                pr2 += pr;
                count2++;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Mean PR FICA spider: " + (double) pr2 / count2);
    }

}
