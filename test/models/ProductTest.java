package models;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

    @Test
    public void checkThatProductFindAllIsNotEmpty() {
        assertThat(ProductEntity.findAll().isEmpty()).isFalse();
    }

    @Test
    public void checkThatTheSizeOfProductsIsGreaterThan3() {
        assertThat(ProductEntity.findAll().size()).isGreaterThan(3);
    }

    @Test
    public void checkThatThereIsOneProductWithEANEqualsTo10000() {
        assertThat(ProductEntity.findByEan("10000")).isPresent();
    }

    @Test
    public void checkThatThereIsNoProductWithEANEqualsTo999() {
        assertThat(ProductEntity.findByEan("999")).isNotPresent();
    }

    @Test
    public void checkThatFindByNameWorks() {
        assertThat(ProductEntity.findByName("Macbook")).isNotNull();
        assertThat(ProductEntity.findByName("Macbook")).hasSize(2);
    }

    @Test
    public void checkThatSaveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        ProductEntity myProduct = new ProductEntity(ean,name,description);

        assertThat(ProductEntity.findByEan(ean)).isNotPresent();
        myProduct.save();
        assertThat(ProductEntity.findByEan(ean)).isPresent();
    }

    @Test
    public void checkThatRemoveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        ProductEntity myProduct = new ProductEntity(ean,name,description);

        assertThat(ProductEntity.findByEan(ean)).isNotPresent();
        myProduct.save();
        assertThat(ProductEntity.findByEan(ean)).isPresent();
        myProduct.remove();
        assertThat(ProductEntity.findByEan(ean)).isNotPresent();
    }
}