/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer2;

import java.net.*;
import java.io.*;

/**
 *
 * @author 12171117
 */
//class to receive requests from the overlay
public class ReceiveService extends Thread {

    MulticastSocket ms;
    String myID;

    Socket s;            //socket to establish connection to requester peer
    OutputStream out;
    String fileName;
    int destinationPort; //requester peer's reception port to file send to

    public ReceiveService(MulticastSocket ms, String myID) {
        this.ms = ms;
        this.myID = myID;

        this.start();
    }

    public void run() {
        while (true) {

            try {
                //receive the multicast msgs
                byte[] buffer = new byte[1000];
                DatagramPacket msgIn = new DatagramPacket(buffer, buffer.length);
                ms.receive(msgIn);
                String msg = new String(msgIn.getData(), 0, msgIn.getLength());

                //to split the incoming message into requested file name and requester's reception port
                String[] str = msg.split(" ");
                fileName = str[0];
                destinationPort = Integer.parseInt(str[1]);

                System.out.println(fileName);
                System.out.println(destinationPort);

                String fileNamePath = "./src/SharingFiles/" + fileName;

                File requestedFile = new File(fileNamePath);

                if (requestedFile.exists()) {
                    InetAddress address = msgIn.getAddress();
                    s = new Socket(address, destinationPort);

                    FileInputStream fileInputStream = new FileInputStream(requestedFile);
                    //Use a data output stream to send the class file
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    byte[] fileNameBytes = fileName.getBytes();

                    byte[] fileContentBytes = new byte[(int) requestedFile.length()];
                    fileInputStream.read(fileContentBytes);

                    dos.writeInt(fileNameBytes.length);
                    //Send the class file name
                    dos.write(fileNameBytes);

                    dos.writeInt(fileContentBytes.length);
                    //Send the class file
                    dos.write(fileContentBytes);
                    dos.flush();
                    System.out.println("File " + fileName + " uploaded.");
                }

            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }
        }
    }
}
