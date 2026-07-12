package dev.sample.productservice.service.serviceImpl;

import dev.sample.productservice.client.CategoryServiceClient;
import dev.sample.productservice.dto.CategorySummary;
import dev.sample.productservice.dto.ProductRequest;
import dev.sample.productservice.dto.ProductResponse;
import dev.sample.productservice.exception.DuplicateResourceException;
import dev.sample.productservice.exception.ResourceNotFoundException;
import dev.sample.productservice.model.Product;
import dev.sample.productservice.repository.ProductRepository;
import dev.sample.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryServiceClient categoryServiceClient;

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with name: {}", request.getName());

        CategorySummary category = categoryServiceClient.getRequiredActiveCategory(request.getCategoryId());

        if (productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        Product product = mapToEntity(request, category);
        Product saved = productRepository.save(product);

        log.info("Product created successfully with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = findOrThrow(id);
        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = findOrThrow(id);
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        return productRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        log.info("Fetching products by category id: {}", categoryId);
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        log.info("Fetching products with price between {} and {}", min, max);
        return productRepository.findByPriceBetween(min, max)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getInStockProducts() {
        log.info("Fetching in-stock products");
        return productRepository.findByStockGreaterThan(0)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummary> getActiveCategories() {
        log.info("Fetching active categories through category-service");
        return categoryServiceClient.getActiveCategories();
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product existing = findOrThrow(id);

        CategorySummary category = categoryServiceClient.getRequiredActiveCategory(request.getCategoryId());

        // Check duplicate name only if name actually changed
        if (!existing.getName().equalsIgnoreCase(request.getName())
                && productRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Product with name '" + request.getName() + "' already exists");
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStock(request.getStock());
        existing.setCategoryId(category.getId());
        existing.setCategory(category.getName());

        Product updated = productRepository.save(existing);
        log.info("Product updated successfully with id: {}", updated.getId());
        return mapToResponse(updated);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    private Product mapToEntity(ProductRequest request, CategorySummary category) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .categoryId(category.getId())
                .category(category.getName())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        CategorySummary categoryDetails = product.getCategoryId() == null
                ? null
                : categoryServiceClient.getCategoryById(product.getCategoryId());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategoryId())
                .categoryName(product.getCategory())
                .category(categoryDetails)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
