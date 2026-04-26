package com.pe.inventoryapp.backend.dashboard.service;

import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryByClientResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderSummaryResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.model.mapper.MovementMapper;
import com.pe.inventoryapp.backend.movement.model.response.MovementsTodaySummaryResponse;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.mapper.ModelMapper;
import com.pe.inventoryapp.backend.product.model.response.ModelExpiringSoonSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelLowStockSummaryResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelRecentsSummaryResponse;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.dashboard.model.response.AdminDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.OperatorDashboardResponse;
import com.pe.inventoryapp.backend.dashboard.model.response.UserDashboardResponse;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final MovementRepository movementRepository;
    private final ModelRepository modelRepository;
    private final DeliveryOrderRepository deliveryOrderRepository;

    public DashboardServiceImpl(DeliveryOrderRepository deliveryOrderRepository, ModelRepository modelRepository,
            MovementRepository movementRepository, UserRepository userRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
        this.modelRepository = modelRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDashboardResponse getSummaryByRoleUser(Long idUser) {
        Long quantityDeliveryOrdersPendingByUser = deliveryOrderRepository.countByOrderStatusPendingAndUser(idUser);

    User user = userRepository.findByIdWithRoles(
                    idUser)
        .orElseThrow(() -> new BusinessException(
            ResponseStatus.NOT_FOUND,
            "El usuario no existe"));

            String userFullname = user.getFirstname() + " " + user.getLastname();


        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("priorityDate").ascending());

        List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.summaryDeliveryOrderPendingByUser(pageable, idUser);
        
        List<DeliveryOrderSummaryByClientResponse> result0 = deliveryOrders.stream().map(deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderSummaryByClientResponse()).collect(Collectors.toList());


        UserDashboardResponse result = new UserDashboardResponse(
                userFullname,
                quantityDeliveryOrdersPendingByUser, // List
                        result0);

        return result;
    }

    @Override
    public OperatorDashboardResponse getSummaryByRoleOperator(Long idUser) {
            User user = userRepository.findByIdWithRoles(
                            idUser)
                            .orElseThrow(() -> new BusinessException(
                                            ResponseStatus.NOT_FOUND,
                                            "El usuario no existe"));

            String userFullname = user.getFirstname() + " " + user.getLastname();

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

        List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.summaryDeliveryOrderPending(pageablePendingOrders);

        List<DeliveryOrderSummaryResponse> result0 = deliveryOrders.stream().map(deliveryOrder -> DeliveryOrderMapper
                        .builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderSummaryResponse())
                        .collect(Collectors.toList());
        // Lista de modelos con bajo stock
        Pageable pageableLowStock = PageRequest.of(
                0,
                10,
                Sort.by("totalQuantityAvailable").ascending());

        List<Model> summaryLowStock = modelRepository.findLowStockModels(pageableLowStock);

        List<ModelLowStockSummaryResponse> result2 = summaryLowStock.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelLowStockSummaryResponse()).collect(Collectors.toList());

        // Modelos recientes
        Pageable pageableRecentModels = PageRequest.of(
                0,
                5,
                Sort.by("entryDate").ascending());

        List<Model> summaryRecentModels = modelRepository.findActiveModels(
                pageableRecentModels);

        List<ModelRecentsSummaryResponse> result3 = summaryRecentModels.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelRecentsSummaryResponse()).collect(Collectors.toList());

        // Lista de modelos a punto de vencerse
        Pageable pageableNearCaducityDate = PageRequest.of(
                0,
                10,
                Sort.by("caducityDate").ascending());

        List<Model> summaryNearCaducityDate = modelRepository.findNearCaducityDate(pageableNearCaducityDate, today,
                nextWeek);

        List<ModelExpiringSoonSummaryResponse> result4 = summaryNearCaducityDate.stream()
                .map(summary -> ModelMapper.builder().setModel(summary).buildModelExpiringSoonSummaryResponse()).collect(Collectors.toList());

        OperatorDashboardResponse result = new OperatorDashboardResponse(userFullname,
                quantityDeliveryOrdersPending, modelsActive, lowerQuantityModels, nearCaducityDate,
                        result0, result2, result3, result4

        );

        return result;

    }

  @Override
  public AdminDashboardResponse getSummaryByRoleAdmin(Long idUser) {
          User user = userRepository.findByIdWithRoles(
                          idUser)
                          .orElseThrow(() -> new BusinessException(
                                          ResponseStatus.NOT_FOUND,
                                          "El usuario no existe"));

            String userFullname = user.getFirstname() + " " + user.getLastname();

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
      List<DeliveryOrder> deliveryOrders = deliveryOrderRepository.summaryDeliveryOrderPending(pageablePendingOrders);

      List<DeliveryOrderSummaryResponse> result0 = deliveryOrders.stream().map(deliveryOrder -> DeliveryOrderMapper
                      .builder().setDeliveryOrder(deliveryOrder).buildDeliveryOrderSummaryResponse())
                      .collect(Collectors.toList());
      // Lista de modelos con bajo stock
      Pageable pageableLowStock = PageRequest.of(
              0,
              10,
              Sort.by("totalQuantityAvailable").ascending());

      List<Model> summaryLowStock = modelRepository.findLowStockModels(pageableLowStock);

      List<ModelLowStockSummaryResponse> result2 = summaryLowStock.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelLowStockSummaryResponse()).collect(Collectors.toList());

      // Modelos recientes
      Pageable pageableRecentModels = PageRequest.of(
              0,
              5,
              Sort.by("entryDate").ascending());

      List<Model> summaryRecentModels = modelRepository.findActiveModels(
              pageableRecentModels);

      List<ModelRecentsSummaryResponse> result3 = summaryRecentModels.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelRecentsSummaryResponse()).collect(Collectors.toList());

      // Lista de modelos a punto de vencerse
      Pageable pageableNearCaducityDate = PageRequest.of(
              0,
              10,
              Sort.by("caducityDate").ascending());

      List<Model> summaryNearCaducityDate = modelRepository.findNearCaducityDate(pageableNearCaducityDate, today,
              nextWeek);

      List<ModelExpiringSoonSummaryResponse> result4 = summaryNearCaducityDate.stream()
              .map(summary -> ModelMapper.builder().setModel(summary).buildModelExpiringSoonSummaryResponse()).collect(Collectors.toList());


    // Lista de ultimos 10 movimientos realizados durante el dia de hoy
    Pageable pageableLastMovements = PageRequest.of(
            0,
            10,
            Sort.by("createdAt").ascending());

            List<Movement> lastMovementsList = movementRepository.findLastMovements(pageableLastMovements, start, end);

            List<MovementsTodaySummaryResponse> result5 = lastMovementsList.stream().map(movement -> MovementMapper.builder().setMovement(movement).buildMovementDto()).collect(Collectors.toList());

      AdminDashboardResponse result = new AdminDashboardResponse(userFullname,
              quantityDeliveryOrdersPending, modelsActive, lowerQuantityModels, nearCaducityDate, movementsInDay,
                      result0, result2, result3, result4, result5

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
