package com.example.fn;

import java.util.HashMap;
import java.util.Map;

import com.fnproject.fn.api.FnConfiguration;
import com.fnproject.fn.api.RuntimeContext;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.StringPrivateKeySupplier;
import com.oracle.bmc.nosql.NosqlClient;
import com.oracle.bmc.nosql.model.UpdateRowDetails;
import com.oracle.bmc.nosql.requests.ListTablesRequest;
import com.oracle.bmc.nosql.requests.UpdateRowRequest;
import com.oracle.bmc.nosql.responses.UpdateRowResponse;

class RequestObject{ 
    public String name;
    public String message;
    
    @Override public String toString() { 
        return String.format("Name: %s\nMessage: %s", name, message);
    }
}

// TODO: Lombok
//class ResponseObject{ 
//    public String name;
//    public String message; public Long timestamp; 
//    
//    public ResponseObject(String name, String message, Long timestamp) { 
//        this.name = name;
//        this.message = message;
//        this.timestamp = timestamp; 
//    }
//    @Override public String toString() { return String.format("Name: %s\nMessage: %s\nTime: %d\n", name, message, timestamp); }
//}
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

public class ButtonProcessor {
//  Some auth stuff
    BasicAuthenticationDetailsProvider authProvider;
    StringPrivateKeySupplier keySupplier;
    
    String region, tenantId, userId, compartmentId, endpoint, fingerprint, keyPhrase, buttonProcessorTable;
    
    @FnConfiguration
    public void config(RuntimeContext ctx) {
        this.region = ctx.getConfigurationByKey("REGION").orElseThrow();
        this.tenantId = ctx.getConfigurationByKey("TENANT_ID").orElseThrow();
        this.userId = ctx.getConfigurationByKey("USER_ID").orElseThrow();
        this.endpoint = ctx.getConfigurationByKey("NOSQL_ENDPOINT").orElseThrow();
        this.compartmentId = ctx.getConfigurationByKey("COMPARTMENT_ID").orElseThrow();
        this.fingerprint = ctx.getConfigurationByKey("FINGERPRINT").orElseThrow();
        this.buttonProcessorTable = ctx.getConfigurationByKey("BUTTON_PROCESSOR_TABLE").orElseThrow();

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
    
    /**
     * Creates a new row in specified Oracle Cloud NoSQL DB
     * 
     * @param input RequestObject containing 'name' and 'message' to be saved to NoSql DB
     * @return opcRequestId The ID corresponding to the request created within oracle cloud
     */
    public String handleRequest(RequestObject input) {
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
        Map<String, Object> rowMap = buildRowFromInput(input); 
        UpdateRowDetails rowDetails = UpdateRowDetails
            .builder()
            .compartmentId(tenantId)
            .value(rowMap)
            .build();
        UpdateRowRequest rowRequest = UpdateRowRequest
            .builder()
            .tableNameOrId(buttonProcessorTable)
            .updateRowDetails(rowDetails)
            .build();
        UpdateRowResponse response = dbClient.updateRow(rowRequest);
        String opcRequestId = response.getOpcRequestId();
        return opcRequestId;
    }

    public Map<String, Object> buildRowFromInput(RequestObject input){
        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("name", input.name);
        rowMap.put("message", input.message);
        rowMap.put("timestamp", System.currentTimeMillis());
        return rowMap;
    }
}