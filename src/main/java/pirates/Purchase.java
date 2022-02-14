package pirates;

import lombok.ToString;

@ToString
public class Purchase {
    private String sourceName;
    private Integer numberOfGallons;
    private Double priceOfGallon;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(final String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getNumberOfGallons() {
        return numberOfGallons;
    }

    public void setNumberOfGallons(final Integer numberOfGallons) {
        this.numberOfGallons = numberOfGallons;
    }

    public Double getPriceOfGallon() {
        return priceOfGallon;
    }

    public void setPriceOfGallon(final Double priceOfGallon) {
        this.priceOfGallon = priceOfGallon;
    }

    public Double getTotalPrice() {
        return getPriceOfGallon() * getNumberOfGallons();
    }
}
