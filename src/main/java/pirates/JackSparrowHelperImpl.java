package pirates;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Yaroslav Kozyrevych
 * @see Purchases
 * Сlass designed to minimize the cost of rum
 */
@Slf4j
public class JackSparrowHelperImpl implements JackSparrowHelper {
    private Set<ExcelObject> excelObjects; // Переменная, содержащая данные из Excel файла
    private int numberOfGallons; // Количество передаваемых галлонов

    @Override
    public Purchases helpJackSparrow(String pathToPrices, int numberOfGallons) {
        Purchases purchases;
        log.info("Called method - helpJackSparrow()");
        try {
            excelObjects = getExcelObjects(pathToPrices);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());

            // Устанавливаю количество галонов в 0, чтобы метод calculateAveragePrice() класса Purchases вернул null
            purchases = new Purchases(0);
            purchases.setPurchases(new HashSet<>());
            return purchases;
        }
        this.numberOfGallons = numberOfGallons;
        removeRedundantSellers();
        Set<Purchase> purchasesSet = countMinPurchases(this.numberOfGallons);
        purchases = new Purchases(this.numberOfGallons);
        purchases.setPurchases(purchasesSet);
        return purchases;
    }

    private void removeRedundantSellers() {
        log.info("Called method - removeRedundantSellers()");
        excelObjects.removeIf(i -> i.getMinSize() > numberOfGallons);
    }

    // TODO: используется жадный алгоритм, в ином случае получится факториальная сложность
    // Получение множества покупок рома среди продавцов
    private Set<Purchase> countMinPurchases(int numberOfGallons) {
        log.info("Called method - countMinPurchases()");
        if (numberOfGallons < 0) {
            this.numberOfGallons = 0;
        }
        Purchase purchase;
        Set<Purchase> purchases = new HashSet<>();
        ExcelObject excelObject;

        while (numberOfGallons > 0) {
            purchase = new Purchase();
            try {
                excelObject = getMinPriceExcelObject(numberOfGallons);
            } catch (NoSuchElementException e) {
                log.warn(e.getMessage() + ". Количество галлонов уменьшилось с {} до {}",
                        this.numberOfGallons, this.numberOfGallons - numberOfGallons);

                // Если нет подходящего обьекта, то количество галлонов становится равным количеству взятых галлонов
                // TODO: непонятно что делать если количество требуемого количества бутылок рома больше максимально возможного
                this.numberOfGallons = this.numberOfGallons - numberOfGallons;
                return purchases;
            }
            if (excelObject.getNumberOfGallons() > numberOfGallons) {
                purchase.setNumberOfGallons(numberOfGallons);
            } else {
                purchase.setNumberOfGallons(excelObject.getNumberOfGallons());

                /*
                  Так как количество галлонов у продавца оказалось меньше либо равным необходимому количеству, то мы
                  берем у этого продавца весь ром, и следовательно больше к данному продавцу не обратимся
                */
                excelObjects.remove(excelObject);
            }
            numberOfGallons -= excelObject.getNumberOfGallons();
            purchase.setSourceName(excelObject.getSourceName());
            purchase.setPriceOfGallon(excelObject.getPriceOfGallon());
            purchases.add(purchase);
        }
        return purchases;
    }

    private ExcelObject getMinPriceExcelObject(int numberOfGallons) {
        log.info("Called method - getMinPriceExcelObject()");

        /*
          Выборка продавцов рома, у которых minSize <= требуемого количества галлонов, с последующим поиском наименьшей
          цены среди подходящих продавцов
         */
        //TODO: для большого количества данных стоило бы использовать параллельный стрим
        ExcelObject excelObject = excelObjects.stream()
                .filter(i -> numberOfGallons >= i.minSize)
                .min(Comparator.comparing(ExcelObject::getPriceOfGallon))
                .orElseThrow(() -> new NoSuchElementException("Нет подходящего обьекта"));

        /*
          Если получилось так, что наиболее подходящий продавец это Hector Barbossa,то после снятия 200 галлонов рома,
          у него останется ровно 1 сундук (100 галлонов), значит теперь у него можно будет брать не от 2 сундуков (200
          галлонов), а ровно 1.
         */
        excelObject.setMinSize(excelObject.getStepSize());
        return excelObject;
    }

    // Преобразование Excel строк в обьекты
    private Set<ExcelObject> getExcelObjects(String pathToPrices) throws FileNotFoundException {
        log.info("Called method - getExcelObjects()");
        Set<ExcelObject> excelObjects;

        try (Scanner scanner = new Scanner(new File(pathToPrices))) {
            excelObjects = new HashSet<>();
            ExcelObject excelObject;

            // Первую строку пропускаем, так как идентификаторы данных нам не нужны
            scanner.nextLine();
            scanner.useDelimiter("[;\r\n]");
            for (int i = 0; i < 6; i++) {
                excelObject = new ExcelObject();
                excelObject.setSourceName(scanner.next());
                excelObject.setNumberOfGallons(scanner.nextInt());
                excelObject.setPriceOfGallon(Double.parseDouble(scanner.next()));
                excelObject.setMinSize(scanner.nextInt());
                excelObject.setStepSize(scanner.nextInt());
                excelObjects.add(excelObject);
                scanner.next();
            }
        }
        return excelObjects;
    }

    // Обьект из Excel файла. Одна строка excel файла = одному обьекту.
    @ToString(callSuper = true)
    private class ExcelObject extends Purchase {
        private int minSize;
        private int stepSize;

        public int getMinSize() {
            return minSize;
        }

        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }

        public int getStepSize() {
            return stepSize;
        }

        public void setStepSize(int stepSize) {
            this.stepSize = stepSize;
        }
    }
}
