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
    /*
    private String name;
    private boolean notifications;
    */
    public sender(int senderAddress) {
        this.senderAddress = senderAddress;
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    */
}
