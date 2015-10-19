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
    private int senderAddress;
 

    public request(String body, int senderAddress) {
        this.body = body;
        this.senderAddress = senderAddress;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(int senderAddress) {
        this.senderAddress = senderAddress;
    }
}
