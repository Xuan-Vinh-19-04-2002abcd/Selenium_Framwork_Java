package Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Config {
    private String browser;
    @JsonProperty("home_url")
    private String homeUrl;
    @JsonProperty("api_url")
    private String apiUrl;
    @JsonProperty("login_url")
    private String loginUrl;
    @JsonProperty("form_url")
    private String formUrl;
    @JsonProperty("bookstore_url")
    private String bookstoreUrl;
    @JsonProperty("profile_url")
    private String profileUrl;
    @JsonProperty("implicit.wait.seconds")
    private int implicitWaitSeconds;
    @JsonProperty("page.load.seconds")
    private int pageLoadSeconds;
    @JsonProperty("environment")
    private String environment;

}
