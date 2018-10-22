package me.arthurlins.simbot.core.tools;

import me.arthurlins.simbot.core.algo.DamerauLevenshtein;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TextProcessor {


    public static String processString(String string){
        string = string.replaceAll("[,.; ]","");
        string = abr(string);
        StringBuilder sb = new StringBuilder();
        char prev = 255;
        for (char ch : deAccent(string).toCharArray()) {
            if (ch != prev) {
                sb.append(ch);
                prev = ch;
            }
        }
        return  sb.toString().toLowerCase();
    }

    public static boolean compare(String s1, String s2, int t) {
        s1 = processString(s1.toLowerCase());
        s2 = processString(s2.toLowerCase());

        int cost = DamerauLevenshtein.compare(s1, s2);

        return cost <= t;

//        int[] costs = new int[s2.length() + 1];
//        for (int i = 0; i <= s1.length(); i++) {
//            int lastValue = i;
//            for (int j = 0; j <= s2.length(); j++) {
//                if (i == 0)
//                    costs[j] = j;
//                else {
//                    if (j > 0) {
//                        int newValue = costs[j - 1];
//                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
//                            newValue = Math.min(Math.min(newValue, lastValue),
//                                    costs[j]) + 1;
//                        costs[j - 1] = lastValue;
//                        lastValue = newValue;
//                    }
//                }
//            }
//            if (i > 0)
//                costs[s2.length()] = lastValue;
//        }
//        return costs[s2.length()] <= t;
    }

    private static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static boolean isValid(String str){
        return !(str.length() > 300 || (!str.contains(" ") && str.length() > 30));
    }

    private static String abr(String str) {
        HashMap<String, String> abreviations = new HashMap<>();
        abreviations.put("voce", "vc");
        abreviations.put("cade", "kd");
        abreviations.put("nao", "n");
        abreviations.put("obrigado", "obg");
        abreviations.put("obrigada", "obg");
        abreviations.put("talvez", "tlvz");
        abreviations.put("comigo", "cmg");
        abreviations.put("vezes","vzs");
        abreviations.put("com", "cm");
        abreviations.put("quem", "qm");
        abreviations.put("tudo", "td");
        abreviations.put("bem", "bm");
        abreviations.put("ola","oi");
        abreviations.put("eae", "oi");
        abreviations.put("entao", "ent");
        abreviations.put("oque", "oq");
        abreviations.put("o que","oq");
        abreviations.put("hoje","hj");
        String abr;
        for (String original : abreviations.keySet()){
            abr = abreviations.get(original);
            str = str.replace(original, abr);
        }
        return str;
    }



}
