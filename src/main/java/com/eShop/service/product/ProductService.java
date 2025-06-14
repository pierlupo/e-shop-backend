package com.eShop.service.product;

import com.eShop.dto.CategoryDto;
import com.eShop.dto.ImageDto;
import com.eShop.dto.ProductDto;
import com.eShop.exceptions.AlreadyExistsException;
import com.eShop.exceptions.ProductNotFoundException;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Category;
import com.eShop.model.Image;
import com.eShop.model.Product;
import com.eShop.repository.CategoryRepository;
import com.eShop.repository.ImageRepository;
import com.eShop.repository.ProductRepository;
import com.eShop.request.AddProductRequest;
import com.eShop.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    // final ici permet au productRepository injecté d'être vraiment injecté (cf l'annotation @RequiredArgsConstructor)
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        //check if the category is found in the database
        //if yes, set it as the new product category
        //if no, save it as a new category
        // then set it as the new product category

        if(isProductAlreadyInDb(request.getProductName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getProductName()+ " " + request.getBrand() + " existe déjà en bdd, mettez-à jour la quantité plutôt!");
        }
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private boolean isProductAlreadyInDb(String productName, String brand) {
        return productRepository.existsByProductNameAndBrand(productName, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getProductName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresent(productRepository::delete);
    }

    @Override
    public Product updateProduct(ProductUpdateRequest product, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, product))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {

        existingProduct.setProductName(request.getProductName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        existingProduct.setCategory(category);
        return existingProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findBybrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndbrand(category, brand);
    }

    @Override
    public List<Product> getProductByName(String productName) {
        return productRepository.findByProductName(productName);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String productName) {
        return productRepository.findBybrandAndProductName(brand, productName);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String productName) {
        return productRepository.countBybrandAndProductName(brand, productName);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public ProductDto convertToDTO(Product product) {
        ProductDto productDTO = modelMapper.map(product, ProductDto.class);

        // Convert Category to CategoryDTO
        CategoryDto categoryDTO = new CategoryDto();
        categoryDTO.setId(product.getCategory().getId());
        categoryDTO.setName(product.getCategory().getName());
        productDTO.setCategory(categoryDTO);

        // Convert Images
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDTO.setImages(imageDtos);
        return productDTO;
    }

}