package ru.Lab1;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.fastcgi.FCGIInterface;

public class Main {
    public static void main(String[] args) throws IOException {
        FCGIInterface fcgi = new FCGIInterface();
        System.out.println("Server is waiting for requests");
        System.setProperty("FCGI_PORT", "64548");
        while (fcgi.FCGIaccept() >= 0) {

            long t0 = System.nanoTime();


            Parser parser = new Parser();
            JsonError SendError = new JsonError();
            Properties props = FCGIInterface.request.params;
//            if (!props.getProperty("REQUEST_URI").equals("localhost:64546/calculate"))
//            {
//            SendError.writeJsonError(404,"Current URI is incorrect");
//            }
            String method = props.getProperty("REQUEST_METHOD");

            if (method == null) {

                SendError.writeJsonError(400, "Unsupported HTTP method: null");
                continue;
            }
            if (method.equals("GET")) {


                SendError.writeJsonError(405, "Unsupported HTTP method: GET");
                continue;
            }
                if (method.equals("POST")) {
                    // Обработка POST-запроса
                    String contentType = props.getProperty("CONTENT_TYPE");
                    if (contentType == null) {
                        SendError.writeJsonError(400, "Content-Type is null");
                        continue;
                    }
                    // проверяем что запрос в нужном формате
                    if (!contentType.startsWith("application/json")) {
                        SendError.writeJsonError(415, "Content-Type is not supported");
                        continue;
                    }
                    int contentLength = parser.parseIntOrDefault(props.getProperty("CONTENT_LENGTH"), 0);

                    // Читаем тело запроса ровно contentLength байт из FastCGI потока
                    byte[] body = parser.readExact(contentLength);


                    DataObj inputObj = null; // объект нашего запроса
                    // пытаемся спарсить
                    try
                    {
                    inputObj = parser.JsonParser(new String(body, StandardCharsets.UTF_8));
                    }
                    catch (Exception e)
                    {
                        SendError.writeJsonError(400,"Invalid Json");
                        continue;
                    }
                    //проверяем, что все необходимые поля заполнились
                    if (inputObj.CheckNull())
                    {
                        SendError.writeJsonError(422,"Could not parse incoming Json");
                        continue;
                    }
                    //проверяем попадание
                    inputObj.CalculateHit();

                    String JsAns;//строка в которой будет ответ json

                    //пытаемся конвертнуть объект в json
                    try
                    {
                        JsonConverter converter = new JsonConverter();
                        JsAns = converter.SerializeToJson(inputObj);
                    }
                    catch (Exception e)
                    {
                        SendError.writeJsonError(422,"Could not form an answer");
                        continue;
                    }

                    // отправляем наш json
                    JsonSend sendr = new JsonSend();
                    sendr.writeJsonOk(JsAns);
                }



        }


    }
}