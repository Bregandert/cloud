package common;

public class ListRequest extends AbstractMessage {

        private String request;

        public String getRequest() {
            return request;
        }
        public ListRequest(String a){
            this.request=a;

}

}