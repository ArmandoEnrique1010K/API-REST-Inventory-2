package com.pe.inventoryapp.backend.deliveryorder.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.deliveryline.model.data.LineStatus;
import com.pe.inventoryapp.backend.deliveryline.model.entity.DeliveryLine;
import com.pe.inventoryapp.backend.deliveryline.repository.DeliveryLineRepository;
import com.pe.inventoryapp.backend.deliveryline.service.DeliveryLineDomainService;
import com.pe.inventoryapp.backend.deliveryorder.model.data.OrderStatus;
import com.pe.inventoryapp.backend.deliveryorder.model.entity.DeliveryOrder;
import com.pe.inventoryapp.backend.deliveryorder.model.mapper.DeliveryOrderMapper;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderComentRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.request.DeliveryOrderRequest;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderClientListResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderDetailsResponse;
import com.pe.inventoryapp.backend.deliveryorder.model.response.DeliveryOrderListResponse;
import com.pe.inventoryapp.backend.deliveryorder.repository.DeliveryOrderRepository;
import com.pe.inventoryapp.backend.deliveryorder.repository.specifications.DeliveryOrderSpecifications;
import com.pe.inventoryapp.backend.movement.model.data.MovementType;
import com.pe.inventoryapp.backend.movement.model.entity.Movement;
import com.pe.inventoryapp.backend.movement.repository.MovementRepository;
import com.pe.inventoryapp.backend.movement.service.MovementDomainService;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.stocklot.model.entity.Company;
import com.pe.inventoryapp.backend.stocklot.model.entity.StockLot;
import com.pe.inventoryapp.backend.stocklot.repository.CompanyRepository;
import com.pe.inventoryapp.backend.stocklot.repository.StockLotRepository;
import com.pe.inventoryapp.backend.stocklot.service.StockLotDomainService;
import com.pe.inventoryapp.backend.user.model.entity.User;
import com.pe.inventoryapp.backend.user.repository.UserRepository;

@Service
public class DeliveryOrderServiceImpl implements DeliveryOrderService {

	private final Model_DeliveryOrderDomainService model_DeliveryOrderDomainService;
	private final DeliveryOrderRepository deliveryOrderRepository;
	private final DeliveryLineRepository deliveryLineRepository;
	private final UserRepository userRepository;
	private final CompanyRepository companyRepository;
	private final StockLotRepository stockLotRepository;
	private final MovementRepository movementRepository;
	private final ModelRepository modelRepository;
	private final DeliveryOrderDomainService deliveryOrderDomainService;
	private final StockLotDomainService stockLotDomainService;
	private final MovementDomainService movementDomainService;
	private final DeliveryLineDomainService deliveryLineDomainService;

	private static final long BATCH_START = 10000L;

	public DeliveryOrderServiceImpl(
			DeliveryOrderRepository deliveryOrderRepository,
			DeliveryLineRepository deliveryLineRepository,
			UserRepository userRepository,
			CompanyRepository companyRepository,
			StockLotRepository stockLotRepository,
			MovementRepository movementRepository,
			ModelRepository modelRepository,
			DeliveryOrderDomainService deliveryOrderDomainService,
			StockLotDomainService stockLotDomainService,
			MovementDomainService movementDomainService, Model_DeliveryOrderDomainService model_DeliveryOrderDomainService, DeliveryLineDomainService deliveryLineDomainService) {
		this.deliveryOrderRepository = deliveryOrderRepository;
		this.deliveryLineRepository = deliveryLineRepository;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
		this.stockLotRepository = stockLotRepository;
		this.movementRepository = movementRepository;
		this.modelRepository = modelRepository;
		this.deliveryOrderDomainService = deliveryOrderDomainService;
		this.stockLotDomainService = stockLotDomainService;
		this.movementDomainService = movementDomainService;
		this.model_DeliveryOrderDomainService = model_DeliveryOrderDomainService;
		this.deliveryLineDomainService = deliveryLineDomainService;
	};

