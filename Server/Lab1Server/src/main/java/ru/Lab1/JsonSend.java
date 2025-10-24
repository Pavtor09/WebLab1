package ru.Lab1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonSend {
    //отправка результата
    void writeJsonOk(String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        System.out.write("Status: 200 OK\r\n".getBytes(StandardCharsets.US_ASCII));
        System.out.write("Content-Type: application/json; charset=utf-8\r\n".getBytes(StandardCharsets.US_ASCII));
        System.out.write(("Content-Length: " + body.length + "\r\n").getBytes(StandardCharsets.US_ASCII));
        System.out.write("\r\n".getBytes(StandardCharsets.US_ASCII)); // Разделяем заголовки и тело
        System.out.write(body); // ВАЖНО: отправляем JSON
        System.out.flush();
    }
}
