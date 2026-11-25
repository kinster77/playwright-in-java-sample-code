package previousWorkings.previousworkings;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightFormsTest_MyException {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser(){
        playwright = Playwright.create();
        browser = playwright.firefox().launch();
        browserContext = browser.newContext();
    }

    @AfterEach
    void closeContext(){
        browserContext.close();
    }

    @AfterAll
static void tearDown(){
        browser.close();
        playwright.close();
    }

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields{

        @BeforeEach
        void openContactPage(){
            page = browserContext.newPage();
            page.navigate("https://practicesoftwaretesting.com/contact");
        }
/*
        @DisplayName("Input fields")
        @Test
        void fieldValues(){
            var firstNameField = page.getByLabel("First name");
            firstNameField.fill("Sarah-Jane");
            assertThat(firstNameField).hasValue("Sarah-Jane");
        }

 */

        @DisplayName("Complete the Form")
        @Test
        void completeForm(){
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("Last name");
            var emailField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message");
            var subjectField = page.getByLabel("Subject");
            var uploadField = page.getByLabel("Attachment");


            firstNameField.fill("Sarah-Jane");
            lastNameField.fill("Smith");
            emailField.fill("Sarah-Jane@example.com");
            messageField.fill("Hello World!");
            subjectField.selectOption(new SelectOption().setIndex(5));

            Path fileToUpload;
            try{fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());}
            catch (Exception e){
                throw new RuntimeException("Failed to load sample-data.txt", e);

            }

            page.setInputFiles("#attachment", fileToUpload);



            assertThat(firstNameField).hasValue("Sarah-Jane");
            assertThat(lastNameField).hasValue("Smith");
            assertThat(emailField).hasValue("Sarah-Jane@example.com");
            assertThat(messageField).hasValue("Hello World!");
            assertThat(subjectField).hasValue("warranty");

            String uploadedFile = uploadField.inputValue();
            org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
        }
    }
}
