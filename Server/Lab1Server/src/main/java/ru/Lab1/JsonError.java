package ru.Lab1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonError {
    public void writeJsonError(int code, String message) throws IOException {
        HttpCodes Codes = new HttpCodes();
        String status = code + " " + Codes.httpStatusText(code);
        String json = "{\"error\":\"" + message + "\"}";
        System.out.write(("Status: " + status + "\r\n").getBytes());
        System.out.write("Content-Type: application/json; charset=utf-8\r\n".getBytes());
        System.out.write(("Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n").getBytes());
        System.out.write("\r\n".getBytes());
        System.out.write(json.getBytes());
        System.out.flush();
    }
}
