package com.pain.flame.mybatis.mapper;

import com.pain.flame.mybatis.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {
    int insertProduct(Product product);

    Product findProductById(@Param("id") Long id);
}
