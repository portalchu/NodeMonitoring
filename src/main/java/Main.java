
import java.util.Scanner;


public class Main {

    static NodeMonitoring indyNodeManager = new NodeMonitoring();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int num = 0;

        try {
            System.out.println("1 : ");

            while (true)
            {
                switch (num)

                break;
            }

            indyNodeManager.ConnectIndyPool();

            /*
            indyNodeManager.walletInput();
            indyNodeManager.GetValidatorInfo();



            indyNodeManager.RunIndyContainer();
            indyNodeManager.AddNode();

             */
            indyNodeManager.GetValidatorInfo();

        } catch (Exception e) {
            System.out.println("Exception : " + e);
        }

        //indyNodeManager.RunIndyContainer();
    }


}