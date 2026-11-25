package myfirstplaywrightpackage.cucumber.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import myfirstplaywrightpackage.domain.ProductSummary;
import myfirstplaywrightpackage.toolshop.catalog.pageobjects.NavBar;
import myfirstplaywrightpackage.toolshop.catalog.pageobjects.ProductList;
import myfirstplaywrightpackage.toolshop.catalog.pageobjects.SearchComponent;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;


public class ProductCatalogStepDefinitions {
    NavBar navBar;
    SearchComponent searchComponent;
    ProductList productList;

    @Before
    public void setUpPageObject(){
        navBar = new NavBar(PlaywrightCucumberFixtures.getPage());
        searchComponent = new SearchComponent(PlaywrightCucumberFixtures.getPage());
        productList = new ProductList(PlaywrightCucumberFixtures.getPage());
    }

    @Given("Sally is on the home page")
    public void sally_is_on_the_home_page(){
        navBar.justOpenHomePage();
    }

    @When("she searches for {string}")
    public void she_searches_for(String searchTerm){
       searchComponent.searchBy(searchTerm);
    }

    @Then("the {string} product should be displayed")
    public void the_product_should_be_displayed(String productName){
        var matchingProducts = productList.getProductNames();
        Assertions.assertThat(matchingProducts).contains(productName);
    }
    @DataTableType
    public ProductSummary productSummaryRow(Map<String, String> productData) {
        return new ProductSummary(productData.get("Product"),productData.get("Price"));
    }

    @Then("the following products should be displayed:")
    public void theFollowingProductsShouldBeDisplayed(List<ProductSummary> expectedProductSummaries) {
        var matchingProducts = productList.getProductSummaries();
//        System.out.println(matchingProducts);
//        List<Map<String,String>> expectedProductData = expectedProducts.asMaps();
//        List<ProductSummary> expectedProductSummaries = expectedProductData.stream()
//                .map(productData -> new ProductSummary(
//                        productData.get("Product"),
//                        productData.get("Price")
//                )).toList();
        Assertions.assertThat(matchingProducts).containsExactlyInAnyOrderElementsOf(expectedProductSummaries);
    }

    @Then("no products should be displayed")
    public void noProductsShouldBeDisplayed() {
        List<ProductSummary> matchingProducts = productList.getProductSummaries();
        Assertions.assertThat(matchingProducts).isEmpty();
    }

    @And("the message {string} should be displayed")
    public void theMessageShouldBeDisplayed(String messageText) {
        String completionMessage = productList.getSearchCompletedMessage();
        Assertions.assertThat(completionMessage).isEqualTo(messageText);
    }

    @And("she filters by {string}")
    public void sheFiltersBy(String filterName) {
        searchComponent.filterBy(filterName);
    }

    @When("she sorts by {string}")
    public void sheSortsBy(String sortFilter) {
        searchComponent.sortBy(sortFilter);
        
    }

    @Then("the first product displayed should be {string}")
    public void theFirstProductDisplayedShouldBe(String firstProductName) {
        List<String> productNames = productList.getProductNames();
        Assertions.assertThat(productNames).startsWith(firstProductName);
    }

//    @Then("the following products should be displayed:")
//    public void theFollowingProductsShouldBeDisplayed(List<String> expectedProducts) {
//        System.out.println(expectedProducts);
//        var matchingProducts = productList.getProductNames();
//        Assertions.assertThat(matchingProducts).containsAll(expectedProducts);
//    }

    
}
