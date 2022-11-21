
import java.util.Scanner;


public class Main {

    static NodeMonitoring indyNodeManager = new NodeMonitoring();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Hello");

        try {

            indyNodeManager.ConnectIndyPool();
            System.out.println("Wallet ID: ");
            String my_wallet = sc.next();
            System.out.println("Wallet KEY: ");
            String my_wallet_key = sc.next();
            indyNodeManager.createWallet(my_wallet, my_wallet_key);
            indyNodeManager.createEndorserDid();
            indyNodeManager.createDid();
            indyNodeManager.GetValidatorInfo();

            //indyNodeManager.RunIndyContainer();

        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        System.out.println("wallet indfo : " + indyNodeManager.myDid);
        //indyNodeManager.RunIndyContainer();
    }


}