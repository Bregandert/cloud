package client;

import common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Button Send;
    public Button Update;
    public Button Download;
    public ListView<String> ServerList;
    public ListView<String> ClientList;
    public TextField tfFileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Network.start();
        Thread t = new Thread(() -> {
            try {
                setTfFileName();
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("ClientStorage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                    if (am instanceof FileList) {
                        FileList fl=(FileList) am;
                        refreshServerFilesList(fl);
                        refreshLocalFilesList();
                    }}
                } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }});
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
        Network.sendMsg(new ListRequest("Запрос"));

        }

    public void setTfFileName(){
        ClientList.setOnMouseClicked(a ->{
            if (a.getClickCount()==2){

                String fileName= ClientList.getSelectionModel().getSelectedItem();
                tfFileName.setText(fileName);
            }});
        ServerList.setOnMouseClicked(a ->{
            if (a.getClickCount()==2){
                String fileName= ServerList.getSelectionModel().getSelectedItem();
                tfFileName.setText(fileName);
            }});}

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        Download.setOnMouseClicked(b->{
            if (tfFileName.getLength() > 0&&b.getClickCount()==1) {
                Network.sendMsg(new FileRequest(tfFileName.getText()));
                tfFileName.clear();
        }});}

    public void pressOnSendBtn(ActionEvent actionEvent) {
        Send.setOnMouseClicked(b->{
            if (tfFileName.getLength() > 0&&b.getClickCount()==1) {
                try {
                    Network.sendMsg(new FileMessage(Paths.get("ClientStorage/" + tfFileName.getText())));
                    tfFileName.clear();
                }catch (IOException e) {
                    e.printStackTrace();
                }}});}

    public void pressOnUpdateBtn(ActionEvent actionEvent) {
        Update.setOnMouseClicked(u->{
            if (u.getClickCount()==1){
                try{
                    Network.sendMsg(new ListRequest("Запрос"));
                } catch (Exception e){
                    e.printStackTrace();
                }}});}

    public void refreshServerFilesList(FileList servlist) {

            FileList fl=servlist;
            Platform.runLater(() -> {
            ArrayList<String> ar=fl.getData();
            ServerList.getItems().clear();
            for (int i=0; i<ar.size();i++){
                ServerList.getItems().add((ar.get(i)));
            }});}

    public void refreshLocalFilesList() {
            Platform.runLater(() -> {
                try {
                    ClientList.getItems().clear();
                    Files.list(Paths.get("ClientStorage"))
                            .filter(p -> !Files.isDirectory(p))
                            .map(p -> p.getFileName().toString())
                            .forEach(o -> ClientList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }});}
}


