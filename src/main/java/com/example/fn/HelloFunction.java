package com.example.fn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class RequestObject{ 
    public String name; 
    public String message; 
    @Override public String toString() { return String.format("Name: %s\nMessage: %s", name, message); }
}

// TODO: Lombok
class ResponseObject{ 
    public String name; public String message; public Long timestamp; 
    public ResponseObject(String name, String message, Long timestamp) { this.name=name; this.message =message; this.timestamp = timestamp; }
    @Override public String toString() { return String.format("Name: %s\nMessage: %s\nTime: %d\n", name, message, timestamp); }
}

public class HelloFunction {
	
//	ObjectMapper mapper = new ObjectMapper();
//    private String readData(InputStream s) {
//        return new BufferedReader(new InputStreamReader(s))
//                .lines().collect(Collectors.joining(" / "));
//    }
//    
//    private RequestObject getObjectFromString(String input) throws JsonMappingException, JsonProcessingException {
//    	RequestObject inputObject = mapper.readValue(input, RequestObject.class); 
//    	if(inputObject.name.isEmpty() || inputObject.message.isEmpty()) {
//    		throw new IllegalArgumentException("\"name\" or \"message\" field is missing");
//    	} else {
//    		return inputObject;
//    	}
//    }
    
//  Example accessing the raw Input & Output Events  
//	public OutputEvent handleRequest(InputEvent rawInput) throws JsonMappingException, JsonProcessingException {
//		String text = rawInput.consumeBody(this::readData);
//		String response = text;
//		try {
//			RequestObject ro = getObjectFromString(text);
//		} catch (Exception e) {
//			System.out.println(String.format("I tried to deserialize the provided object but got this error, %s", e.getMessage()));
//		}
//        OutputEvent out = OutputEvent.fromBytes(
//                response.getBytes(), // Data
//                OutputEvent.Status.Success, 
//                "text/plain"            // Content type
//            );
//    return out;
//	}

    public String handleRequest(RequestObject input) { 
        return new ResponseObject(input.name, input.message, System.currentTimeMillis()).toString();
    }
}