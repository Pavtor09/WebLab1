package ru.Lab1;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class JParser {
    public Map<String, String> JsonParser(String s) {
        Map<String, String> map = new LinkedHashMap<>();
        if (s == null || s.isEmpty()) return map;
        String[] pairs = s.replaceAll("}","").replaceAll("\\{","").split(",");
        for (String p : pairs) {
            String[] splitPair = p.replaceAll("\"","").trim().split(":");
            try
            {
                String k = splitPair[0];
                String v = splitPair[1];
                k = urlDecode(k);
                v = urlDecode(v);
                System.out.println(k+" "+v);
                if (!k.isEmpty()) map.put(k, v);
            }
            catch (Exception e)
            {
                return map;
            }

        }
        return map;
    }

    // Безопасное URL-декодирование
    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }
}
