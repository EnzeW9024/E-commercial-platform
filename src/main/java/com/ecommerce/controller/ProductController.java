package com.ecommerce.controller;

import com.ecommerce.dto.ProductRequest;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Product Controller
 * Handles product catalog REST API endpoints
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * Get all products
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) Boolean activeOnly,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        if (category != null && !category.isEmpty()) {
            // Get products by category
            if (page > 0 || size != 10) {
                Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ProductResponse> products = productService.getProductsByCategory(category, pageable);
                return ResponseEntity.ok(products.getContent());
            } else {
                List<ProductResponse> products = productService.getProductsByCategory(category);
                return ResponseEntity.ok(products);
            }
        }
        
        if (search != null && !search.isEmpty()) {
            // Search products by name
            List<ProductResponse> products = productService.searchProductsByName(search);
            return ResponseEntity.ok(products);
        }
        
        if (activeOnly != null && activeOnly) {
            // Get only active products
            if (page > 0 || size != 10) {
                Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ProductResponse> products = productService.getAllActiveProducts(pageable);
                return ResponseEntity.ok(products.getContent());
            } else {
                List<ProductResponse> products = productService.getAllActiveProducts();
                return ResponseEntity.ok(products);
            }
        }
        
        // Get all products
        if (page > 0 || size != 10) {
            Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ProductResponse> products = productService.getAllProducts(pageable);
            return ResponseEntity.ok(products.getContent());
        } else {
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        }
    }
    
    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Create a new product
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    /**
     * Update an existing product
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }
    
    /**
     * Delete a product (hard delete)
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
    
    /**
     * Deactivate a product (soft delete)
     * PATCH /api/products/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable Long id) {
        ProductResponse product = productService.deactivateProduct(id);
        return ResponseEntity.ok(product);
    }
}

