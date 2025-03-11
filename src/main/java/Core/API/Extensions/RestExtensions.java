package Core.API.Extensions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONObject;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RestExtensions {


    public static void verifySchema(String responseContent, String pathFile) throws Exception {
        // Read schema file as String
        String schemaContent = new String(Files.readAllBytes(Paths.get(pathFile)));

        // Convert schemaContent and responseContent to JsonNode
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rawSchema = mapper.readTree(schemaContent);
        JsonNode responseJson = mapper.readTree(responseContent);

        // Validate JSON schema
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(rawSchema);

        ProcessingReport report = schema.validate(responseJson);
        assertThat("Schema validation failed: " + report, report.isSuccess(), is(true)); // Ensures validation passes
    }


    public static JSONObject convertToJsonObject(Response response) {
        return new JSONObject(response.asString());
    }

    public static void verifyStatusCodeOk(Response response) {
        assertThat("Expected status code 200 but got " + response.statusCode(), response.statusCode(), is(200));
    }

    public static void verifyStatusCodeCreated(Response response) {
        assertThat("Expected status code 201 but got " + response.statusCode(), response.statusCode(), is(201));
    }

    public static void verifyStatusCodeBadRequest(Response response) {
        assertThat("Expected status code 400 but got " + response.statusCode(), response.statusCode(), is(400));
    }

    public static void verifyStatusCodeUnauthorized(Response response) {
        assertThat("Expected status code 401 but got " + response.statusCode(), response.statusCode(), is(401));
    }

    public static void verifyStatusCodeForbidden(Response response) {
        assertThat("Expected status code 403 but got " + response.statusCode(), response.statusCode(), is(403));
    }

    public static void verifyStatusCodeInternalServerError(Response response) {
        assertThat("Expected status code 500 but got " + response.statusCode(), response.statusCode(), is(500));
    }

    public static void verifyStatusCodeNoContent(Response response) {
        assertThat("Expected status code 204 but got " + response.statusCode(), response.statusCode(), is(204));
    }
}