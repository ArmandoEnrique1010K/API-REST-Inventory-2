package com.pe.inventoryapp.backend.dashboard.service;

import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.dto.MovementDto;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.mapper.MovementMapper;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.dto.ModelDto;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.mapper.ModelMapper;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.dashboard.model.dto.PendingDeliveryOrdersDto;
import com.pe.inventoryapp.backend.dashboard.model.response.AdminDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.OperatorDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.UserDashboardResponse;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MovementRepository movementRepository;
    private final ModelRepository modelRepository;
    private final DeliveryOrderRepository deliveryOrderRepository;

    public DashboardServiceImpl(DeliveryOrderRepository deliveryOrderRepository, ModelRepository modelRepository,
            MovementRepository movementRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
        this.modelRepository = modelRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public UserDashboardResponse getSummaryByRoleUser(Long idUser) {
        Long quantityDeliveryOrdersPendingByUser = deliveryOrderRepository.countByOrderStatusPendingAndUser(idUser);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("priorityDate").ascending());
        List<PendingDeliveryOrdersDto> summaryDeliveryOrder = deliveryOrderRepository
                .summaryDeliveryOrdersPendingAndUser(
                        idUser, pageable);

        UserDashboardResponse result = new UserDashboardResponse(
                quantityDeliveryOrdersPendingByUser, // List
                summaryDeliveryOrder);

        return result;
    }

    @Override
    public OperatorDashboardResponse getSummaryByRoleOperator() {
        Long quantityDeliveryOrdersPending = deliveryOrderRepository.countByOrderStatusPending();

        Long modelsActive = modelRepository.countByModelsActive();

        Long lowerQuantityModels = modelRepository.countByLowerQuantityAvailable();

        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        Long nearCaducityDate = modelRepository.countByNearCaducityDate(today, nextWeek);

        // Lista de ordenes pendientes
        Pageable pageablePendingOrders = PageRequest.of(
                0,
                10,
                Sort.by("priorityDate").ascending());
        List<PendingDeliveryOrdersDto> summaryDeliveryOrder = deliveryOrderRepository.summaryDeliveryOrdersPending(
                pageablePendingOrders);

        // Lista de modelos con bajo stock
        Pageable pageableLowStock = PageRequest.of(
                0,
                10,
                Sort.by("totalQuantityAvailable").ascending());

        List<Model> summaryLowStock = modelRepository.findLowStockModels(pageableLowStock);

        List<ModelDto> result2 = summaryLowStock.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());

        // Modelos recientes
        Pageable pageableRecentModels = PageRequest.of(
                0,
                5,
                Sort.by("entryDate").ascending());

        List<Model> summaryRecentModels = modelRepository.findActiveModels(
                pageableRecentModels);

        List<ModelDto> result3 = summaryRecentModels.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());

        // Lista de modelos a punto de vencerse
        Pageable pageableNearCaducityDate = PageRequest.of(
                0,
                10,
                Sort.by("caducityDate").ascending());

        List<Model> summaryNearCaducityDate = modelRepository.findNearCaducityDate(pageableNearCaducityDate, today,
                nextWeek);

        List<ModelDto> result4 = summaryNearCaducityDate.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());

        OperatorDashboardResponse result = new OperatorDashboardResponse(
                quantityDeliveryOrdersPending, modelsActive, lowerQuantityModels, nearCaducityDate,
                summaryDeliveryOrder, result2, result3, result4

        );

        return result;

    }

  @Override
  public AdminDashboardResponse getSummaryByRoleAdmin() {
  
      Long quantityDeliveryOrdersPending = deliveryOrderRepository.countByOrderStatusPending();

      Long modelsActive = modelRepository.countByModelsActive();

      Long lowerQuantityModels = modelRepository.countByLowerQuantityAvailable();

      LocalDate today = LocalDate.now();
      LocalDate nextWeek = today.plusDays(7);

      LocalDateTime start = today.atStartOfDay();
      LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);

      Long nearCaducityDate = modelRepository.countByNearCaducityDate(today, nextWeek);

    // Movimientos realizados durante el dia
    Long movementsInDay = movementRepository.countByMovementsInDay(start, end);



      // Lista de ordenes pendientes
      Pageable pageablePendingOrders = PageRequest.of(
              0,
              10,
              Sort.by("priorityDate").ascending());
      List<PendingDeliveryOrdersDto> summaryDeliveryOrder = deliveryOrderRepository.summaryDeliveryOrdersPending(
              pageablePendingOrders);

      // Lista de modelos con bajo stock
      Pageable pageableLowStock = PageRequest.of(
              0,
              10,
              Sort.by("totalQuantityAvailable").ascending());

      List<Model> summaryLowStock = modelRepository.findLowStockModels(pageableLowStock);

      List<ModelDto> result2 = summaryLowStock.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());

      // Modelos recientes
      Pageable pageableRecentModels = PageRequest.of(
              0,
              5,
              Sort.by("entryDate").ascending());

      List<Model> summaryRecentModels = modelRepository.findActiveModels(
              pageableRecentModels);

      List<ModelDto> result3 = summaryRecentModels.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());

      // Lista de modelos a punto de vencerse
      Pageable pageableNearCaducityDate = PageRequest.of(
              0,
              10,
              Sort.by("caducityDate").ascending());

      List<Model> summaryNearCaducityDate = modelRepository.findNearCaducityDate(pageableNearCaducityDate, today,
              nextWeek);

      List<ModelDto> result4 = summaryNearCaducityDate.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelDto()).collect(Collectors.toList());


    // Lista de ultimos 10 movimientos realizados durante el dia de hoy
    Pageable pageableLastMovements = PageRequest.of(
            0,
            10,
            Sort.by("createdAt").ascending());

            List<Movement> lastMovementsList = movementRepository.findLastMovements(pageableLastMovements, start, end);

            List<MovementDto> result5 = lastMovementsList.stream().map(movement -> MovementMapper.builder().setMovement(movement).buildMovementDto()).collect(Collectors.toList());

      AdminDashboardResponse result = new AdminDashboardResponse(
              quantityDeliveryOrdersPending, modelsActive, lowerQuantityModels, nearCaducityDate, movementsInDay,
              summaryDeliveryOrder, result2, result3, result4, result5

      );

      return result;

  }
}

// public class UserDashboardResponse {
// private Long quantityDeliveryOrdersPending;
// private DeliveryOrderSummaryDto pendingDeliveryOrders;
// }

// public class DeliveryOrderSummaryDto {
// private Long id;
// private String batch;
// private LocalDateTime priorityDate;
// private Double percentage;
// }
