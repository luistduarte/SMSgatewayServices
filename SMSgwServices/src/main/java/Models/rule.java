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
public class rule {
    /*private int ruleid;
    private int serviceid;*/
    private String regex;
    /*
    public rule(int ruleid, int serviceid, String regex) {
        this.ruleid = ruleid;
        this.serviceid = serviceid;
        this.regex = regex;
    }*/

    
    public rule(String regex) {
        this.regex = regex;
    }
    
    /*
    public int getRuleid() {
        return ruleid;
    }

    public void setRuleid(int ruleid) {
        this.ruleid = ruleid;
    }

    public int getServiceid() {
        return serviceid;
    }

    public void setServiceid(int serviceid) {
        this.serviceid = serviceid;
    }*/
    

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }



}
