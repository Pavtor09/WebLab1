package ru.Lab1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;


public class JsonConverter {
    public String SerializeToJson(DataObj outputObj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(outputObj);
        return jsonString;
    }
}
