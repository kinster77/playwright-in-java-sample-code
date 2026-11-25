package myfirstplaywrightpackage.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import myfirstplaywrightpackage.domain.ProductSummary;

import java.util.List;

public class ProductList {
    private final Page page;

    public ProductList(Page page){
        this.page = page;
    }

    public List<String> getProductNames() {
        return page.getByTestId("product-name").allInnerTexts();
    }

    public List<ProductSummary> getProductSummaries(){
        return page.locator(".card").all()
                .stream()
                .map(productCard -> {
                    String productName = productCard.getByTestId("product-name").textContent().strip();
                    String productPrice = productCard.getByTestId("product-price").textContent();
                    return new ProductSummary(productName, productPrice);
                }).toList();
    }


    public List<String> getProductNames(int expectedCount) {
        page.waitForCondition(() -> page.getByTestId("product-name").count() == expectedCount);
        return page.getByTestId("product-name").allInnerTexts();
    }

    @Step ("View product details")
    public void viewProductDetails(String productName) {
        page.locator(".card").getByText(productName).click();
    }

    public String getSearchCompletedMessage(){
        return page.getByTestId("search_completed").textContent();
    }
}
