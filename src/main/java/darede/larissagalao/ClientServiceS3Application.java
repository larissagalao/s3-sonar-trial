package darede.larissagalao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClientServiceS3Application {
  public static void main(String[] args) {
    SpringApplication.run(ClientServiceS3Application.class, args);
  }
}
