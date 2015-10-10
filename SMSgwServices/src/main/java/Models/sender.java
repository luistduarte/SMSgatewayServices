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
    /*
    private boolean notifications;
    */

    public sender(int senderAddress, String username) {
        this.senderAddress = senderAddress;
        this.username = username;
    }

    /*
    public sender(int senderAddress, String name, boolean notifications) {
        this.senderAddress = senderAddress;
        this.name = name;
        this.notifications = notifications;
    }
    */
    public int getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(int senderAddress) {
        this.senderAddress = senderAddress;
    }
    
    
    /*
    
    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
