import Configurations.ConfigReader;
import Core.API.APIClient;
import Model.Config;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseApiTest {
    private static final Config config = ConfigReader.getConfiguration();
    protected static APIClient apiClient = new APIClient(config.getBookstoreUrl());

    @BeforeClass
    public static void setup() {
        apiClient.createRequest(apiClient.getBaseUrl())
                .addHeader("accept", "application/json");
    }

    @AfterClass
    public static void teardown() {
    }

    @Test
    public void getAllBooks() throws InterruptedException {
        System.out.println(apiClient.getBaseUrl());
        Response response = apiClient.executeGet();
        System.out.println(response.getStatusCode());
    }
}
