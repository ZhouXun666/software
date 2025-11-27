package com.school.secondhand.product.service;

import com.school.secondhand.product.dto.CreateProductDTO;
import com.school.secondhand.product.dto.ProductDTO;
import com.school.secondhand.product.dto.UpdateProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * 创建商品
     */
    ProductDTO createProduct(CreateProductDTO request);

    /**
     * 更新商品
     */
    ProductDTO updateProduct(Long id, UpdateProductDTO request);

    /**
     * 根据ID获取商品详情
     */
    ProductDTO getProductById(Long id);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 获取商品列表
     */
    Page<ProductDTO> getProducts(Pageable pageable);

    /**
     * 根据名称搜索商品
     */
    Page<ProductDTO> searchProductsByName(String name, Pageable pageable);

    /**
     * 根据分类获取商品列表
     */
    Page<ProductDTO> getProductsByCategory(Pageable pageable,Integer categoryId);

    /**
     * 获取用户发布的商品列表
     */
    List<ProductDTO> getProductsByUserId(Long userId);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long id, Integer status);

}