package Pages;

import Core.UI.Element;
import Model.RegisterAccount;
import com.google.common.base.Verify;
import org.openqa.selenium.By;

public class RegisterPage extends  HomePage {
    Element FIRST_NAME_INT = new Element(By.name("firstname"));
    Element LAST_NAME_INT = new Element(By.name("lastname"));
    Element Email = new Element(By.name("email"));
    Element Telephone = new Element(By.name("telephone"));
    Element Password = new Element(By.name("password"));
    Element ConfirmPassword = new Element(By.name("confirm"));
    Element PrivacyPolicy = new Element(By.name("agree"));
    Element REGISTER_SUBMIT_BTN = new Element(By.xpath("//input[@type='submit']"));
    Element REGISTER_SUCCESS_MESSAGE = new Element(By.xpath("//p[.='Congratulations! Your new account has been successfully created!']"));
    public void Register(RegisterAccount registerAccount) throws InterruptedException {
        FIRST_NAME_INT.enter(registerAccount.getFirstName());
        LAST_NAME_INT.enter(registerAccount.getLastName());
        Email.enter(registerAccount.getEmail());
        Telephone.enter(registerAccount.getTelephone());
        Password.enter(registerAccount.getPassword());
        ConfirmPassword.enter(registerAccount.getConfirmPassword());
        PrivacyPolicy.click();
        REGISTER_SUBMIT_BTN.click();
        Thread.sleep(10000);
    }
    public boolean checkRegisterSuccessMessage(){
        return REGISTER_SUCCESS_MESSAGE.isDisplayed();
    }

}
