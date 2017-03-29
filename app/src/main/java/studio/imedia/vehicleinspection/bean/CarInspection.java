package studio.imedia.vehicleinspection.bean;

/**
 * Created by eric on 15/10/10.
 */
public class CarInspection {
    private String inpectionType;
    private int soldCount;
    private int priceOriginal;
    private int priceDiscount;

    public String getInpectionType() {
        return inpectionType;
    }

    public void setInpectionType(String inpectionType) {
        this.inpectionType = inpectionType;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public int getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(int priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public int getPriceDiscount() {
        return priceDiscount;
    }

    public void setPriceDiscount(int priceDiscount) {
        this.priceDiscount = priceDiscount;
    }
}
