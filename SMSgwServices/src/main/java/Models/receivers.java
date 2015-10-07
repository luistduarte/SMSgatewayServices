/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

/**
 *
 * @author root
 */
public class receivers {
    
    private receiver receiverAddress;
    //private request requestid;
    private state stateid;

    public receivers(receiver receiverAddress, state stateid) {
        this.receiverAddress = receiverAddress;
        //this.requestid = requestid;
        this.stateid = stateid;
    }

    public receiver getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(receiver receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
/*
    public request getRequestid() {
        return requestid;
    }

    public void setRequestid(request requestid) {
        this.requestid = requestid;
    }
*/
    public state getStateid() {
        return stateid;
    }

    public void setStateid(state stateid) {
        this.stateid = stateid;
    }
    
    
    
}
