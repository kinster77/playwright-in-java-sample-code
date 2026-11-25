package previousWorkings.previousworkings;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightAssertionsTest {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    Page page;

    @BeforeAll
    public static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpw"))
        );
        browserContext = browser.newContext();
        playwright.selectors().setTestIdAttribute("data-test");

    }

    @BeforeEach
    public void setUp() {
        page = browserContext.newPage();
    }

    /*@AfterEach
    void closeContext(){
        browserContext.close();
    }

    */
    @AfterAll
    static void tearDown(){
        browser.close();
        playwright.close();
    }


    @DisplayName("Making assertions about the contents of a field")
    @Nested
    class LocatingElementsUsingCss{

    @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com/contact");

        }

        @DisplayName("Checking the value of a field")
        @Test
        void fieldValues() {
        var firstNameField = page.getByLabel("First name");

        firstNameField.fill("Sarah-Jane");

        assertThat(firstNameField).hasValue("Sarah-Jane");
        assertThat(firstNameField).not().isDisabled();
        assertThat(firstNameField).isVisible();
        assertThat(firstNameField).isEditable();

        }
    }

    @DisplayName("Making assertions about data values")
    @Nested
    class MakingAssertionsAboutDataValues{

        @BeforeEach
        void openHomePage(){
            page.navigate("https://practicesoftwaretesting.com");
            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);
        }

        @Test
        void allProductPricesShouldBeCorrectValues(){
            List<Double> prices = page.getByTestId("product-price").allInnerTexts()
                    .stream()
                    .map (price -> Double.parseDouble(price.replace("$", "")))
                    .toList();

        Assertions.assertThat(prices)
                .isNotEmpty()
                .allMatch(price -> price > 0)
                .doesNotContain(0.0)
                .allMatch(price -> price < 1000)
                .allSatisfy(price -> Assertions.assertThat(price)
                        . isGreaterThan(0.0)
                        . isLessThan(1000.00));


        }

        @Test
        void shouldSortInAlphabeticalOrder(){
            page.getByLabel("sort").selectOption("Name (A - Z)");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByLabel("product-name").allTextContents();

            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.naturalOrder());
            Assertions.assertThat(productNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);



        }

        @Test
        void shouldSortInReverseAlphabeticalOrder(){
            page.getByLabel("sort").selectOption("Name (Z - A)");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByLabel("product-name").allTextContents();

            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.reverseOrder());




        }

    }
}
