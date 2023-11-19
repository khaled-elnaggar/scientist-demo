package org.example;

public class ProductRest {
  ProductDatabase productDatabase = new ProductDatabase();

  public Product getProductsV1(int i) {
    return productDatabase.getProductV1(i);
  }

  public Product getProductsV2(int i) {
    return productDatabase.getProductV2(i);
  }
}
