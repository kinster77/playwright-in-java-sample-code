package myfirstplaywrightpackage.toolshop.contact;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Path;

public class ContactForm {
    private final Page page;
    private Locator firstNameField;
    private Locator lastNameField;
    private Locator emailField;
    private Locator messageField;
    private Locator subjectField;
    private Locator sendButton;

    public ContactForm(Page page){
        this.page = page;
        firstNameField = page.getByLabel("First name");
        lastNameField = page.getByLabel("Last name");
        emailField = page.getByLabel("Email");
        messageField = page.getByLabel("Message");
        subjectField = page.getByLabel("Subject");
        sendButton = page.getByText("Send");

    }

    public void setFirstName(String firstname) {
        firstNameField.fill(firstname);
    }

    public void setLastName(String lastname) {
        lastNameField.fill(lastname);
    }


    public void setEmail(String email) {
        emailField.fill(email);
    }

    public void setMessage(String message) {
        messageField.fill(message);
    }

    public void selectSubject(String subject) {
        subjectField.selectOption(subject);
    }

    public void setAttachment(Path fileToUpload) {
        page.setInputFiles("#attachment", fileToUpload);
    }

    public void submitForm() {
        sendButton.click();
    }

    public String getAlertMessage() {
        return page.getByRole(AriaRole.ALERT).textContent();
    }

    public void clearField(String fieldName) {
        page.getByText(fieldName).clear();
    }
}
