package dev.sample.productservice.controller;

import dev.sample.productservice.dto.ProductRequest;
import dev.sample.productservice.dto.ProductResponse;
import dev.sample.productservice.dto.CategorySummary;
import dev.sample.productservice.dto.ApiResponse;
import dev.sample.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        log.info("POST /api/v1/products - Creating product: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("GET /api/v1/products - Fetching all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(
                ApiResponse.success("Products fetched successfully", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id) {
        log.info("GET /api/v1/products/{} - Fetching product", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Product fetched successfully", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("PUT /api/v1/products/{} - Updating product", id);
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Product updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(
                ApiResponse.success("Product deleted successfully", null));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategory(
            @PathVariable String category) {
        log.info("GET /api/v1/products/category/{} - Fetching by category", category);
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(
                ApiResponse.success("Products fetched by category", products));
    }

    @GetMapping("/category-id/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByCategoryId(
            @PathVariable Long categoryId) {
        log.info("GET /api/v1/products/category-id/{} - Fetching by category id", categoryId);
        List<ProductResponse> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(
                ApiResponse.success("Products fetched by category id", products));
    }

    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<List<CategorySummary>>> getActiveCategories() {
        log.info("GET /api/v1/products/categories/active - Fetching active categories from category-service");
        List<CategorySummary> categories = productService.getActiveCategories();
        return ResponseEntity.ok(
                ApiResponse.success("Active categories fetched successfully", categories));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchByName(
            @RequestParam String name) {
        log.info("GET /api/v1/products/search?name={} - Searching products", name);
        List<ProductResponse> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(
                ApiResponse.success("Search results", products));
    }

    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/v1/products/price-range?min={}&max={}", min, max);
        List<ProductResponse> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(
                ApiResponse.success("Products in price range", products));
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getInStock() {
        log.info("GET /api/v1/products/in-stock - Fetching in-stock products");
        List<ProductResponse> products = productService.getInStockProducts();
        return ResponseEntity.ok(
                ApiResponse.success("In-stock products fetched", products));
    }
}



