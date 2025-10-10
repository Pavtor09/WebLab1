package ru.Lab1;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import com.fastcgi.*;
public class Main {
    public static void main(String[] args) throws IOException {
        // Создаём объект FastCGI интерфейса
        var fcgi = new FCGIInterface();

        // Основной цикл обработки FastCGI запросов
        while (fcgi.FCGIaccept() >= 0) {
            // Получаем параметры текущего запроса (переменные окружения FastCGI)
            Properties props = FCGIInterface.request.params;

            // Засекаем время начала обработки запроса (наносекунды)
            long t0 = System.nanoTime();

            // Получаем HTTP метод (GET, POST и т.д.)
            String method = props.getProperty("REQUEST_METHOD");
            if (method == null) {
                // Если метод не указан, возвращаем ошибку
                writeJsonError(400, "Unsupported HTTP method: null");
                continue; // Переходим к следующему запросу
            }

//            if (method.equals("GET")) {
//                // Обработка GET-запроса
//                String query = props.getProperty("QUERY_STRING");
//                if ("debug=1".equals(query)) {
//                    // Если в QUERY_STRING есть debug=1, выводим дамп параметров
//                    String dump = dumpProps(props);
//                    writeHtmlOk(echoPage(dump));
//                } else {
//                    // Иначе выводим простую приветственную страницу
//                    writeHtmlOk(getHelloPage());
//                }
//                continue;
//            }

            if (method.equals("POST")) {
                // Обработка POST-запроса
                String contentType = props.getProperty("CONTENT_TYPE");
                if (contentType == null) {
                    writeJsonError(400, "Content-Type is null");
                    continue;
                }
                // Проверяем, что Content-Type поддерживается
                if (!contentType.startsWith("application/x-www-form-urlencoded")) {
                    writeJsonError(415, "Content-Type is not supported");
                    continue;
                }

                // Получаем длину тела запроса
                int contentLength = parseIntOrDefault(props.getProperty("CONTENT_LENGTH"), 0);

                // Читаем тело запроса ровно contentLength байт из FastCGI потока
                byte[] body = readExact(contentLength);

                // Парсим тело как application/x-www-form-urlencoded в Map
                Map<String, String> form = parseUrlEncoded(new String(body, StandardCharsets.UTF_8));

                // Также подмешиваем параметры из QUERY_STRING, если они есть и не перекрывают тело
                String query = props.getProperty("QUERY_STRING");
                if (query != null && !query.isEmpty()) {
                    Map<String, String> queryMap = parseUrlEncoded(query);
                    queryMap.forEach(form::putIfAbsent);
                }

                // Получаем параметры x, y, R из формы
                String xStr = form.get("x");
                String yStr = form.get("y");
                String rStr = form.get("R");

                // Проверяем, что все параметры переданы
                if (xStr == null || yStr == null || rStr == null) {
                    writeJsonError(400, "x, y, R must be provided");
                    continue;
                }

                // Парсим параметры в числа с проверкой
                double x, y, R;
                try {
                    x = Double.parseDouble(xStr);
                } catch (NumberFormatException e) {
                    writeJsonError(400, "x must be a number");
                    continue;
                }
                try {
                    y = Double.parseDouble(yStr);
                } catch (NumberFormatException e) {
                    writeJsonError(400, "y must be a number");
                    continue;
                }
                try {
                    R = Double.parseDouble(rStr);
                } catch (NumberFormatException e) {
                    writeJsonError(400, "R must be a number");
                    continue;
                }

                // Проверяем попадание точки в круг радиуса R
                boolean hit = (x * x + y * y) <= (R * R);

                // Вычисляем время обработки в миллисекундах
                long elapsedMs = Math.max(0, (System.nanoTime() - t0) / 1_000_000);

                writeJsonOk(resultJson(x, y, R, hit, elapsedMs));

                continue;
            }

            // Если метод не поддерживается, возвращаем ошибку
            writeJsonError(405, "Unsupported HTTP method: " + method);
        }
    }

    // Читает ровно contentLength байт из FastCGI STDIN
    private static byte[] readExact(int contentLength) throws IOException {
        byte[] buf = new byte[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = FCGIInterface.request.inStream.read(buf, off, contentLength - off);
            if (r < 0) break; // неожиданное окончание
            off += r;
        }
        return buf;
    }

