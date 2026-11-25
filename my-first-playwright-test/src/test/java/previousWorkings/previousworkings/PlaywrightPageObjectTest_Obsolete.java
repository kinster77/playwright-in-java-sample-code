package previousWorkings.previousworkings;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightPageObjectTest_Obsolete {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void openHomePage() {
        page.navigate("https://practicesoftwaretesting.com");
    }

    @DisplayName("Without Page Objects")
    @Test
    void withoutPageObjects(){
        page.waitForResponse(response -> response.url().contains("/products/search")
                && response.request().postData() !=null
                && response.request().postData().contains("tape")
                && response.status() == 200,
                () -> {
                    page.getByPlaceholder("Search").fill("tape");
                    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
                });
        List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
        Assertions.assertThat(matchingProducts).contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");

    }

    @DisplayName("With Page Objects")
    @Test
    void withPageObjects(){

        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);
        searchComponent.searchBy("tape");

        var matchingProducts = productList.getProductNames();
        Assertions.assertThat(matchingProducts).contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    class SearchComponent {
        private final Page page;

        SearchComponent(Page page) {
            this.page = page;
        }


        public void searchBy(String keyword) {
            page.waitForResponse(response -> response.url().contains("/products/search")
                            && response.request().postData() != null
                            && response.request().postData().contains(keyword)
                            && response.status() == 200,
                    () -> {
                        page.getByPlaceholder("Search").fill(keyword);
                        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
                    });
        }


    }

    class ProductList{
        private final Page page;

        ProductList(Page page){
            this.page = page;
        }

        public List<String> getProductNames() {
            return page.getByTestId("product-name").allInnerTexts();
        }

        public void viewProductDetails(String productName) {
            page.locator(".card").getByText(productName).click();
        }
    }

    class ProductDetails{
        private final Page page;

        ProductDetails(Page page){
            this.page = page;
        }

        public void increaseQuantityBy(int increment) {
            for (int i = 1; i <= increment; i++){
                page.getByTestId("increase-quantity").click();
            }
        }


        public void addToCart(int expectedCartTotal) {
            page.waitForResponse(response -> response.url().contains("/carts")
            && response.request().method().equals("POST") && response.ok(),
                    () -> page.getByText("Add to cart").click()
            );

            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals(String.valueOf(expectedCartTotal)));

        }

    }
    class NavBar{
        private final Page page;

        NavBar(Page page){
            this.page = page;
        }

        public void openCart() {
            // could also have navigated via url
            page.getByTestId("nav-cart").click();
        }

        public void openHomePage() {
            // page.navigate("https://practicesoftwaretesting.com/"); Removed full reload
            page.getByTestId("nav-home").click();
            page.waitForSelector(".card-img-top");
        }
    }
    record CartLineItem(String title, int quantity, double price, double total){}
    class CheckOutCart{
        private final Page page;

        CheckOutCart(Page page){
            this.page = page;
        }

        public List<CartLineItem> getLineItems() {
            page.locator("app-cart tbody tr").first().waitFor();

            return page.locator("app-cart tbody tr")
                    .all()
                    .stream()
                    .map(
                            row -> {
                                String title = trimmed(row.getByTestId("product-title").innerText());
                                int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                                double price = Double.parseDouble(price(row.getByTestId("product-price").innerText()));
                                double linePrice = Double.parseDouble(price(row.getByTestId("line-price").innerText()));
                                return new CartLineItem(title, quantity, price, linePrice);
                            }
                    )
                    .toList();
        }

            private String trimmed(String value){
                return value.strip().replaceAll("\u00A0","");
            }

        private String price(String value){
            return value.replace("$", "");
        }
    }


    @Nested
    class WhenAddingItemsToTheCart{

        SearchComponent searchComponent;
        ProductList productList;
        ProductDetails productDetails;
        NavBar navBar;
        CheckOutCart checkOutPage;

        @BeforeEach
        void setUp(){
            searchComponent = new SearchComponent(page);
            productList = new ProductList(page);
            productDetails = new ProductDetails(page);
            navBar = new NavBar(page);
            checkOutPage = new CheckOutCart(page);
        }
        @DisplayName("Without Page Objects")
        @Test
        void withoutPageObjects(){
            //Search for pliers
            page.waitForResponse(response -> response.url().contains("/products/search")
                            && response.request().postData() !=null
                            && response.request().postData().contains("pliers")
                            && response.status() == 200,
                    () -> {
                        page.getByPlaceholder("Search").fill("pliers");
                        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
                    });
            // show details page
            page.locator(".card").getByText("Combination Pliers").click();

            // increase cart quantity
            page.getByTestId("increase-quantity").click();
            page.getByTestId("increase-quantity").click();

            // add to the cart
            page.getByText("Add to cart").click();
            // waiting for cart to update
            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));
            //Open the cart
            page.getByTestId("nav-cart").click();
            //check cart contents
            assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
            assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
        }

        @Test
        void withPageObjects(){
            searchComponent.searchBy("pliers");

            productList.viewProductDetails("Combination Pliers");
            productDetails.increaseQuantityBy(2);
            productDetails.addToCart(3);
            navBar.openCart();

            List<CartLineItem> lineItems = checkOutPage.getLineItems();

            Assertions.assertThat(lineItems)
                    .hasSize(1)
                    .first()
                    .satisfies(item -> {
                        Assertions.assertThat(item.title()).contains("Combination Pliers");
                        Assertions.assertThat(item.quantity()).isEqualTo(3);
                        Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());

                    });
        }

        @Test
        void whenCheckingOutMultipleItems(){
            navBar.openHomePage();
            productList.viewProductDetails("Bolt Cutters");
            productDetails.increaseQuantityBy(2);
            productDetails.addToCart(3);
            // Now need to go back to the main screen - new method to open Homepage inside NavBar class
            navBar.openHomePage();
            // changed to slip joint pliers from Claw Hammer as Claw Hammer was not unique
            productList.viewProductDetails("Slip Joint Pliers");
            productDetails.addToCart(4);

           // Now open cart
            navBar.openCart();

            // Retrieve line items
            List<CartLineItem> lineItems = checkOutPage.getLineItems();
            // Assertions - 2 lines
            Assertions.assertThat(lineItems).hasSize(2);

            // Example assertions
            List<String> productNames = lineItems.stream().map(CartLineItem::title).toList();
            Assertions.assertThat(productNames).contains("Bolt Cutters", "Slip Joint Pliers");

            Assertions.assertThat(lineItems)
                    .allSatisfy(item -> {
                        Assertions.assertThat(item.quantity()).isGreaterThanOrEqualTo(1);
                        Assertions.assertThat(item.price()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isGreaterThan(0.0);
                        Assertions.assertThat(item.total()).isEqualTo(item.quantity() * item.price());

                    });
        }
    }


}
