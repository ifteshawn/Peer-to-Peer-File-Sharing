/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer3;

import java.net.*;
import java.io.*;

/**
 *
 * @author 12171117
 */
//class to send file request message to group
public class CastService extends Thread {

    MulticastSocket ms;
    InetAddress group;  //group address
    int groupPort;      //group's port
    String myID;        //peerID
    int receptionPort;  //port to receive file to
    String fileName;    //requesting file name
    FXMLDocumentController controller;  //to output results in GUI result area

    public CastService(MulticastSocket ms, InetAddress group, int port, String myID, int receptionPort, String fileName, FXMLDocumentController controller) {
        this.ms = ms;
        this.group = group;
        this.groupPort = port;
        this.myID = myID;
        this.receptionPort = receptionPort;
        this.fileName = fileName;
        this.controller = controller;

        this.start();
    }
    
    //socket to accept incoming connections at. This variable needs to be accessed by connectio class as well, hence it is static
    static ServerSocket listenSocket;

    public void run() {
        String msg = null;
        
        try {
            //message to send to group for file request, also including the reception port
            msg = this.fileName + " " + this.receptionPort;

            byte[] m = msg.getBytes();
            DatagramPacket msgOut = new DatagramPacket(m, m.length, group, groupPort);
            ms.send(msgOut);

            //opens server socket and waits for a peer to start sending file requested
            try {
                listenSocket = new ServerSocket(receptionPort);
                //if nobody responds in 3 seconds, listen socket timesout 
                listenSocket.setSoTimeout(3000);
                while (true) {
                    Socket clientSocket = listenSocket.accept();
                    //establishes TCP streaming connection also passing in the controller to connection class to enable it to output text in GUI.
                    Connection c = new Connection(clientSocket, controller);
                }
                //to let the peer to that conneciton timedout, meaning no peers have the file.
            } catch (SocketTimeoutException e) {
                controller.setResult("The request file: " + fileName + " cannot be found on the peer overlay!");
                listenSocket.close();

            } catch (IOException e) {
                System.out.println("Listen socket:" + e.getMessage());
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
