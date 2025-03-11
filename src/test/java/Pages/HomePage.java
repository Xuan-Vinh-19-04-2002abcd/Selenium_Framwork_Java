package Pages;

import Core.UI.Element;
import org.openqa.selenium.By;

public class HomePage {
    Element MY_ACCOUNT_BTN = new Element(By.xpath("//span[.='My Account']"));
    Element REGISTER_BTN = new Element(By.xpath("//a[.='Register']"));
    public void GotoRegisterPage() {
        MY_ACCOUNT_BTN.click();
        REGISTER_BTN.click();
    }
}
