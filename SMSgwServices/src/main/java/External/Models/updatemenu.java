/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package External.Models;

import java.util.List;

/**
 *
 * @author root
 */
public class updatemenu {

    public info getInfo() {
        return info;
    }

    public void setInfo(info info) {
        this.info = info;
    }

    public List<menu> getMenu() {
        return menu;
    }

    public void setMenu(List<menu> menu) {
        this.menu = menu;
    }
     private info info;
     private List<menu> menu;

    public updatemenu(info info, List<menu> menu) {
        this.info = info;
        this.menu = menu;
    }
     
     
}
