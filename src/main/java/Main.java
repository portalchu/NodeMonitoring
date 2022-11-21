
import java.util.Scanner;


public class Main {

    static NodeMonitoring indyNodeManager = new NodeMonitoring();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {


            indyNodeManager.ConnectIndyPool();
            System.out.println("Wallet ID: ");
            String my_wallet = sc.next();
            System.out.println("Wallet KEY: ");
            String my_wallet_key = sc.next();
            indyNodeManager.createWallet(my_wallet, my_wallet_key);
            indyNodeManager.createTrusteeDid();
            indyNodeManager.createDid();
            indyNodeManager.GetValidatorInfo();



            indyNodeManager.RunIndyContainer();
            indyNodeManager.AddNode();

        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        //indyNodeManager.RunIndyContainer();
    }


}