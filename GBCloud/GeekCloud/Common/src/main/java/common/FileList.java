package common;

import java.util.ArrayList;

public class FileList extends AbstractMessage {

    public ArrayList<String> data;
    public ArrayList<String> getData(){
        return data;
    }
public FileList(ArrayList<String> al){
        data=al;
}
}
