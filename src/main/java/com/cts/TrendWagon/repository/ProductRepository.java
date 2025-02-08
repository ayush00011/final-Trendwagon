package com.cts.TrendWagon.repository;

import com.cts.TrendWagon.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductDetail, Long> {
}