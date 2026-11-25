package myfirstplaywrightpackage.toolshop.contact;

import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import myfirstplaywrightpackage.toolshop.fixtures.PlaywrightTestCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Contact form")
@Feature("Contacts")
public class ContactFormTest extends PlaywrightTestCase {

    ContactForm contactForm;
    @BeforeEach
    void openContactPage(){
        contactForm = new ContactForm(page);
        page.navigate("https://practicesoftwaretesting.com/contact");
    }

    @DisplayName("The message must be at least 50 characters long")
    @Story ("Contact form")
    @Test
    void messageValidation() {
        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("Sarah-Jane@example.com");
        contactForm.setMessage("Hello World!");
        contactForm.selectSubject("Warranty");

        contactForm.submitForm();
        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Message must be minimal 50 characters");
    }

        @DisplayName("Customer can use the contact form to contact us")
        @Story ("Contact form")
        @Test
        void completeForm() throws URISyntaxException {
            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail("Sarah-Jane@example.com");
            contactForm.setMessage("Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!");
            contactForm.selectSubject("Warranty");

            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());

            contactForm.setAttachment(fileToUpload);

            contactForm.submitForm();

           Assertions.assertThat(contactForm.getAlertMessage()).contains("Thanks for your message! We will contact you shortly.");
        }

        @DisplayName("First name, last name, email and message are Mandatory fields")
        @Story ("Contact form")
        @ParameterizedTest
        @ValueSource(strings = {"First name","Last name", "Email", "Message"})
        void mandatoryFields(String fieldName){
            contactForm.setFirstName("Sarah-Jane");
            contactForm.setLastName("Smith");
            contactForm.setEmail("Sarah-Jane@example.com");
            contactForm.setMessage("Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!, Hello World!");
            contactForm.selectSubject("Warranty");

            // Clear one of the fields
            contactForm.clearField(fieldName);
            page.getByText(fieldName).clear();

            // Submit
            contactForm.submitForm();

            // Check the error message for that field
            var errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");
            assertThat(errorMessage).isVisible();
        }
    }

