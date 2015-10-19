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
public class sender {

    private int senderAddress;
    
    private String username;


    public sender(int senderAddress, String username) {
        this.senderAddress = senderAddress;
        this.username = username;
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(int senderAddress) {
        this.senderAddress = senderAddress;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