	@Override
	@Transactional
	public void saveDeliveryOrder(DeliveryOrderRequest deliveryOrderRequest, Long id_user) {
		Long id_client = deliveryOrderRequest.getUserIdClient();

		if (id_user == null || id_client == null) {
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
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
		deliveryOrder.setOrderStatus(OrderStatus.ORDER_PENDING);
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

		Specification<DeliveryOrder> spec = (DeliveryOrderSpecifications.hasStatus(status))
				.and(DeliveryOrderSpecifications.userClientNameContains(userClientName))
				.and(DeliveryOrderSpecifications.batchContains(batch))
				.and(DeliveryOrderSpecifications.priorityDateBetween(startDate, endDate))
				.and(DeliveryOrderSpecifications.isNotCanceled());

		Pageable sortedPageable = PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				Sort.by("createdAt").descending());

		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAll(
				spec,
				sortedPageable);

		// Page<DeliveryOrder> deliveryOrders =
		// deliveryOrderRepository.findAllByParams(pageable, batch, startDate, endDate,
		// status, userClientName);

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
				deliveryOrders.isFirst(),
				deliveryOrders.isLast());

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
		// Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAllActiveByParams(pageable, batch, startDate,
		// 		endDate, userClientName);
		Specification<DeliveryOrder> spec = (DeliveryOrderSpecifications.isActiveForOperator())
				.and(DeliveryOrderSpecifications.userClientNameContains(userClientName))
				.and(DeliveryOrderSpecifications.batchContains(batch))
				.and(DeliveryOrderSpecifications.priorityDateBetween(startDate, endDate));

