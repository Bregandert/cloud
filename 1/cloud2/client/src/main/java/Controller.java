import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button send;
    public Button update;
    public ListView<String> listView1;
    public ListView<String> listView2;
    public TextField text;
    private List<File> clientFileList;
    private List<File> serverFileList;
    public static Socket socket;
    private DataInputStream is;
    private DataOutputStream os;

    public void sendCommand(ActionEvent actionEvent) {
        System.out.println("SEND!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: 7/21/2020 init connect to server
        try{
            socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            Thread.sleep(1500);
            clientFileList = new ArrayList<>();
            serverFileList = new ArrayList<>();
            String clientPath = "./client/src/main/resources/";
            String serverPath = "./server/src/main/resources/";
            File dir1 = new File(clientPath);
            File dir2 = new File(serverPath);
            if (!dir1.exists()||!dir2.exists()) {
                throw new RuntimeException("directory resource not exists on client");
            }
            update.setOnMouseClicked(c ->{
                if (c.getClickCount()==1) {
                    clientFileList.clear();
                    serverFileList.clear();
                    listView1.getItems().clear();
                    listView2.getItems().clear();
            for (File file : Objects.requireNonNull(dir1.listFiles())) {
                clientFileList.add(file);
                listView1.getItems().add(file.getName());
            }
            for (File file : Objects.requireNonNull(dir2.listFiles())) {
                serverFileList.add(file);
                listView2.getItems().add(file.getName());
            }}});

            listView1.setOnMouseClicked(a -> {
                if (a.getClickCount() == 2) {
                    String fileName1 = listView1.getSelectionModel().getSelectedItem();
                    File currentFile = findFileByName(fileName1);
                    if (currentFile != null) {
                        try {
                            os.writeUTF("./upload");
                            os.writeUTF(fileName1);
                            os.writeLong(currentFile.length());
                            FileInputStream fis = new FileInputStream(currentFile);
                            byte [] buffer = new byte[1024];
                            while (fis.available() > 0) {
                                int bytesRead = fis.read(buffer);
                                os.write(buffer, 0, bytesRead);
                            }
                            os.flush();
                            } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            listView2.setOnMouseClicked(b -> {
                if (b.getClickCount() == 2) {
                    String fileName2 = listView2.getSelectionModel().getSelectedItem();
                    File currentFile2 = findServerFileByName(fileName2);
                    if (currentFile2 != null) {
                        try {
                            os.writeUTF("./download");
                            os.writeUTF(fileName2);
                            os.writeLong(currentFile2.length());
                            FileInputStream fis2 = new FileInputStream(currentFile2);
                            byte [] buffer = new byte[1024];
                            while (fis2.available() > 0) {
                                int bytesRead = fis2.read(buffer);
                                os.write(buffer, 0, bytesRead);
                            }
                            os.flush();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File findFileByName(String fileName) {
        for (File file : clientFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }
    private File findServerFileByName(String fileName) {
        for (File file : serverFileList) {
            if (file.getName().equals(fileName)){
                return file;
            }
        }
        return null;
    }
}