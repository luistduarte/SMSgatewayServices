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
public class requestDeliveryInfo {
     private int requestID;
     private List<receivers> receivers;
    

    public requestDeliveryInfo(int requestID,List<receivers> receivers) {
        this.receivers = receivers;
        this.requestID = requestID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public List<receivers> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<receivers> receivers) {
        this.receivers = receivers;
    }
     
}
