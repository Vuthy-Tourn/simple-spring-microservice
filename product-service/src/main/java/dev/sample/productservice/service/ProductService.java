package dev.sample.productservice.service;

import dev.sample.productservice.dto.CategorySummary;
import dev.sample.productservice.dto.ProductRequest;
import dev.sample.productservice.dto.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> getProductsByCategory(String category);

    List<ProductResponse> getProductsByCategoryId(Long categoryId);

    List<ProductResponse> searchProductsByName(String name);

    List<ProductResponse> getProductsByPriceRange(BigDecimal min, BigDecimal max);

    List<ProductResponse> getInStockProducts();

    List<CategorySummary> getActiveCategories();
}
