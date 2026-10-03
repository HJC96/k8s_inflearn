package com.example.ordersystem.ordering.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    public Ordering orderCreate(OrderCreateDto orderDto){
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.parseLong(id)).orElseThrow(()-> new EntityNotFoundException("member is not found"));

        Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));
        int quantity = orderDto.getProductCount();
        if(product.getStockQuantity() < quantity){
            throw new IllegalArgumentException("재고 부족");
        }else {
            product.updateStockQuantity(orderDto.getProductCount());
        }
        Ordering ordering = Ordering.builder()
                .member(member)
                .product(product)
                .quantity(orderDto.getProductCount())
                .build();
        orderingRepository.save(ordering);
        return  ordering;
    }

}
