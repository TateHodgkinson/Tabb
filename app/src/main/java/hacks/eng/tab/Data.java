package hacks.eng.tab;

/**
 * Created by abhi on 2017-02-04.
 */

public class Data {
    public String name;
    public double amount;
    public int imageId;
    boolean approved;

    Data(String name, double amount, int imageId) {
        this.name = name;
        this.amount = amount;
        this.imageId = imageId;
    }
}
