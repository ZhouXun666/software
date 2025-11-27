package com.school.secondhand.product.controller;

import com.school.secondhand.common.utils.SortUtils;
import com.school.secondhand.product.dto.CreateProductDTO;
import com.school.secondhand.product.dto.ProductDTO;
import com.school.secondhand.product.dto.UpdateProductDTO;
import com.school.secondhand.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SortUtils sortUtils;

    @Value("${file.upload-dir}")
    private String uploadDir;
    /**
     * 创建商品
     */
    @PostMapping("/create")
    public ResponseEntity<ProductDTO> createProduct(@ModelAttribute CreateProductDTO request) {
        ProductDTO productDTO = productService.createProduct(request);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * 上传图片，得到url
     */
    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {
        // 生成唯一文件名
        String fileName = UUID.randomUUID() +
                file.getOriginalFilename().substring(
                        file.getOriginalFilename().lastIndexOf("."));
        // 保存文件到本地
        Path filePath = Paths.get(uploadDir + fileName);
        file.transferTo(filePath);

        // 返回可访问的URL
        String imageUrl = uploadDir + fileName;
        System.out.println(imageUrl);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * 更新商品
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody UpdateProductDTO request) {
        ProductDTO productDTO = productService.updateProduct(id, request);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        return ResponseEntity.ok(productDTO);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取商品列表（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "0") int sort) {
        Sort sortType = sortUtils.getSort(sort);
        Pageable pageable = PageRequest.of(page, size,sortType);
        Page<ProductDTO> products = productService.getProducts(pageable);
        System.out.println(products);
        return ResponseEntity.ok(products);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProductsByName(
            @RequestParam String searchKeyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "0") int sort) {
        Sort sortType = sortUtils.getSort(sort);
        Pageable pageable = PageRequest.of(page, size,sortType);
        Page<ProductDTO> products = productService.searchProductsByName(searchKeyword, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * 根据分类获取商品列表
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "0") int sort) {
        Sort sortType = sortUtils.getSort(sort);
        Pageable pageable = PageRequest.of(page, size,sortType);
        Page<ProductDTO> products = productService.getProductsByCategory(pageable,categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * 获取用户发布的商品列表
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<ProductDTO>> getProductsByUserId(@PathVariable Long id) {
        List<ProductDTO> products = productService.getProductsByUserId(id);
        return ResponseEntity.ok(products);
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateProductStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return ResponseEntity.ok().build();
    }

}