/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peer1;

import java.net.*;
import java.io.*;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 *
 * @author Ifte
 */
public class FXMLDocumentController implements Initializable {

    @FXML // fx:id="groupIPArea"
    private TextField groupIPArea;

    @FXML // fx:id="groupPort"
    private TextField groupPortArea;

    @FXML // fx:id="receptionPortArea"
    private TextField receptionPortArea;

    @FXML // fx:id="set"
    private Button set;

    @FXML // fx:id="search"
    private Button search;

    @FXML // fx:id="searchResultArea"
    private TextArea searchResultArea;

    @FXML // fx:id="fileNameArea"
    private TextField fileNameArea;

    @FXML // fx:id="peerIDArea"
    private TextField peerIDArea;

    public MulticastSocket ms;
    String groupIP;
    int groupPort;
    String myID;
    int receptionPort;
    InetAddress group;
    String fileName;

    //to set parameters and join multicast group on set button clicked
    @FXML
    void handleSetButtonAction(ActionEvent event) {
        groupIP = groupIPArea.getText();
        groupPort = Integer.parseInt(groupPortArea.getText());
        myID = peerIDArea.getText();
        receptionPort = Integer.parseInt(receptionPortArea.getText());

        try {
            //join the multicast group
            group = InetAddress.getByName(groupIP);
            ms = new MulticastSocket(groupPort);
            ms.joinGroup(group);

            //to begin listening to any incoming messages in the multicast group
            ReceiveService receiveService = new ReceiveService(ms, myID);

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    //to send file request to group once clicked on search
    @FXML
    void handleSearchButtonAction(ActionEvent event) {
        fileName = fileNameArea.getText();
        //new castservice taking in also this FXMLController as the controller
        CastService castService = new CastService(ms, group, groupPort, myID, receptionPort, fileName, this);
    }

    //method to set the search result on GUI
    public void setResult(String text) {
        searchResultArea.appendText(text + "\n");
    }

    //to set the text areas default text once the program runs
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        groupIPArea.setText("228.5.6.7");
        groupPortArea.setText("8888");
        peerIDArea.setText("PPP1");
        receptionPortArea.setText("6779");
    }

}
