package previousWorkings.previousworkings;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

public class PlaywrightLocatorsTest {

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext browserContext;
    Page page;

    @BeforeAll
    public static void setUpBrowser(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions","--disable-gpw"))
        );
        browserContext = browser.newContext();

    }
    @BeforeEach
    public void setUp() {
        page = browserContext.newPage();
    }

    @AfterAll
    static void tearDown(){
        browser.close();
        playwright.close();
    }

    @DisplayName("Locating elements by text nested Class")
    @Nested
    class LocatingElementsByText{

        @BeforeEach
        void openTheCatalogPage(){
            page.navigate("https://practicesoftwaretesting.com/");

        }

        @DisplayName("Locating an element by text contents")
        @Test
        void byText(){
            page.getByText("Bolt Cutters").click();

            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();

        }

        @DisplayName("Using alt text")
        @Test
        void byAltText(){
            page.getByAltText("Combination Pliers").click();

            PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();

        }

        @DisplayName("Using title")
        @Test
        void byTitle(){
            page.getByAltText("Combination Pliers").click();
            page.getByTitle("Practice Software Testing - Toolshop").click();
            

        }

    }
    @DisplayName("Locating elements using CSS")
    @Nested
    class LocatingElementsUsingCss{

        @BeforeEach
        void openContactPage(){
            page.navigate("https://practicesoftwaretesting.com/contact");

        }

        @DisplayName("By id")
        @Test
        void locateTheFirstNameFieldByID(){
            page.locator("#first_name").fill("Sarah-Jane");
            PlaywrightAssertions.assertThat(page.locator("#first_name")).hasValue("Sarah-Jane");
        }

        @DisplayName("By CSS class")
        @Test
        void locateTheSendButtonByCssClass(){
            page.locator("#first_name").fill("Sarah-Jane");
            page.locator(".btnSubmit").click();
            List<String> alertMessages = page.locator(".alert").allTextContents();
            Assertions.assertTrue(!alertMessages.isEmpty());
        }

        @DisplayName("By attribute")
        @Test
        void locateTheLastNameByAttribute(){
            page.locator("input[placeholder='Your last name *']").fill("Smith");
            PlaywrightAssertions.assertThat(page.locator("#last_name")).hasValue("Smith");

        }

    }
}
