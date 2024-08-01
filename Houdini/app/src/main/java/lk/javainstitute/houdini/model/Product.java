package lk.javainstitute.houdini.model;

public class Product {
    private String productName;
    private double productPrice;
    private String productDesc;
    private double productQty;
    private String productBrand;
    private String productImage;

    public Product() {
    }

    public Product(String productName, double productPrice, String productDesc, double productQty, String productBrand, String productImage) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productQty = productQty;
        this.productBrand = productBrand;
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public double getProductQty() {
        return productQty;
    }

    public void setProductQty(double productQty) {
        this.productQty = productQty;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
