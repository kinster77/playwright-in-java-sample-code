package myfirstplaywrightpackage.toolshop.catalog.pageobjects;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class SearchComponent {
    private final Page page;

    public SearchComponent(Page page) {
        this.page = page;
    }


    @Step ("Search for keyword")
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

    @Step ("Clear the search criteria")
    public void clearSearch() {
        page.waitForResponse(response -> response.url().contains("/products") && response.ok(), () -> page.getByTestId("search-reset").click());
    }

    public void filterBy(String filterName) {
        //{
        //  "page": "0",
        //  "q": "saw",
        //  "between": "price,1,100",
        //  "by_category": "01M57AQ8TD7FSY7WRBA0SJ7XXR",
        //  "is_rental": "false"
        //}

        page.waitForResponse(response -> response.url().contains("/products")
                && response.request().postData() != null
                && response.request().postData().contains("by_category")
                && response.status() == 200, () -> {
            page.getByLabel(filterName).click();
        });
    }

    public void sortBy(String sortFilter) {
        page.waitForResponse(response -> response.url().contains("/products")
                && response.request().postData() != null
                && response.request().postData().contains("sort")
                && response.status() == 200, () -> {
            page.getByTestId("sort").selectOption(sortFilter);
        });
    }
}
