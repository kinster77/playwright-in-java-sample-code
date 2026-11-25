package myfirstplaywrightpackage.toolshop;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import myfirstplaywrightpackage.toolshop.fixtures.PlaywrightTestCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightRestAPITest extends PlaywrightTestCase {

    /*@BeforeEach
    public void openHomePage(){
        page.navigate("https://practicesoftwaretesting.com/");
    }

     */

    @DisplayName("Playwright allows us to mock out API responses")
    @Nested
    class MockingAPIResponses {

        @Test
        @DisplayName("When a search returns a single product")
        void whenASingleItemIsFound() {
            page.route("**/products/search", route -> {
                route.fulfill(
                        new Route.FulfillOptions()
                                .setBody(MockSearchResponses.RESPONSE_WITH_A_SINGLE_ENTRY)
                                .setStatus(200)
                );
            });
            page.navigate("https://practicesoftwaretesting.com/");
            page.getByPlaceholder("Search").fill("Pliers");
            page.getByPlaceholder("Search").press("Enter");
            assertThat(page.getByTestId("product-name")).hasCount(1);
            assertThat(page.getByTestId("product-name")).hasText("Super Pliers");
        }

        @Test
        @DisplayName("When a search returns a no products")
        void whenNoItemAreFound() {


            page.route("**/products/search", route -> {
                route.fulfill(
                        new Route.FulfillOptions()
                                .setBody(MockSearchResponses.RESPONSE_WITH_NO_ENTRIES)
                                .setStatus(200)
                );
            });
            page.navigate("https://practicesoftwaretesting.com/");
            page.getByPlaceholder("Search").fill("Pliers");
            page.getByPlaceholder("Search").press("Enter");
            assertThat(page.getByTestId("product-name")).hasCount(0);
            assertThat(page.getByTestId("no-results")).hasText("There are no products found.");
        }
    }

    @Nested
    class MakingAPICalls{
        record Product(String name, Double price, String searchQuery){}

        private static APIRequestContext requestContext;

        @BeforeAll
        public static void setRequestContext(){
            requestContext = playwright.get().request().newContext(
                    new APIRequest.NewContextOptions()
                            .setBaseURL("https://api.practicesoftwaretesting.com/")
                            .setExtraHTTPHeaders((new HashMap<>(){{
                                put("Accept", "application/json");
                            }}))
            );
        }

        @DisplayName("Check presence of known products")
        @ParameterizedTest(name = "Checking product {0}")
        @MethodSource("products")
        void checkKnownProduct (Product product){
            page.navigate("https://practicesoftwaretesting.com/");
            page.fill("[placeholder='Search']", product.searchQuery);
            page.click("button:has-text('Search')");
            // Check the product appears with the correct name and price
            Locator productCard = page.locator(".card")
                    .filter(
                      new Locator.FilterOptions()
                              .setHasText(product.name)
                              .setHasText(Double.toString(product.price))
                    );

            assertThat(productCard).isVisible();
        }

        static Stream<Product> products(){
            APIResponse response = requestContext.get("products?page=2");
            Assertions.assertThat(response.status()).isEqualTo(200);
            JsonObject jsonObject = new Gson().fromJson(response.text(), JsonObject.class);
            JsonArray data = jsonObject.getAsJsonArray("data");
            return data.asList().stream()
                    .map(jsonElement -> {
                        JsonObject productJson = jsonElement.getAsJsonObject();
                        String name = productJson.get("name").getAsString();
                        Double price = productJson.get("price").getAsDouble();
                        String searchQuery = name.replaceAll("(?i)\\b(with|the|and|of|for|a|an)\\b", "");
                        return new Product(name, price, searchQuery);
                    });

        }
    }
}
