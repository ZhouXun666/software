package com.school.secondhand.product.repository;

import com.school.secondhand.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据用户ID查询商品列表
     */
    List<Product> findByUserId(Long userId);

    /**
     * 根据分类ID查询商品列表
     */
    Page<Product> findByCategoryId(Pageable pageable, Integer categoryId);

    /**
     * 根据状态查询商品列表
     */
    List<Product> findByStatus(Integer status);


    /**
     * 分页查询商品列表
     */
    @Query("SELECT p from Product p where p.status = 1")
    Page<Product> findAll(Pageable pageable);

    /**
     * 根据名称模糊查询商品列表
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据用户ID和状态查询商品列表
     */
    List<Product> findByUserIdAndStatus(Long userId, Integer status);

}