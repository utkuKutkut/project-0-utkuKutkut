//helpful readings:
// https://www.geeksforgeeks.org/multithreaded-servers-in-java/
// https://www.tutorialspoint.com/javaexamples/net_multisoc.htm

import java.util.Arrays;

public class Main {

    public static void main(String args[]){
        DataOuterClass.Data dataToSent = DataOuterClass.Data.newBuilder().setA("Utku").build();

        //String str = new String(a); // for UTF-8 encoding
       // System.out.println(str);

        LossyServer host1 = new LossyServer(1901);

        host1.send(dataToSent.toByteArray());

    }

}
