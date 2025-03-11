import Model.RegisterAccount;
import Pages.HomePage;
import Pages.RegisterPage;
import com.github.javafaker.Faker;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class RegisterTest extends BaseTest {
    private HomePage homepage = new HomePage();
    private RegisterPage registerPage = new RegisterPage();
    private RegisterAccount registerAccount = new RegisterAccount();
    @Test
    public void login() throws InterruptedException {
        homepage.GotoRegisterPage();
        registerPage.Register(registerAccount);
        Assert.assertTrue(registerPage.checkRegisterSuccessMessage());

    }

    public static void main(String[] args) {
        int[] arr = {5, 2, 8, 1, 9, 3};
        Arrays.sort(arr);

        System.out.print("Mảng sau khi sắp xếp: ");
        for (int i : arr) {
            System.out.print(i + " ");
        }
    }
}
