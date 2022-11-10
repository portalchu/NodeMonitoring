import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@SpringBootApplication
public class Main {

    NodeMonitoring indyNodeManager = new NodeMonitoring();

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        Scanner sc = new Scanner(System.in);

        return args -> {
            /*
            indyNodeManager.ConnectIndyPool();
            System.out.println("Wallet ID: ");
            String my_wallet = sc.next();
            System.out.println("Wallet KEY: ");
            String my_wallet_key = sc.next();
            indyNodeManager.createWallet(my_wallet, my_wallet_key);
            indyNodeManager.createEndorserDid();
            indyNodeManager.createDid();
            indyNodeManager.GetValidatorInfo();

             */

            // System.out.println("wallet indfo : " + indyNodeManager.myDid);
            indyNodeManager.RunIndyContainer();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


}