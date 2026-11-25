package myfirstplaywrightpackage.toolshop.login;

import myfirstplaywrightpackage.toolshop.domain.User;
import myfirstplaywrightpackage.toolshop.fixtures.PlaywrightTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class LoginWithRegisteredUserTest extends PlaywrightTestCase {

    @Test
    @DisplayName("Should be able to login with a registered user")
    void should_login_with_registered_user(){

        // Register a user via the API
        User user = User.randomUser();
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);
        // Login via the login page
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user);
        // Check that we are on the right account page
        assertThat(loginPage.title()).isEqualTo("My account");
    }

    @Test
    @DisplayName("Should reject user with invalid password")
    void should_reject_user_with_invalid_password(){
        // Register a user via the API
        User user = User.randomUser();
        UserAPIClient userAPIClient = new UserAPIClient(page);
        userAPIClient.registerUser(user);
        // Open login page and use wrong password
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user.withPassword("wrong-password"));
        // Assertion
        assertThat(loginPage.loginErrorMessage()).isEqualTo("Invalid email or password");
    }
}