		Pageable sortedPageable = PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				Sort.by("createdAt").descending());

		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAll(
				spec,
				sortedPageable);

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
				deliveryOrders.isFirst(),
				deliveryOrders.isLast());

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
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		Specification<DeliveryOrder> spec =
				(DeliveryOrderSpecifications.hasUserClient(id))
				.and(DeliveryOrderSpecifications.isNotCanceled())
				.and(DeliveryOrderSpecifications.hasStatus(status))
				.and(DeliveryOrderSpecifications.batchContains(batch))
				.and(DeliveryOrderSpecifications.priorityDateBetween(startDate, endDate));
		Pageable sortedPageable = PageRequest.of(
				pageable.getPageNumber(),
				pageable.getPageSize(),
				Sort.by("createdAt").descending());

		Page<DeliveryOrder> deliveryOrders = deliveryOrderRepository.findAll(
				spec,
				sortedPageable);

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
				deliveryOrders.isFirst(),
				deliveryOrders.isLast());

		return pageResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public DeliveryOrderDetailsResponse findDeliveryOrderById(Long id) {
		if (id == null) {
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		// TODO: EN ESTE CASO SI SE ESTA OPTIMIZANDO EL QUERY
		// DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id)
				DeliveryOrder deliveryOrder = deliveryOrderRepository.findByIdAndJoins(id)

				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_CANCELED) {
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
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findByIdAndJoins(id)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_CANCELED) {
			throw new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega ha sido cancelada");
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		// Si el usuario tiene solamente el rol de USER, entonces solamente podra ver
		// una orden cuyo userClient sea el mismo usuario que ha iniciado sesión
		// boolean isOnlyUserRole = user.getRoles().size() == 1 &&
		// user.getRoles().stream()
		// .anyMatch(r -> "ROLE_USER".equals(r.getName()));

		// if (isOnlyUserRole) {
		if (deliveryOrder.getUserClient().getId().equals(user.getId())) {
			return DeliveryOrderMapper.builder().setDeliveryOrder(deliveryOrder)
					.buildDeliveryOrderClientDetailsResponse();
		} else {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"El usuario no es el cliente de la orden de entrega");
		}
		// } else {
		// throw new BusinessException(
		// ResponseStatus.CONFLICT,
		// "El usuario no tiene el rol de cliente");
		// }
	}

	// Cambia la fecha limite de una orden de entrega
	@Override
	@Transactional
	public void changeLimitDate(Long id, LocalDateTime limitDate, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (limitDate == null) {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"La fecha límite es obligatoria");
		}

		if (limitDate.isBefore(LocalDateTime.now())) {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"La fecha límite debe ser igual o posterior a la fecha y hora actual");
		}

		if (deliveryOrder.getPriorityDate() != null && deliveryOrder.getPriorityDate().isAfter(limitDate)) {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"La fecha límite de entrega no puede ser anterior a la fecha límite prioritaria");
		}

		// CAMBIO DE LA FECHA LIMITE
		deliveryOrder.setLimitDate(limitDate);
		deliveryOrder.setUserUpdater(user);

		deliveryOrderRepository.save(deliveryOrder);
	}

	@Override
	@Transactional
	public void processDeliveryOrderCancellation(Long id, DeliveryOrderComentRequest deliveryOrderComentRequest,
			Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		// Verifica que la orden de entrega no tenga el estado de
		// MOVEMENT_LINE_DELIVERED o MOVEMENT_LINE_CANCELED,

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		// Lista de las lineas de entrega asociadas a la orden de entrega
		List<DeliveryLine> deliveryLines = deliveryLineDomainService.findAllByDeliveryOrderId(id);

		// SOLAMENTE SI NO HAY LINEAS DE ENTREGA, PODRA SER ELIMINADA LA ORDEN DE
		// ENTREGA SIN NINGUN PROBLEMA
		if (deliveryLines.isEmpty()) {
			deliveryOrder.setOrderStatus(OrderStatus.ORDER_CANCELED);
			deliveryOrder.setUserUpdater(user);
			deliveryOrderRepository.save(deliveryOrder);
			return; // <-- TERMINA LA EJECUCION DEL CÓDIGO
		}

		// OTRA FORMA DE IMPLEMENTARLO
		// EnumSet<LineStatus> cancelableStates = EnumSet.of(
		// LineStatus.LINE_READY,
		// LineStatus.LINE_PENDING,
		// LineStatus.LINE_EXCEEDED);

		// boolean hasCancelableLines = deliveryLines.stream()
		// .anyMatch(dl -> cancelableStates.contains(dl.getLineStatus()));

		// Verifica si quedan lineas de entrega con estado de READY, PENDING O EXCEEDED
		// EJEMPLO: CUANDO PULSA EL BOTON DE CANCELAR ORDEN POR 2° VEZ
		boolean hasCancelableLines = deliveryLines.stream()
				.anyMatch(dl -> dl.getLineStatus() == LineStatus.LINE_READY ||
						dl.getLineStatus() == LineStatus.LINE_PENDING ||
						dl.getLineStatus() == LineStatus.LINE_EXCEEDED);

		if (!hasCancelableLines) {
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"No hay líneas pendientes para cancelar");
		}

		// Si las lineas de entrega tienen el estado READY, PENDING o EXCEEDED, entonces
		// se cancelan solamente esas lineas de entrega y se restaura el stock de los
		// modelos de productos asociados a esas lineas de entrega, además se crean
		// movimientos de tipo MOVEMENT_LINE_CANCELED por cada linea de entrega
		// cancelada y por
		// cada producto restaurado

		Map<Long, Integer> stockToRestore = cancelDeliveryLines(deliveryLines, deliveryOrder, user,
				deliveryOrderComentRequest);

		restoreStock(stockToRestore, deliveryOrder, user, deliveryOrderComentRequest);

		// Verifica si existe al menos una línea de entrega con estado
		// MOVEMENT_LINE_DELIVERED o
		// MOVEMENT_LINE_MISSING
		// Devuelve false si no hay ninguna línea de entrega con esos estados
		// boolean hasDeliveredOrMissing = deliveryLines.stream()
		// .anyMatch(dl -> dl.getLineStatus() == LineStatus.LINE_DELIVERED ||
		// dl.getLineStatus() == LineStatus.LINE_MISSING);

		// Nota: Este método tambien contiene la logica para guardarlo en el repositorio
		updateDeliveryOrderStatus(deliveryOrder, user, deliveryLines);
	}

	@Override
	@Transactional
	public void markDeliveryOrderAsDelivered(Long id, Long id_user) {
		if (id == null || id_user == null) {
			throw new BusinessException(ResponseStatus.BAD_REQUEST);
		}

		User user = userRepository.findById(id_user)
				.orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El usuario no existe"));

		DeliveryOrder deliveryOrder = deliveryOrderRepository.findById(id).orElseThrow(
				() -> new BusinessException(ResponseStatus.NOT_FOUND, "La orden de entrega no existe"));

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_DELIVERED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ya ha sido entregada");
		}

		if (deliveryOrder.getOrderStatus() == OrderStatus.ORDER_CANCELED) {
			throw new BusinessException(ResponseStatus.CONFLICT,
					"La orden de entrega ha sido cancelada");
		}

		// DEBE HABER AL MENOS UNA LINEA DE ENTREGA COMO MINIMO
		List<DeliveryLine> deliveryLines = deliveryLineDomainService.findAllByDeliveryOrderId(id);

		if (deliveryLines.isEmpty()) {
			throw new BusinessException(ResponseStatus.CONFLICT, "No hay líneas de entrega");
		}

		// VERIFICA QUE TODAS LAS LINEAS DE ENTREGA ASOCIADAS A ESTA ORDEN DE ENTREGA
		// TENGAN EL ESTADO READY, ADEMÁS DEL ESTADO MOVEMENT_LINE_DELIVERED
		deliveryOrderDomainService.assertAllLinesInAllowedStates(
				deliveryLines,
				EnumSet.of(LineStatus.LINE_READY, LineStatus.LINE_DELIVERED, LineStatus.LINE_CANCELED,
						LineStatus.LINE_MISSING));

		// Ninguna linea de entrega debe tener el estado "PENDING", "EXCEEDED"
		// Estados permitidos: "READY", "MOVEMENT_LINE_DELIVERED",
		// "MOVEMENT_LINE_CANCELED", "MOVEMENT_LINE_MISSING"

		Map<Long, Integer> deliveredByModel = processDeliveredLines(deliveryLines, deliveryOrder, user);

		// SE ACTUALIZARA LA CANTIDAD TOTAL DEL MODELO DEL PRODUCTO QUE SE ENTREGO
		applyDeliveredStock(deliveredByModel);

		// NOTA: NO SE RECALCULARAN SUMATORIAS, PORQUE NO IMPLICA QUE SE VAN A ALTERAR
		// LAS CANTIDADES
		// recalculateSummaries(deliveryOrder);

		// Calcular la sumatoria de las cantidades de los modelos de productos de las
		// lineas de entrega que tienen el estado READY y acumularlas en el
		// totalDelivered

		deliveryOrder.setOrderStatus(OrderStatus.ORDER_DELIVERED);
		deliveryOrder.setUserUpdater(user);

		deliveryOrderRepository.save(deliveryOrder);

	}

	private Map<Long, Integer> cancelDeliveryLines(
			List<DeliveryLine> lines,
			DeliveryOrder order,
			User user,
			DeliveryOrderComentRequest commentRequest) {

		// Estados permitidos que debe tener la linea de entrega
		EnumSet<LineStatus> cancelableStates = EnumSet.of(LineStatus.LINE_READY, LineStatus.LINE_PENDING,
				LineStatus.LINE_EXCEEDED);

		// UNA ORDEN DE ENTREGA TIENE VARIOS MODELOS DE PRODUCTOS A LA VEZ
		// La nueva cantidad de los modelos de los productos y sus cantidades se
		// almacenaran en el almacen mediante un mapeo
		// Key: ID del modelo, Value: canitdad
		Map<Long, Integer> stockToRestore = new HashMap<>();
		List<Movement> movements = new ArrayList<>();

		// Solamente las lineas actualizadas
		List<DeliveryLine> updatedLines = new ArrayList<>();

		// Solamente alterara el estado de las lineas de entrega que tengan los estados
		// mencionados, las demás lineas de entrega no se alteran
		for (DeliveryLine line : lines) {
			if (!cancelableStates.contains(line.getLineStatus()))
				continue;

			int quantityToRestore = line.getDeliveredQuantity();
			// Long modelId = line.getModel().getId();

			// Almacena en el map el id del modelo del producto y la cantidad que se
			// almacenara
			if (quantityToRestore > 0) {
				stockToRestore.merge(
						line.getModel().getId(),
						quantityToRestore,
						// (a, b) -> a + b);
						Integer::sum);
			}

			// Cancelación de la línea (idealmente método de dominio)
			cancelLine(line, user);

			updatedLines.add(line); // solo las modificadas

			// Movimiento
			if (quantityToRestore > 0) {
				movements.add(buildCancelMovement(
						line,
						order,
						user,
						quantityToRestore,
						commentRequest));
			}
		}

		if (!updatedLines.isEmpty()) {
			deliveryLineRepository.saveAll(updatedLines);
		}

		// GUARDAR TODOS LOS MOVIMIENTOS
		if (!movements.isEmpty()) {
			movementRepository.saveAll(movements);
			movementDomainService.deleteManyLatestMovements();
		}

		Set<Long> modelIds = updatedLines.stream()
				.map(dl -> dl.getModel().getId())
				.collect(Collectors.toSet());

		for (Long modelId : modelIds) {
			model_DeliveryOrderDomainService.recalculateSummaries(order.getId(), modelId);
		}

		return stockToRestore;
	}

	private void cancelLine(DeliveryLine line, User user) {
		// * NO ALTERAR EL REPOSITORIO PARA FILTRAR POR CANTIDADES
		// NO SE VAN A ALTERAR LOS CAMPOS CON LAS CANTIDADES REQUERIDAS Y ENTREGADAS
		line.setLineStatus(LineStatus.LINE_CANCELED);
		line.setLimitDate(null);
		line.setUserUpdater(user);
	}

	private Movement buildCancelMovement(
			DeliveryLine line,
			DeliveryOrder order,
			User user,
			int quantity,
			DeliveryOrderComentRequest commentRequest) {

		Movement movement = new Movement();
		movement.setQuantity(quantity);
		movement.setComment(
				movementDomainService.generateComment(
						commentRequest.getMovementComment(),
						"Se canceló la orden de entrega de la factura #" + order.getBatch()));
		movement.setMovementType(MovementType.MOVEMENT_LINE_CANCELED);
		movement.setUser(user);
		movement.setDeliveryLine(line);
		movement.setModel(line.getModel());

		return movement;
	}

	private void restoreStock(
			Map<Long, Integer> stockToRestore,
			DeliveryOrder deliveryOrder,
			User user,
			DeliveryOrderComentRequest deliveryOrderComentRequest) {

		if (stockToRestore.isEmpty())
			return;

		Company company = companyRepository.findById(1L)
				.orElseThrow(() -> new BusinessException(
						ResponseStatus.NOT_FOUND,
						"La empresa no existe"));

		Map<Long, Model> models = modelRepository.findAllById(stockToRestore.keySet())
				.stream()
				.collect(Collectors.toMap(Model::getId, Function.identity()));

		List<StockLot> stockLots = new ArrayList<>();
		List<Movement> movements = new ArrayList<>();

		// Crear varios lotes de stock por cada uno de los modelos de los productos

		// Map.Entry es una subinterfaz de map, se utiliza para iterar las entradas
		// (mediante el método entrySet) permitiendo modificar los valores con el método
		// setValue

		for (Map.Entry<Long, Integer> entry : stockToRestore.entrySet()) {
			Model model = models.get(entry.getKey());
			Integer restoredQuantity = entry.getValue();

			// Si no hay cantidad a almacenar, pasa a la siguiente iteración
			if (restoredQuantity <= 0)
				continue;

			// Validacion en caso de que la cantidad tomada del modelo sea menor que la
			// cantidad que se va a restaurar
			if (model.getTotalQuantityTaken() < restoredQuantity) {
				throw new BusinessException(
						ResponseStatus.CONFLICT,
						"Stock inconsistente: cantidad tomada insuficiente");
			}

			// StockLot lot = createStockLot(model, qty, company);
			// stockLots.add(lot);

			// Crea el nuevo lote de stock
			StockLot stockLot = new StockLot();
			stockLot.setBatch(
					stockLotDomainService.resolveBatch(model.getProduct().getName(), model.getName(), company.getName()));
			stockLot.setQuantityReceived(restoredQuantity);
			stockLot.setQuantityAvailable(restoredQuantity);
			stockLot.setQuantityDelivered(0);
			stockLot.setQuantityLost(0);
			stockLot.setQuantityRecovered(0);
			stockLot.setZeroStock(false);
			stockLot.setTemporary(true);
			stockLot.setModel(model);
			stockLot.setCompany(company);
			stockLots.add(stockLot);

			// movements.add(createRefundMovement(
			// model, lot, qty, order, user, commentRequest));
			Movement movement = new Movement();
			movement.setQuantity(restoredQuantity);
			movement.setComment(movementDomainService.generateComment(
					deliveryOrderComentRequest.getMovementComment(),
					"Devolución de productos de la factura #" + deliveryOrder.getBatch()));
			movement.setMovementType(MovementType.MOVEMENT_STOCK_REFUND);
			movement.setUser(user);
			movement.setStockLotReceiver(stockLot);
			movement.setStockLotEmitter(null);
			movement.setDeliveryLine(null);
			movement.setModel(model);
			movements.add(movement);

			// Debe actualizar los campos que contiene la sumatoria
			// No altera los campos de quantityReceived, quantityDelivered, quantityLost ni
			// quantityRecovered
			model.setTotalQuantityAvailable(
					model.getTotalQuantityAvailable() + restoredQuantity);

			model.setTotalQuantityTaken(
					model.getTotalQuantityTaken() - restoredQuantity);

		}

		stockLotRepository.saveAll(stockLots);
		movementRepository.saveAll(movements);
		movementDomainService.deleteManyLatestMovements();
		modelRepository.saveAll(models.values());
	}

	private void updateDeliveryOrderStatus(
			DeliveryOrder order,
			User user,
			List<DeliveryLine> deliveryLines) {

		boolean allCanceled = deliveryLines.stream()
				.allMatch(dl -> dl.getLineStatus() == LineStatus.LINE_CANCELED);

		boolean anyDeliveredOrMissing = deliveryLines.stream()
				.anyMatch(dl -> dl.getLineStatus() == LineStatus.LINE_DELIVERED ||
						dl.getLineStatus() == LineStatus.LINE_MISSING);

		if (allCanceled) {
			order.setOrderStatus(OrderStatus.ORDER_CANCELED);
		} else if (anyDeliveredOrMissing) {
			order.setOrderStatus(OrderStatus.ORDER_PARTIALLY_DELIVERED);
		} else {
			// * Esto nunca deberia pasar
			throw new BusinessException(
					ResponseStatus.CONFLICT,
					"Estado inconsistente en las líneas de entrega");
		}

		order.setPriorityDate(deliveryOrderDomainService.getClosestLimitDate(order.getId()));
		order.setUserUpdater(user);

		deliveryOrderRepository.save(order);
	}

	private Map<Long, Integer> processDeliveredLines(
			List<DeliveryLine> deliveryLines,
			DeliveryOrder deliveryOrder,
			User user) {

		Map<Long, Integer> deliveredByModel = new HashMap<>();
		List<Movement> movements = new ArrayList<>();

		for (DeliveryLine deliveryLine : deliveryLines) {

			// Solamente van a entrar al bloque todas las lineas que tengan el estado de
			// LINE_READY
			if (deliveryLine.getLineStatus() != LineStatus.LINE_READY)
				continue;

			int qty = deliveryLine.getDeliveredQuantity();

			if (deliveryLine.getDeliveredQuantity() != deliveryLine.getRequiredQuantity()) {
				throw new BusinessException(
						ResponseStatus.CONFLICT,
						"Una de las lineas de entrega no cumple con la cantidad requerida para ser entregada");
			}

			deliveredByModel.merge(
					deliveryLine.getModel().getId(),
					qty,
					Integer::sum);

			deliveryLine.setLineStatus(LineStatus.LINE_DELIVERED);
			deliveryLine.setUserUpdater(user);

			// movements.add(createDeliveredMovement(deliveryLines, deliveryOrder, user,
			// qty));
			Movement movement = new Movement();
			movement.setQuantity(deliveryLine.getDeliveredQuantity());
			movement.setComment("Se entrego la linea de entrega correspondiente a la factura #" + deliveryOrder.getBatch());
			movement.setMovementType(MovementType.MOVEMENT_LINE_DELIVERED);
			movement.setUser(user);
			movement.setStockLotReceiver(null);
			movement.setStockLotEmitter(null);
			movement.setDeliveryLine(deliveryLine);
			movement.setModel(deliveryLine.getModel());
			movements.add(movement);
		}

		deliveryLineRepository.saveAll(deliveryLines);
		movementRepository.saveAll(movements);
		movementDomainService.deleteManyLatestMovements();

		return deliveredByModel;
	}

	private void applyDeliveredStock(Map<Long, Integer> deliveredByModel) {

		if (deliveredByModel.isEmpty())
			return;

		Map<Long, Model> models = modelRepository.findAllById(deliveredByModel.keySet())
				.stream()
				.collect(Collectors.toMap(Model::getId, Function.identity()));

		for (Map.Entry<Long, Integer> entry : deliveredByModel.entrySet()) {
			Model model = models.get(entry.getKey());
			Integer qty = entry.getValue();

			model.setTotalQuantityTaken(
					model.getTotalQuantityTaken() - qty);

			model.setTotalQuantityDelivered(
					model.getTotalQuantityDelivered() + qty);
		}

		modelRepository.saveAll(models.values());
	}
}
