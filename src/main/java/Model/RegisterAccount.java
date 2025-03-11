package Model;

import com.github.javafaker.Faker;
import lombok.Getter;

@Getter
public class RegisterAccount {
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String password;
    private String confirmPassword;

    // Constructor để khởi tạo dữ liệu giả mạo
    public RegisterAccount() {
        Faker faker = new Faker();
        this.firstName = faker.name().firstName();
        this.lastName = faker.name().lastName();
        this.email = faker.internet().emailAddress();
        this.telephone = faker.phoneNumber().cellPhone();
        this.password = faker.internet().password(8, 16);
        this.confirmPassword = this.password;
    }

}
