package ru.Lab1;

public enum JsonCodes {
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
