import com.google.protobuf.InvalidProtocolBufferException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class LossyServer implements Receiver, Sender{
    int PORT;
    Socket clientSocket = new Socket();
    byte[] ans;


    public LossyServer(int port) {
        this.PORT = port;
        try {
            SetupServer server1 = new SetupServer(this.PORT);
            new Thread(server1).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override public byte[] receive() {
        if(ans != null) {
            // the receiver and sender got the SAME byte array. It works fine ...
            //String res = Arrays.toString(ans);
            //System.out.println("Recieved from client : " + res);

            DataOuterClass.Data data;

                //data = DataOuterClass.Data.parseFrom(ans);
              //  System.out.println(data.getA());

        }
        return ans;
    }



    @Override public boolean send(byte[] message) {
        double lossRate = .25;
        double maxDelay = 50;

            try (Socket socket = new Socket("localhost", 1900)) {

                // writing to server
                OutputStream out;
                out = new DataOutputStream(socket.getOutputStream());

                int i = 10;
                while (i>0) {

                    Random rand = new Random();
                    // Generate random integers in range 0 to 100
                    int rand_int1 = rand.nextInt(100);
                    //data lost case!
                    if(rand_int1 < (lossRate * 100)){
                        System.out.println("PACKET LOST!");
                        i--;
                        continue;
                    }

                    //for delay
                    rand = new Random();
                    int rand_int2 = rand.nextInt((int) maxDelay);

                    // sending the user input to server
                    DataOuterClass.Data data = DataOuterClass.Data.parseFrom(message);
                    System.out.println("SENDING DATA:"+data.getA()+"  Delay time:"+rand_int2);
                    TimeUnit.MILLISECONDS.sleep(rand_int2);
                    out.write(message);
                    out.flush();

                    i--;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        return true;
    }



    class SetupServer extends Thread {

        int PORT;
        public SetupServer(int port) {
            this.PORT = port;
        }

        public void run()
        {
            ServerSocket server = null;

            try {

                // server is listening on port
                server = new ServerSocket(this.PORT);
                server.setReuseAddress(true);

                while (true) {

                    clientSocket = server.accept();
                    System.out.println("Connection established!");

                    ClientHandler clientSock = new ClientHandler();
                    new Thread(clientSock).start();

                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (server != null) {
                    try {
                        server.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    class ClientHandler extends Thread {

        public void run()
        {
            try {
                InputStream inputStream = new DataInputStream(clientSocket.getInputStream());
                int checkBit = 0;
                while(checkBit != -1) {
                    byte buffer[] = new byte[1024];
                    checkBit = inputStream.read(buffer);
                    if(checkBit == -1) break;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write(buffer, 0, checkBit);
                    byte result[] = baos.toByteArray();

                    DataOuterClass.Data data = DataOuterClass.Data.parseFrom(result);
                    System.out.println("RECEIVED:"+data.getA());

                    ans = result;
                    receive();
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

