/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer3;

import java.io.*;
import java.net.*;

/**
 *
 * @author Ifte
 */
//to establish a TCP connection
public class Connection extends Thread {

    InputStream in;
    OutputStream out;
    Socket clientSocket;                        //reference to the socket of peer sending the file
    private FXMLDocumentController controller;  //reference to GUI

    public Connection(Socket aClientSocket, FXMLDocumentController controller) {
        try {
            clientSocket = aClientSocket;
            in = clientSocket.getInputStream();
            this.controller = controller;   //setting the controller to manipulate GUI
            //Start the thread
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        String fileName = null;
        while (true) {
            try {
                //Construct data input stream to receive requested file
                DataInputStream dataInputStream = new DataInputStream(in);
                //Receive the requested file name length
                int fileNameLength = dataInputStream.readInt();
                //Construct a byte array to receive the file name
                byte[] fileNameBytes = new byte[fileNameLength];
                dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                fileName = new String(fileNameBytes);
                //Receive the requested file length
                int fileContentLength = dataInputStream.readInt();
                //Construct a byte array to receive the file
                byte[] fileContentBytes = new byte[fileContentLength];
                dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                
                //setting destination directory to save file to
                File targetDir = new File("src/SharingFiles");
                File targetFile = new File(targetDir, "Downloaded-" + fileName);
                //Construct a file output stream to save the sent file
                FileOutputStream fo = new FileOutputStream(targetFile);
                fo.write(fileContentBytes, 0, fileContentBytes.length);
                fo.close();
                //to notify the peer that file has been downloaded
                String displayText = ("The file of " + fileName + " was found, downloaded and saved as Downloaded-" + fileName);
                controller.setResult(displayText);

            } catch (EOFException e) {
                System.out.println("EOF" + e.getMessage());
                break;
            } catch (FileNotFoundException ex) {
                System.out.println("File " + fileName + " cannot find.");
                break;
            } catch (SocketException e) {
                System.out.println("Client closed.");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            } finally {
                try {
                    //closing the TCP connection once file received or if any exception thrown
                    clientSocket.close();
                    CastService.listenSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}