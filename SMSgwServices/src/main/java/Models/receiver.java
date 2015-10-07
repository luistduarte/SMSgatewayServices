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
public class receiver {
    
    private int receiverAddress;
    //private String name;

    public receiver(int receiverAddress, String name) {
        this.receiverAddress = receiverAddress;
      //  this.name = name;
    }
    
     public receiver(int receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(int receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
/*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    */
    
    
}
