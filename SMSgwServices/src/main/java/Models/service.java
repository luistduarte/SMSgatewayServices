package Models;
/**
 *
 * @author Luis Duarte
 */
public class service {
    private int serviceid;
    private String serviceurl;
    private String name;

    public service(int serviceid, String serviceurl, String name) {
        this.serviceid = serviceid;
        this.serviceurl = serviceurl;
        this.name = name;
    }

    public int getServiceid() {
        return serviceid;
    }

    public void setServiceid(int serviceid) {
        this.serviceid = serviceid;
    }

    public String getServiceurl() {
        return serviceurl;
    }

    public void setServiceurl(String serviceurl) {
        this.serviceurl = serviceurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }  
}
