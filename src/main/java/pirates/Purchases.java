package pirates;

import java.util.Set;

public class Purchases {
    private final int requestedNumberOfGallons;
    private Set<Purchase> purchases;

    public Purchases(int requestedNumberOfGallons) {
        this.requestedNumberOfGallons = requestedNumberOfGallons;
    }

    public Double calculateAveragePrice() {
        double totalSum = 0;
        for (Purchase purchase : getPurchases()) {
            totalSum += purchase.getTotalPrice();
        }
        return getRequestedNumberOfGallons() == 0
                ? null
                : totalSum / getRequestedNumberOfGallons();
    }

    public Integer getRequestedNumberOfGallons() {
        return requestedNumberOfGallons;
    }

    public Set<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(Set<Purchase> purchases) {
        this.purchases = purchases;
    }
}
