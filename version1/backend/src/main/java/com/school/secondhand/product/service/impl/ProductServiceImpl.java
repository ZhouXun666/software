package com.school.secondhand.product.service.impl;

import com.school.secondhand.product.dto.ProductDTO;
import com.school.secondhand.product.dto.CreateProductDTO;
import com.school.secondhand.product.dto.UpdateProductDTO;
import com.school.secondhand.product.entity.Product;
import com.school.secondhand.product.repository.ProductRepository;
import com.school.secondhand.product.service.ProductService;
import com.school.secondhand.user.entity.User;
import com.school.secondhand.user.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.school.secondhand.common.utils.SortUtils;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProductDTO createProduct(CreateProductDTO request) {
        // 创建商品
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImages(request.getImages());
        product.setCategoryId(request.getCategoryId());
        product.setUserId(request.getUserId());
        product.setStatus(1); // 默认为上架状态
        product.setViews(0); // 初始浏览量为0

        productRepository.save(product);

        // 构建DTO并填充用户信息
        return convertToDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, UpdateProductDTO request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 更新商品信息
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        productRepository.save(product);

        return convertToDTO(product);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 增加浏览量
        product.setViews(product.getViews() + 1);
        productRepository.save(product);

        return convertToDTO(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        Path filepath = Paths.get(product.getImages());
        try {
            Files.deleteIfExists(filepath);
        }catch(IOException e){
            System.out.println("商品图片不存在");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> getProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> searchProductsByName(String name, Pageable pageable) {
        Page<Product> products = productRepository.findByNameContaining(name, pageable);
        return products.map(this::convertToDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Pageable pageable, Integer categoryId) {
        Page<Product> products = productRepository.findByCategoryId(pageable, categoryId);
        return products.map(this::convertToDTO);
    }

    @Override
    public List<ProductDTO> getProductsByUserId(Long id) {
        List<Product> products = productRepository.findByUserId(id);
        return products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setStatus(status);
        productRepository.save(product);
    }

    /**
     * 将Product实体转换为ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        // 填充用户信息
        User user = userRepository.findById(product.getUserId())
                .orElse(null);
        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setUserphone(user.getPhone());
        }

        return dto;
    }
}