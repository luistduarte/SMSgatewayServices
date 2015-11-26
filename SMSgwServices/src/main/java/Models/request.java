package Models;
/**
 *
 * @author Luis Duarte
 */
public class request { 
    private String body;
    private String senderAddress;
    private int requestid;
    private int stateid;

    public request(String body, String senderAddress, int requestid, int stateid) {
        this.body = body;
        this.senderAddress = senderAddress;
        this.requestid = requestid;
        this.stateid = stateid;
    }

    public int getStateid() {
        return stateid;
    }

    public void setStateid(int stateid) {
        this.stateid = stateid;
    }

    public int getRequestid() {
        return requestid;
    }

    public void setRequestid(int requestid) {
        this.requestid = requestid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }
}
