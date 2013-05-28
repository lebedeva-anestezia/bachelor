package ru.ifmo.mailru.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Anastasia Lebedeva
 */
public class DictionaryGenerator {

    private static Map<Character, String> transliterationChar = new HashMap<>();

    {
        transliterationChar.put('а', "a");
        transliterationChar.put('б', "b");
        transliterationChar.put('в', "v");
        transliterationChar.put('г', "g");
        transliterationChar.put('д', "d");
        transliterationChar.put('е', "e");
        transliterationChar.put('ё', "yo");
        transliterationChar.put('ж', "zh");
        transliterationChar.put('з', "z");
        transliterationChar.put('и', "i");
        transliterationChar.put('й', "y");
        transliterationChar.put('к', "k");
        transliterationChar.put('л', "l");
        transliterationChar.put('м', "m");
        transliterationChar.put('н', "n");
        transliterationChar.put('о', "o");
        transliterationChar.put('п', "p");
        transliterationChar.put('р', "r");
        transliterationChar.put('с', "s");
        transliterationChar.put('т', "t");
        transliterationChar.put('у', "u");
        transliterationChar.put('ф', "f");
        transliterationChar.put('х', "h");
        transliterationChar.put('ц', "c");
        transliterationChar.put('ч', "ch");
        transliterationChar.put('ш', "sh");
        transliterationChar.put('щ', "sch");
        transliterationChar.put('ъ', "");
        transliterationChar.put('ы', "y");
        transliterationChar.put('ь', "");
        transliterationChar.put('э', "e");
        transliterationChar.put('ю', "yu");
        transliterationChar.put('я', "ya");
    }

    public static String transliterateRus(String rus) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rus.length(); i++) {
            builder.append(transliterationChar.get(rus.charAt(i)));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        DictionaryGenerator generator = new DictionaryGenerator();
        File rus = new File("src/main/resources/ru.txt");
        File trans = new File("src/main/resources/ru_trans.txt");
        try {
            Scanner scanner = new Scanner(rus, String.valueOf(Charset.forName("UTF-16LE")));
            PrintWriter printWriter = new PrintWriter(trans);
            while (scanner.hasNext()) {
                String s = scanner.nextLine();
                try {
                    String s1 = new String(s.getBytes(), "UTF-8");
                    String res = transliterateRus(s1);
                    printWriter.println(res);
                    System.out.println(s1 + " " + res);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
