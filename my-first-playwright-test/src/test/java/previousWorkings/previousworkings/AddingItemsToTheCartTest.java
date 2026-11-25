package previousWorkings.previousworkings;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import myfirstplaywrightpackage.HeadlessChromeOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class AddingItemsToTheCartTest {
      /*
   =====deleted after HeadlessChromeOptions was created
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browsercontext;

    Page page;

       */

   /*
   =====deleted after HeadlessChromeOptions was created
   @BeforeAll
    static void setUpBrowser(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        playwright.selectors().setTestIdAttribute("data-test");
    }



    @BeforeEach
    void setUp(){
        browsercontext = browser.newContext();
        page = browsercontext.newPage();
    }

    @AfterEach
    void closeContext(){
        browsercontext.close();
    }

    @AfterAll
    static void tearDown(){
        browser.close();
        playwright.close();
    }

    */

    @DisplayName("Search for pliers")
    @Test
    void searchForPliers(Page page){
        page.navigate("https://practicesoftwaretesting.com/");
        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        assertThat(page.locator(".card")).hasCount(4);
        List<String> productNames = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(productNames).allMatch(name -> name.contains("Pliers"));

        Locator outOfStockItem = page.locator(".card")
                .filter(new Locator.FilterOptions().setHasText("Out of stock"))
                .getByTestId("product-name");


        assertThat(outOfStockItem).hasCount(1);
        assertThat(outOfStockItem).hasText("Long Nose Pliers");
    }
}
