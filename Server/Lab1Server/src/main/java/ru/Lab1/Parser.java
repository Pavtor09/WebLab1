package ru.Lab1;

import com.fastcgi.FCGIInterface;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
public class Parser {
    public DataObj JsonParser(String s) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        DataObj inObj = mapper.readValue(s,DataObj.class);
       return inObj;
    }

    // Безопасное URL-декодирование
    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }
    public int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
    public byte[] readExact(int contentLength) throws IOException {
        byte[] buf = new byte[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = FCGIInterface.request.inStream.read(buf, off, contentLength - off);
            if (r < 0) break;
            off += r;
        }
        return buf;
    }




}
