package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product Repository
 * Data access layer for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find all active products
     */
    List<Product> findByIsActiveTrue();
    
    /**
     * Find all active products with pagination
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find products by category with pagination
     */
    Page<Product> findByCategory(String category, Pageable pageable);
    
    /**
     * Find products by brand
     */
    List<Product> findByBrand(String brand);
    
    /**
     * Find products by name (case-insensitive search)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * Find product by SKU
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Check if SKU exists
     */
    boolean existsBySku(String sku);
    
    /**
     * Find products with low stock (stock less than threshold)
     */
    @Query("SELECT p FROM Product p WHERE p.stock < :threshold AND p.isActive = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}

