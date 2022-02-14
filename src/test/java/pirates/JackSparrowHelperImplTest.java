package pirates;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(Enclosed.class)
public class JackSparrowHelperImplTest {
    private static final JackSparrowHelper jackSparrowHelper = new JackSparrowHelperImpl();


    @RunWith(Parameterized.class)
    public static class parametrizedJackSparrowHelperImplTestForShowingData {
        private final int numberOfGallons;

        public parametrizedJackSparrowHelperImplTestForShowingData(int numberOfGallons) {
            this.numberOfGallons = numberOfGallons;
        }

        @Parameterized.Parameters
        public static List<Integer> data() {
            return IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        }

        @Test
        public void ShowAveragePricesAndPurchasesForDifferentCases() {
            Purchases purchases = jackSparrowHelper.helpJackSparrow("src/main/resources/sources.csv", numberOfGallons);
            System.out.println("Количество требуемых галонов: " + numberOfGallons);
            System.out.printf("Средняя цена: %.3f", purchases.calculateAveragePrice());
            System.out.println("\nПокупки: " + purchases.getPurchases());
        }
    }

    @RunWith(Parameterized.class)
    public static class parametrizedJackSparrowHelperImplTestForEqualsComparing {
        private final double expectedAvgPrice;
        private final int numberOfGallons;

        public parametrizedJackSparrowHelperImplTestForEqualsComparing(int numberOfGallons, double expectedAvgPrice) {
            this.numberOfGallons = numberOfGallons;
            this.expectedAvgPrice = expectedAvgPrice;
        }

        @Parameterized.Parameters
        public static List<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {50, 50}, {51, 50.039}, {100, 51.000}, {110, 51.091}, {210, 51.048}, {301, 50.417}, {401, 50.628},
                    {501, 50.703}, {601, 51.373}, {701, 51.890}, {801, 52.029}, {901, 52.358}, {910, 52.385}, {950, 52.385},
                    {999, 52.385}
            });
        }

        @Test
        public void ShowAveragePricesAndPurchasesForDifferentCases() {
            Purchases purchases = jackSparrowHelper.helpJackSparrow("src/main/resources/sources.csv", numberOfGallons);
            assertEquals(expectedAvgPrice, purchases.calculateAveragePrice(), 0.001);
        }
    }

    public static class notParametrizedJackSparrowHelperImplTest {
        @Test
        public void IfSpecifiedPathIsWrongWeGetNullAveragePrice() {
            Purchases purchases = jackSparrowHelper.helpJackSparrow("21j21j1j12 n11lnqdqw", 111);
            assertNull(purchases.calculateAveragePrice());
        }

        @Test
        public void IfNumberOfGallonsIsLessThanZeroWeGetNullAveragePrice() {
            Purchases purchases = jackSparrowHelper.helpJackSparrow("src/main/resources/sources.csv", -1);
            assertNull(purchases.calculateAveragePrice());
        }
    }

}