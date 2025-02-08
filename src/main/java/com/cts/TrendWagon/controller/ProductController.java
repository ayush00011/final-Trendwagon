package com.cts.TrendWagon.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cts.TrendWagon.dto.ProductUserView;
import com.cts.TrendWagon.model.ProductDetail;
import com.cts.TrendWagon.repository.ProductRepository;

@Controller
@RequestMapping("/api/products")
public class ProductController {

    private static final String IMAGE_DIRECTORY = "C:\\Users\\2376740\\OneDrive - Cognizant\\Desktop\\Product_Pictures";
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/user-view")
    @ResponseBody
    public ResponseEntity<?> getAllProductsForUsers() {
        List<ProductDetail> products = productRepository.findAll();

        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<ProductUserView> productDTOs = products.stream()
                .map(product -> new ProductUserView(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getImagePath()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getAllProducts() {
        List<ProductDetail> products = productRepository.findAll();
        
        if (products.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); 
        }
        return ResponseEntity.ok(products);
    }

    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<ProductDetail> product = productRepository.findById(id);
        
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }
    
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> addProduct(@RequestParam("name") String name,
                                             @RequestParam("description") String description,
                                             @RequestParam("price") double price,
                                             @RequestParam("quantity") int quantity,
                                             @RequestParam("image") MultipartFile image) {
        ProductDetail product = new ProductDetail();
        try {
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setImagePath("TEMP_IMAGE_PATH"); // Temporary placeholder
            product = productRepository.save(product);

            String imageName = product.getId() + "_product.jpg";
            Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
            Files.createDirectories(imagePath.getParent()); // Ensure directory exists
            image.transferTo(imagePath.toFile()); // Save the image

            product.setImagePath("/api/products/images/" + imageName);
            productRepository.save(product);

            return ResponseEntity.ok("Product added successfully!");
        } catch (IOException e) {
            logger.error("Error saving product image for '{}': {}", name, e.getMessage(), e);
            
            productRepository.deleteById(product.getId());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving product image: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") double price,
                                                @RequestParam("quantity") int quantity,
                                                @RequestParam(value = "image", required = false) MultipartFile image) {
        Optional<ProductDetail> optionalProduct = productRepository.findById(id);
        
        if (!optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        ProductDetail product = optionalProduct.get();

        try {
            // Update basic details
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);

            // Check if new image is provided
            if (image != null && !image.isEmpty()) {
                // Delete the old image if it exists
                Path oldImagePath = Paths.get(IMAGE_DIRECTORY, product.getId() + "_product.jpg");
                Files.deleteIfExists(oldImagePath);

                // Save the new image
                String newImageName = product.getId() + "_product.jpg";
                Path newImagePath = Paths.get(IMAGE_DIRECTORY, newImageName);
                Files.createDirectories(newImagePath.getParent());
                image.transferTo(newImagePath.toFile());

                // Update image path in database
                product.setImagePath("/api/products/images/" + newImageName);
            }

            productRepository.save(product);
            return ResponseEntity.ok("Product updated successfully!");
        } catch (IOException e) {
            logger.error("Error updating product '{}': {}", name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating product: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        Optional<ProductDetail> product = productRepository.findById(id);
        if (product.isPresent()) {
            File imageFile = new File(IMAGE_DIRECTORY, product.get().getId() + "_product.jpg");
            if (imageFile.exists()) {
                imageFile.delete();
            }
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    @GetMapping("/images/{imageName}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(IMAGE_DIRECTORY, imageName);
            byte[] imageBytes = Files.readAllBytes(imagePath);
            return ResponseEntity.ok().body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
