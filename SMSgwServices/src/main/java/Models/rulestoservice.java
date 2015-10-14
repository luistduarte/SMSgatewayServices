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
public class rulestoservice {
    private String url;
    private List<rule> rules;

    public rulestoservice(String url, List<rule> rules) {
        this.url = url;
        this.rules = rules;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<rule> getRules() {
        return rules;
    }

    public void setRules(List<rule> rules) {
        this.rules = rules;
    }
    
    
}
