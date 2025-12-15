package com.pe.inventoryapp.backend.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pe.inventoryapp.backend.stock.model.entity.StockLot;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {

}