    // Парсит строку application/x-www-form-urlencoded в Map
    private static Map<String, String> parseUrlEncoded(String s) {
        Map<String, String> map = new LinkedHashMap<>();
        if (s == null || s.isEmpty()) return map;
        String[] pairs = s.split("&");
        for (String p : pairs) {
            int eq = p.indexOf('=');
            String k = eq >= 0 ? p.substring(0, eq) : p;
            String v = eq >= 0 ? p.substring(eq + 1) : "";
            k = urlDecode(k);
            v = urlDecode(v);
            if (!k.isEmpty()) map.put(k, v);
        }
        return map;
    }

    // Безопасное URL-декодирование
    private static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }

    // Формируем успешный JSON ответ с HTTP заголовками
    private static void writeJsonOk(String json) {
        System.out.print("Status: 200 OK\r\n");
        System.out.print("Content-Type: application/json; charset=utf-8\r\n");
        System.out.print("Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n");
        System.out.print("\r\n");
        System.out.print(json);
        System.out.flush();
    }

    // Формируем JSON ошибку с кодом и сообщением
    private static void writeJsonError(int code, String message) {
        String status = code + " " + httpStatusText(code);
        String json = "{\"error\":\"" + message + "\"}";
        System.out.print("Status: " + status + "\r\n");
        System.out.print("Content-Type: application/json; charset=utf-8\r\n");
        System.out.print("Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n");
        System.out.print("\r\n");
        System.out.print(json);
        System.out.flush();
    }

//    // Формируем успешный HTML ответ с HTTP заголовками
//    private static void writeHtmlOk(String html) {
//        System.out.print("Status: 200 OK\r\n");
//        System.out.print("Content-Type: text/html; charset=utf-8\r\n");
//        System.out.print("Content-Length: " + html.getBytes(StandardCharsets.UTF_8).length + "\r\n");
//        System.out.print("\r\n");
//        System.out.print(html);
//        System.out.flush();
//    }

    // Текстовые описания HTTP кодов
    private static String httpStatusText(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 415 -> "Unsupported Media Type";
            default -> "Error";
        };
    }

//    // Шаблон приветственной HTML страницы
//    private static String getHelloPage() {
//        return """
//                <!doctype html>
//                <html><head><meta charset=\"utf-8\"><title>FastCGI Hello</title></head>
//                <body>
//                <h1>FastCGI Java</h1>
//                <p>Отправьте POST с x, y, R (x-www-form-urlencoded). Параметр format=html вернёт HTML.</p>
//                </body></html>
//                """;
//    }
//
//    // Шаблон страницы с дампом параметров
//    private static String echoPage(String dump) {
//        return """
//                <!doctype html>
//                <html><head><meta charset=\"utf-8\"><title>Debug</title></head>
//                <body>
//                <h2>Request params</h2>
//                <pre>%s</pre>
//                </body></html>
//                """.formatted(dump);
//    }
//
//    // Шаблон HTML результата
//    private static String resultHtml(double x, double y, double R, boolean hit, long ms) {
//        return """
//                <!doctype html>
//                <html><head><meta charset=\"utf-8\"><title>Result</title></head>
//                <body>
//                  <h2>Result</h2>
//                  <ul>
//                    <li>x = %s</li>
//                    <li>y = %s</li>
//                    <li>R = %s</li>
//                    <li>hit = %s</li>
//                    <li>time_ms = %d</li>
//                  </ul>
//                </body></html>
//                """.formatted(x, y, R, hit, ms);
//    }

    // Формируем JSON результат
    private static String resultJson(double x, double y, double R, boolean hit, long ms) {
        return "{" +
                "\"x\":" + x + "," +
                "\"y\":" + y + "," +
                "\"R\":" + R + "," +
                "\"hit\":" + hit + "," +
                "\"time_ms\":" + ms +
                "}";
    }

//    // Формируем дамп параметров в текст
//    private static String dumpProps(Properties p) {
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<Object, Object> e : p.entrySet()) {
//            sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
//        }
//        return sb.toString();
//    }

//    // Экранирование HTML
//    private static String escapeHtml(String s) {
//        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
//
//    // Экранирование JSON строк
//    private static String escapeJson(String s) {
//        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
//    }

    // Парсинг int с дефолтом
    private static int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    // Проверка на попадание в область
    private boolean hitCheck()
    {
        return true;
    }
}