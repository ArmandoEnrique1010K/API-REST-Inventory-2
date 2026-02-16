package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.Model_DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderComentRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.Model_DeliveryOrderRepository;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.stocklot.service.StockLotDomainService;
import com.pe.inventoryapp.backend.summary.model.entity.Model_DeliveryOrder_Region;
import com.pe.inventoryapp.backend.summary.repository.Model_DeliveryOrder_RegionRepository;
import com.pe.inventoryapp.backend.summary.service.Model_DeliveryOrder_RegionDomainService;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

import jakarta.validation.constraints.NotNull;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {



	private final DeliveryOrderRepository deliveryOrderRepository;
	private final DeliveryLineRepository deliveryLineRepository;
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;
	private final ProductRepository productRepository;
	private final StockLotRepository stockLotRepository;
	private final MovementRepository movementRepository;
	private final ModelRepository modelRepository;
	private final Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository;
	private final Model_DeliveryOrderRepository model_DeliveryOrderRepository;
	private final DeliveryOrderDomainService deliveryOrderDomainService;
	private final StockLotDomainService stockLotDomainService;
	private final Model_DeliveryOrder_RegionDomainService model_DeliveryOrder_RegionDomainService;

	private static final long BATCH_START = 10000L;

	public DeliveryOrderServiceImpl(
			DeliveryOrderRepository deliveryOrderRepository,
			DeliveryLineRepository deliveryLineRepository,
			UserRepository userRepository,
			CompanyRepository companyRepository,
			ProductRepository productRepository,
			StockLotRepository stockLotRepository,
			MovementRepository movementRepository,
			ModelRepository modelRepository,
			Model_DeliveryOrder_RegionRepository model_DeliveryOrder_RegionRepository,
			Model_DeliveryOrderRepository model_DeliveryOrderRepository,
			DeliveryOrderDomainService deliveryOrderDomainService,
			StockLotDomainService stockLotDomainService,
			Model_DeliveryOrder_RegionDomainService model_DeliveryOrder_RegionDomainService) {
		this.deliveryOrderRepository = deliveryOrderRepository;
		this.deliveryLineRepository = deliveryLineRepository;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
		this.productRepository = productRepository;
		this.stockLotRepository = stockLotRepository;
		this.movementRepository = movementRepository;
		this.modelRepository = modelRepository;
		this.model_DeliveryOrder_RegionRepository = model_DeliveryOrder_RegionRepository;
		this.model_DeliveryOrderRepository = model_DeliveryOrderRepository;
		this.deliveryOrderDomainService = deliveryOrderDomainService;
		this.stockLotDomainService = stockLotDomainService;	
		this.model_DeliveryOrder_RegionDomainService = model_DeliveryOrder_RegionDomainService;
	};

	@Override
	@Transactional
	public void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {
		Long id_client = deliveryOrderRequest.getIdClient();

		if (id_user == null || id_client == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		User userClient = userRepository.findById(id_client).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "El cliente no existe"));

		DeliveryOrder deliveryOrder = new DeliveryOrder();

		deliveryOrder.setLimitDate(deliveryOrderRequest.getLimitDate());
		// La fecha limite prioritaria se establece en null porque aun no hay una fecha
		// de entrega de una linea de entrega
		deliveryOrder.setPriorityDate(null);
		deliveryOrder.setOrderStatus(OrderStatus.PENDING);
		deliveryOrder.setUserCreator(user);
		deliveryOrder.setUserUpdater(user);

		// Especifica el ID del cliente que se asignara a la orden de entrega
		deliveryOrder.setUserClient(userClient);

		DeliveryOrder saved = deliveryOrderRepository.save(deliveryOrder);

		// Este numero sera generado automaticamente
		Long newBatch = BATCH_START + saved.getId();
		String newBatchString = String.valueOf(newBatch);

		saved.setBatch(newBatchString);
		deliveryOrderRepository.save(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<DeliveryOrderListResponse> findAllDeliveryOrdersByParams(
			Pageable pageable,
			String batch,
			LocalDateTime startDate,
			LocalDateTime endDate,
			OrderStatus status,
			String userClientName) {
		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByParams(pageable, batch, startDate, endDate,
				status, userClientName);

		List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderListResponse())
				.toList();

		PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<DeliveryOrderListResponse> findAllActiveDeliveryOrdersByParams(
			Pageable pageable,
			String batch,
			LocalDateTime startDate,
			LocalDateTime endDate,
			String userClientName) {
		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllActiveByParams(pageable, batch, startDate,
				endDate, userClientName);

		List<DeliveryOrderListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderListResponse())
				.toList();

		PageResponse<DeliveryOrderListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<DeliveryOrderClientListResponse> findAllDeliveryOrderByClientId(
			Pageable pageable,
			Long id,
			String batch,
			LocalDateTime startDate,
			LocalDateTime endDate,
			OrderStatus status) {

		if (id == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllByUserClientId(pageable, id, batch, startDate,
				endDate, status);

		List<DeliveryOrderClientListResponse> result = deliveryOrders.getContent().stream().map(
				deliveryOrder -> DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderClientListResponse())
				.toList();

		PageResponse<DeliveryOrderClientListResponse> pageResponse = new PageResponse<>(
				result,
				deliveryOrders.getNumber(),
				deliveryOrders.getSize(),
				deliveryOrders.getTotalElements(),
				deliveryOrders.getTotalPages(),
				deliveryOrders.hasNext(),
				deliveryOrders.hasPrevious());

		return pageResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryOrderDetailsResponse findDeliveryOrderById(Long id) {
		if (id == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
		}

		return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
				.buildDeliveryOrderDetailsResponse();
	}

	// Encontrar una orden de entrega por su id y validar que el cliente de la orden
	// de entrega sea el mismo que el usuario que ha iniciado sesión, si no es asi,
	// entonces se lanza una excepción
	@Override
	@Transactional(readOnly = true)
	public DeliveryOrderClientDetailsResponse findDeliveryOrderByIdAndValidateUserClient(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		// Si el usuario tiene solamente el rol de USER, entonces solamente podra ver
		// una orden cuyo userClient sea el mismo usuario que ha iniciado sesión
		boolean isOnlyUserRole = user.getRoles().size() == 1 &&
				user.getRoles().stream()
						.anyMatch(r -> "ROLE_USER".equals(r.getName()));

		if (isOnlyUserRole) {
			if (deliveryOrder.getUserClient().getId().equals(user.getId())) {
				return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
						.buildDeliveryOrderClientDetailsResponse();
			} else {
				throw new BusinessException(
						ResponseStatus.CONFLICT,
						"El usuario no es el cliente de la orden de entrega");
			}
		} else {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"El usuario no tiene el rol de cliente");
		}
	}

	// Cambia la fecha limite de una orden de entrega
	@Override
	@Transactional
	public void changeLimitDate(Long id, LocalDateTime limitDate, Long id_user) {
		if (id == null || limitDate == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		deliveryOrder.setLimitDate(limitDate);
		deliveryOrder.setUserUpdater(user);

		deliveryOrderRepository.save(deliveryOrder);
	}


	@Override
	@Transactional
	public void processDeliveryOrderCancellation(Long id, DeliveryOrderComentRequest deliveryOrderComentRequest, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		// Verifica que la orden de entrega no tenga el estado de DELIVERED o CANCELED,
		if (deliveryOrder.getOrderStatus() == OrderStatus.DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		// Lista de las lineas de entrega asociadas a la orden de entrega
		List<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByDeliveryOrderId(id);

		// Verifica que haya al menos una linea de entrega
		if (deliveryLines.isEmpty()) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "No hay lineas de entrega");
		}

		// Si las lineas de entrega tienen el estado READY, PENDING o EXCEEDED, entonces
		// se cancelan solamente esas lineas de entrega y se restaura el stock de los
		// modelos de productos asociados a esas lineas de entrega, además se crean
		// movimientos de tipo CANCELED por cada linea de entrega cancelada y por
		// cada producto restaurado

		// Verifica si existe al menos una línea de entrega con estado DELIVERED o MISSING
		// Devuelve false si no hay ninguna línea de entrega con esos estados
		boolean hasDeliveredOrMissing = deliveryLines.stream().anyMatch(dl -> dl.getLineStatus() == LineStatus.DELIVERED ||
				dl.getLineStatus() == LineStatus.MISSING);

		// Estados permitidos que debe tener la linea de entrega
		EnumSet<LineStatus> cancelableStates = EnumSet.of(LineStatus.READY, LineStatus.PENDING, LineStatus.EXCEEDED);

		// UNA ORDEN DE ENTREGA TIENE VARIOS MODELOS DE PRODUCTOS A LA VEZ
		// La nueva cantidad de los modelos de los productos y sus cantidades se almacenaran en el almacen mediante un mapeo
		// Key: ID del modelo, Value: canitdad
		Map<Long, Integer> newStockRestoredQuantity = new HashMap<>();

		// Solamente alterara el estado de las lineas de entrega que tengan los estados
		// mencionados, las demás lineas de entrega no se alteran
		for (DeliveryLine deliveryLine : deliveryLines) {
			if (!cancelableStates.contains(deliveryLine.getLineStatus())) {
				// Omite las lineas de entrega que no tengan un estado permitido
				continue;
			}

			int quantityToRestore = deliveryLine.getDeliveredQuantity();

			// Almacena en el map el id del modelo del producto y la cantidad que se almacenara
			if (quantityToRestore > 0) {
				newStockRestoredQuantity.merge(
						deliveryLine.getModel().getId(),
						quantityToRestore,
						// (a, b) -> a + b);
						Integer::sum);
			}

			deliveryLine.setDeliveredQuantity(0);
			deliveryLine.setPendingQuantity(0);
			deliveryLine.setLineStatus(LineStatus.CANCELED);
			deliveryLine.setUserUpdater(user);
			deliveryLineRepository.save(deliveryLine);

			// Crea un nuevo movimiento
			Movement movement = new Movement();
			movement.setQuantity(quantityToRestore);
			movement.setComment(deliveryOrderDomainService.generateComment(
					deliveryOrderComentRequest.getComment(), "Se cancelo la orden de entrega de la factura #" + deliveryOrder.getBatch()));
			movement.setMovementType(MovementType.CANCELED);
			movement.setUser(user);
			movement.setStockLotReceiver(null);
			movement.setStockLotEmitter(null);
			movement.setDeliveryLine(deliveryLine);
			movement.setModel(deliveryLine.getModel());
			movementRepository.save(movement);
		}

		// Si habia lineas de entrega con el estado de DELIVERED o CANCELED, la orden de entrega tendra un nuevo estado respectivamente 
		deliveryOrder.setOrderStatus(
				hasDeliveredOrMissing ? OrderStatus.DELIVERED : OrderStatus.CANCELED);

		Company company = companyRepository.findById(1L).get();

		// Crear varios lotes de stock por cada uno de los modelos de los productos

		// Map.Entry es una subinterfaz de map, se utiliza para iterar las entradas (mediante el método entrySet) permitiendo modificar los valores con el método setValue
		for (Map.Entry<Long, Integer> modelProductEntry : newStockRestoredQuantity.entrySet()) {
			Long modelId = modelProductEntry.getKey();
			Integer restoredQuantity = modelProductEntry.getValue();

			// Si no hay cantidad a almacenar, pasa a la siguiente iteración
			if (restoredQuantity <= 0)
				continue;

			if (modelId == null) {
				throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
			}

			Model model = modelRepository.findById(modelId).orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

			// Crea el nuevo lote de stock
			StockLot stockLot = new StockLot();
			stockLot.setBatch(stockLotDomainService.resolveBatch(model.getProduct().getName(), model.getName()));
			stockLot.setQuantityReceived(restoredQuantity);
			stockLot.setQuantityAvailable(restoredQuantity);
			stockLot.setQuantityDelivered(0);
			stockLot.setQuantityLost(0);
			stockLot.setQuantityRecovered(0);
			stockLot.setZeroStock(false);
			stockLot.setTemporary(true);
			stockLot.setModel(model);
			stockLot.setCompany(company);
			stockLotRepository.save(stockLot);

			// Crea un nuevo movimiento por cada lote de stock
			Movement movement = new Movement();
			movement.setQuantity(restoredQuantity);
			movement.setComment(deliveryOrderDomainService.generateComment(
					deliveryOrderComentRequest.getComment(),
					"Devolución de productos de la factura #" + deliveryOrder.getBatch()));
			movement.setMovementType(MovementType.REFUND);
			movement.setUser(user);
			movement.setStockLotReceiver(restoredQuantity > 0 ? stockLot : null);
			movement.setStockLotEmitter(null);
			movement.setDeliveryLine(null);
			movement.setModel(model);
			movementRepository.save(movement);
		}

		// Itera con cada uno de los modelos de los productos devueltos
		for (Long modelId : newStockRestoredQuantity.keySet()) {
			// Busca la relación existente entre el modelo del producto y la orden de entrega
			Model_DeliveryOrder model_DeliveryOrder = model_DeliveryOrderRepository
					.findByModelIdAndDeliveryOrderId(modelId, deliveryOrder.getId())
					.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
							"No se encontró la relación entre el modelo del producto y la orden de entrega"));

			// Toma la sumatoria
			Integer newRequired = deliveryLineRepository.sumRequiredQuantityByDeliveryOrderIdAndModelId(
					deliveryOrder.getId(), modelId);

			model_DeliveryOrder.setRequiredQuantityTotal(newRequired);
			model_DeliveryOrderRepository.save(model_DeliveryOrder);

			// Recalcula la cantidad total
			model_DeliveryOrder_RegionDomainService.recalculateSummatoryModel_DeliveryOrderRegions(
					model_DeliveryOrder.getId());

			Model model = modelRepository.findById(modelId)
					.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo del producto no existe"));

			// Debe actualizar los campos que contiene la sumatoria
			// No altera los campos de quantityReceived, quantityDelivered, quantityLost ni quantityRecovered
			model.setTotalQuantityAvailable(model.getTotalQuantityAvailable() + newStockRestoredQuantity.get(modelId));
			modelRepository.save(model);
		}

		deliveryOrder.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(deliveryOrder.getId()));
		deliveryOrder.setUserUpdater(user);
		deliveryOrderRepository.save(deliveryOrder);

	}


	@Override
	@Transactional
	public void markDeliveryOrderAsDelivered(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.INTERNAL_SERVER_ERROR);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		List<DeliveryLine> deliveryLines = deliveryLineRepository.findAllByDeliveryOrderId(id);

		if (deliveryLines.isEmpty()) {
			throw new BusinessException(ResponseStatus.CONFLICT, "No hay líneas de entrega");
		}

		// VERIFICA QUE TODAS LAS LINEAS DE ENTREGA ASOCIADAS A ESTA ORDEN DE ENTREGA
		// TENGAN EL ESTADO READY, ADEMÁS DEL ESTADO DELIVERED
		assertAllLinesInAllowedStates(
				deliveryLines,
				EnumSet.of(LineStatus.READY, LineStatus.DELIVERED, LineStatus.CANCELED, LineStatus.MISSING));

		// Ninguna linea de entrega debe tener el estado "PENDING", "EXCEEDED"
		// Estados permitidos: "READY", "DELIVERED", "CANCELED", "MISSING"

		deliveryLines.stream()
				.filter(l -> l.getLineStatus() == LineStatus.READY)
				.forEach(l -> {
					l.setLineStatus(LineStatus.DELIVERED);
					l.setUserUpdater(user);

					Movement movement = new Movement();
					movement.setQuantity(l.getDeliveredQuantity());
					movement.setComment("Se entrego la linea de entrega");
					movement.setDeliveryLine(l);
					movement.setProduct(l.getProduct());
					movement.setUser(user);
					movement.setStockLotEmitter(null);
					movement.setStockLotReceiver(null);
					movement.setMovementType(MovementType.DELIVERED);
					movementRepository.save(movement);
				});

		boolean allCompleted = deliveryLines.stream()
				.allMatch(l -> l.getLineStatus() == LineStatus.DELIVERED ||
						l.getLineStatus() == LineStatus.CANCELED ||
						l.getLineStatus() == LineStatus.MISSING);

		if (allCompleted) {
			deliveryOrder.setOrderStatus(OrderStatus.DELIVERED);
			deliveryOrder.setUserUpdater(user);
			deliveryOrderRepository.save(deliveryOrder);
		}
	}
}
