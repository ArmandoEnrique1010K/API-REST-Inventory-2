package com.pe.inventoryapp.backend.movement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.movement.model.entity.Movement_StockLot;
import com.pe.inventoryapp.backend.movement.model.mapper.Movement_StockLotMapper;
import com.pe.inventoryapp.backend.movement.model.response.Movement_StockLotResponse;
import com.pe.inventoryapp.backend.movement.repository.Movement_StockLotRepository;

@Service
public class Movement_StockLotServiceImpl implements Movement_StockLotService{
  
  private final Movement_StockLotRepository movement_StockLotRepository;

  public Movement_StockLotServiceImpl(Movement_StockLotRepository movement_StockLotRepository) {
    this.movement_StockLotRepository = movement_StockLotRepository;
  }

  @Override
  public List<Movement_StockLotResponse> findAllByMovementId(Long movementId) {
    List<Movement_StockLot> movement_StockLots = (List<Movement_StockLot>) movement_StockLotRepository.findAllByMovementId(movementId);

    return movement_StockLots.stream()
        .map(movement_StockLot -> Movement_StockLotMapper.builder().setMovement_StockLot(
            movement_StockLot).buildMovement_StockLotResponse())
        .collect(Collectors.toList());

    
  }

  

}
