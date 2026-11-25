package myfirstplaywrightpackage.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import myfirstplaywrightpackage.toolshop.fixtures.ScreenshotManager;

public class NavBar {
    private final Page page;

    public NavBar(Page page){
        this.page = page;
    }

    @Step ("Open the shopping cart")
    public void openCart() {
        // could also have navigated via url
        page.getByTestId("nav-cart").click();
        ScreenshotManager.takeScreenshot(page, "Shopping cart");
    }

    @Step("Open home page")
    public void openHomePage() {
        // page.navigate("https://practicesoftwaretesting.com/"); Removed full reload
        page.getByTestId("nav-home").click();
        page.waitForSelector(".card-img-top");
        ScreenshotManager.takeScreenshot(page, "Home page");
    }

    @Step("Open home page")
    public void justOpenHomePage() {
        page.navigate("https://practicesoftwaretesting.com/");
        ScreenshotManager.takeScreenshot(page, "Home page");
    }
}

