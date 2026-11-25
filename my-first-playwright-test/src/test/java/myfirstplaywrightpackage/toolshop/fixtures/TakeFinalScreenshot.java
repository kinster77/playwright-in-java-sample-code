package myfirstplaywrightpackage.toolshop.fixtures;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;


public interface TakeFinalScreenshot {
    @AfterEach
    default void takeScreenshot(Page page){
        System.out.println("Taking final screenshot");
        ScreenshotManager.takeScreenshot(page, "Final screenshot");
    }
}
