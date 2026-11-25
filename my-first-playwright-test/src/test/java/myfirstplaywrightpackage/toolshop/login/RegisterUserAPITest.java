package myfirstplaywrightpackage.toolshop.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import myfirstplaywrightpackage.toolshop.domain.Address;
import myfirstplaywrightpackage.toolshop.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@UsePlaywright
public class RegisterUserAPITest {

    private APIRequestContext request;
    private Gson gson = new Gson();

    @BeforeEach
    void setup(Playwright playwright){
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com")
        );
    }

    @AfterEach
    void tearDown(){
        if(request !=null){
            request.dispose();
        }
    }

    @Test
    void should_register_user(){
        User validUser = User.randomUser();

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(validUser)
        );

        // assertThat(response.status()).isEqualTo(201);
        String responseBody = response.text();
        User createdUser = gson.fromJson(responseBody,User.class);
        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);


        assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Registration should return 201 created status code")
                    .isEqualTo(201);

            softly.assertThat(createdUser)
                    .as("Created user should match the specified user without the password")
                    .isEqualTo(validUser.withPassword(null));

            assertThat(responseObject.has("password"))
                    .as("No password shoud be returned")
                            .isFalse();

            softly.assertThat(responseObject.get("id").getAsString())
                    .as("Registered user should have an id")
                    .isNotEmpty();

            softly.assertThat(response.headers().get("content-type"))
                    .contains("application/json");
        });
    }

    @Test
    void first_name_is_mandatory(){

        User userWithNoName = new User(
                null,
                "Smith",
        new Address(
                "Street 1",
                "12",
                "City",
                "State",
                "Country",
                "1234AA"
        ),
                "0987654321",            // phone
                "1970-01-01",            // dob
                "SuperSecure@",       // password
                "john@doe.example"

        );

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(userWithNoName));

        assertSoftly(softly -> {
            softly.assertThat(response.status()).isEqualTo(422);

            JsonObject responseObject = gson.fromJson(response.text(), JsonObject.class);
            softly. assertThat(responseObject.has("first_name")).isTrue();
            String errorMessage = responseObject.get("first_name").getAsString();
            softly.assertThat(errorMessage).isEqualTo("The first name field is required.");
            softly. assertThat(responseObject.has("password")).isTrue();
            String errorMessage1 = responseObject.get("password").getAsString();
            softly.assertThat(errorMessage1).isEqualTo("The password field must contain at least one number.");
        });
    }

}
