package myfirstplaywrightpackage.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;

public class ProductDetails {
    private final Page page;

    public ProductDetails(Page page){
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
