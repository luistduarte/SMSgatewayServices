/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import java.util.List;

/**
 *
 * @author root
 */
public class request {
    
    //private int requestid;
    
    private String body;
    private sender senderAddress;
    private List<receiver> receiver;

   /* public request(int requestid, String body, sender senderAddress, List<receiver> receiver) {
        this.requestid = requestid;
        this.body = body;
        this.senderAddress = senderAddress;
        this.receiver = receiver;
    }
    */
    public request(String body, sender senderAddress, List<receiver> receiver) {
        this.body = body;
        this.senderAddress = senderAddress;
        this.receiver = receiver;
    }
    
    
    /*
    public int getRequestid() {
        return requestid;
    }

    public void setRequestid(int requestid) {
        this.requestid = requestid;
    }
    */

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public sender getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(sender senderAddress) {
        this.senderAddress = senderAddress;
    }

    public List<receiver> getReceivers() {
        return receiver;
    }

    public void setReceivers(List<receiver> receiver) {
        this.receiver = receiver;
    }
    
    
    
    
    
}
