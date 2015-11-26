package Models;
/**
 *
 * @author Luis Duarte
 */
public class rule {

    private String regex;

    public rule(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
