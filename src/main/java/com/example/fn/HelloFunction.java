package com.example.fn;

import java.util.List;
import java.util.stream.Collectors;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.StringPrivateKeySupplier;
import com.oracle.bmc.nosql.NosqlClient;
import com.oracle.bmc.nosql.requests.ListTablesRequest;

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
//
//class InputSupplier<T> implements Supplier<T> {
//    T inputStream;
//    public InputSupplier(T inputStream) { this.inputStream = inputStream; }
//    
//    @Override
//    public T get() {
//        return inputStream;
//    }
//}

public class HelloFunction {
//  Some auth stuff
    BasicAuthenticationDetailsProvider authProvider;
    StringPrivateKeySupplier keySupplier;
    
    String region, tenantId, userId, compartmentId, endpoint, fingerprint, keyPhrase;
    
    @FnConfiguration
    public void config(RuntimeContext ctx) {
        this.region = ctx.getConfigurationByKey("REGION").orElseThrow();
        this.tenantId = ctx.getConfigurationByKey("TENANT_ID").orElseThrow();
        this.userId = ctx.getConfigurationByKey("USER_ID").orElseThrow();
        this.endpoint = ctx.getConfigurationByKey("NOSQL_ENDPOINT").orElseThrow();
        this.compartmentId = ctx.getConfigurationByKey("COMPARTMENT_ID").orElseThrow();
        this.fingerprint = ctx.getConfigurationByKey("FINGERPRINT").orElseThrow();

        // Manually doing some manipulation to the private key, as fn doesn't like fn cf a <app> <keystring> (probably user error.. TODO)
        // Also passing in key phrase from env var not great
        this.keyPhrase = ctx.getConfigurationByKey("KEY_PHRASE").orElseThrow();
        String privateKey = new StringBuilder()
                .append("-----BEGIN RSA PRIVATE KEY-----\r\n")
                .append(ctx.getConfigurationByKey("PRIVATE_KEY").orElseThrow())
                .append("\r\n-----END RSA PRIVATE KEY-----")
                .toString();
        this.keySupplier = new StringPrivateKeySupplier(privateKey);
        
        
    }
    
    public HelloFunction() {    
//        keySupplier = new StringPrivateKeySupplier(privateKey);
        
//        InputStream is = getClass().getClassLoader().getResourceAsStream("oci_config");
//        System.out.println(String.format("Bytes available for read: %d", is.available()));
//        System.out.println(String.format("\n\nEnvironment Check:\n Region: %s\nTenantID: %s\nUserId: %s\nPrivate Key: %s\n\n", 
//                Region.fromRegionCode(region),
//                tenantId,
//                userId,
//                privateKey
//                ));
//        authProvider = SimpleAuthenticationDetailsProvider
//                .builder()
//                .region(Region.fromRegionCode(region))
//                .tenantId(tenantId)
//                .userId(userId)
//                .privateKeySupplier(keySupplier)
//                .build();
//        System.out.println(authProvider.getKeyId());
//        
//        System.out.println("Authbuilder Success");
    }
    
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
//    
    public List<String> handleRequest(RequestObject input) {
        authProvider = SimpleAuthenticationDetailsProvider
            .builder()
//            .region(Region.fromRegionCode(region))
            .region(Region.US_PHOENIX_1)
            .tenantId(tenantId)
            .userId(userId)
            .privateKeySupplier(keySupplier)
            .passPhrase(keyPhrase)
            .fingerprint(fingerprint)
            .build();
//      Trying to write the data to the nosql database
        NosqlClient dbClient = NosqlClient.builder().endpoint(endpoint).build(authProvider);
        ListTablesRequest request = ListTablesRequest.builder()
                .compartmentId(compartmentId)
                .build();
        dbClient.listTables(request).getTableCollection().getItems().forEach(i -> {
            System.out.println(i.getName());
        });
        return dbClient.listTables(request).getTableCollection().getItems().stream().map(i->i.getName()).collect(Collectors.toList());
        
        
        
//        SignatureProvider sp = new SignatureProvider();
//        //Create an handle to access the cloud service in the us-ashburn-1 region.
//        NoSQLHandleConfig config = new NoSQLHandleConfig(Region.US_ASHBURN_1);
//        config.setAuthorizationProvider(sp);
//        NoSQLHandle handle = NoSQLHandleFactory.createNoSQLHandle(config);
        
        
//        return new ResponseObject(input.name, input.message, System.currentTimeMillis()).toString();
    }

}