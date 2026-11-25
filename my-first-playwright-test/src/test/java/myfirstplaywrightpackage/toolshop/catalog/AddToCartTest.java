package myfirstplaywrightpackage.toolshop.catalog;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.junit.UsePlaywright;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import myfirstplaywrightpackage.HeadlessChromeOptions;
import myfirstplaywrightpackage.toolshop.catalog.pageobjects.*;
import myfirstplaywrightpackage.toolshop.fixtures.TakeFinalScreenshot;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.List;

@DisplayName("Shopping Cart")
@Feature("Shopping Cart")
@UsePlaywright(HeadlessChromeOptions.class)
public class AddToCartTest implements TakeFinalScreenshot {
        SearchComponent searchComponent;
        ProductList productList;
        ProductDetails productDetails;
        NavBar navBar;
        CheckoutCart checkoutCart;

        @BeforeEach
        void setUp(Page page){
            searchComponent = new SearchComponent(page);
            productList = new ProductList(page);
            productDetails = new ProductDetails(page);
            navBar = new NavBar(page);
            checkoutCart = new CheckoutCart(page);
        }

        @BeforeEach
        void setupTrace(BrowserContext context){
            context.tracing().start(
                    new Tracing.StartOptions()
                            .setScreenshots(true)
                            .setSnapshots(true)
                            .setSources(true)

            );
        }

        @BeforeEach
        void openHomePage(Page page){
           navBar.justOpenHomePage();
        }

        @AfterEach
        void recordTrace(TestInfo testInfo, BrowserContext context){
            String traceName = testInfo.getDisplayName().replace(" ","-").toLowerCase();
            context.tracing().stop(
                    new Tracing.StopOptions()
                            .setPath(Paths.get("target/traces/trace-" + traceName + ".zip"))
            );
        }



    @Test
    @Story ("Checking out a product")
    @DisplayName("Checking out a single item")
    void whenCheckingOutASingleItem(){
        searchComponent.searchBy("pliers");

        productList.viewProductDetails("Combination Pliers");
        productDetails.increaseQuantityBy(2);
        productDetails.addToCart(3);
        navBar.openCart();

        List<CartLineItem> lineItems = checkoutCart.getLineItems();

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
    @Story ("Checking out a product")
    @DisplayName("Checking out multiple items")
    void whenCheckingOutMultipleItems(){
        // navBar.openHomePage();
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
        List<CartLineItem> lineItems = checkoutCart.getLineItems();
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



