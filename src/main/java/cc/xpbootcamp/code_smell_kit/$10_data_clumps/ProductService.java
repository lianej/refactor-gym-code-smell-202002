package com.thoughtworks.tw_mall_project.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 在这里可以把order，pageSize, pageNum封装成spring的PageRequest传递，减少参数的直接传递
    public List<Product> getAll(String order, int pageSize, int pageNum, int maxPrice, int minPrice, String category) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize,
                new Sort(order.equals("ASC") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, "price"));
        List<Product> productList = productRepository.findAll(pageRequest).getContent();
        if (maxPrice != 0) {
            productList = productList.stream().filter(product -> product.getPrice() <= maxPrice).collect(Collectors.toList());
        }
        if (minPrice != 0) {
            productList = productList.stream().filter(product -> product.getPrice() >= minPrice).collect(Collectors.toList());
        }
        if (!category.isEmpty()) {
            productList = productList.stream().filter(product -> product.getCategory().equals(category)).collect(Collectors.toList());
        }
        return productList;
    }

    public Product get(Long id) throws RuntimeException {
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }
}
