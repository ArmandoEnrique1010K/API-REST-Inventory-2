package com.pe.inventoryapp.backend.deliveryorder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.model.response.CommonResponse;
import com.pe.inventoryapp.backend.common.model.response.DataResponse;
import com.pe.inventoryapp.backend.common.service.ResponseService;
import com.pe.inventoryapp.backend.deliveryorder.model.response.ProductDeliveryOrderResponse;
import com.pe.inventoryapp.backend.deliveryorder.service.Product_DeliveryOrderService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/products-delivery-orders")
public class Product_DeliveryOrderController {
  @Autowired
  private Product_DeliveryOrderService product_DeliveryOrderService;

  @Autowired
  private ResponseService responseService;

  // @PostMapping("/{idDeliveryOrder}")
  // public ResponseEntity<CommonResponse> relationManyProductsToDeliveryOrder(@PathVariable Long idDeliveryOrder, @Valid @RequestBody Product_DeliveryOrderRequest product_DeliveryOrderRequest, BindingResult result) {
  //   validationService.validateFieldsAndThrowResponse(result);
  //   product_DeliveryOrderService.saveProduct_DeliveryOrder(product_DeliveryOrderRequest, idDeliveryOrder);

  //   return ResponseEntity.status(201)
  //       .body(responseService.generateCommonResponse("success", ResponseStatus.SUCCESS,
  //           "Se han agregado productos a la orden de entrega"));
  // }

  @PostMapping("/product/{productId}/deliveryOrder/{deliveryOrderId}")
  public ResponseEntity<CommonResponse> registerRelationProductToDeliveryOrder(@PathVariable Long productId, @PathVariable Long deliveryOrderId) {
    product_DeliveryOrderService.saveRelationProductInDeliveryOrder(productId, deliveryOrderId);

    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.CREATED,
        "Se ha agregado un producto a la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }
  

  @GetMapping("/products/deliveryOrder/{deliveryOrderId}")
  public ResponseEntity<?> listAllProductsByDeliveryOrder(@PathVariable Long deliveryOrderId) {
    List<ProductDeliveryOrderResponse> product_DeliveryOrderListResponses = product_DeliveryOrderService.findAllByDeliveryOrderId(
        deliveryOrderId);

    DataResponse<List<ProductDeliveryOrderResponse>> dataResponse = responseService.generateDataResponse(ResponseStatus.SUCCESS, 
        product_DeliveryOrderListResponses);
    return ResponseEntity.status(dataResponse.status()).body(dataResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CommonResponse> deleteProduct_DeliveryOrder(@PathVariable Long id) {
    product_DeliveryOrderService.deleteRelationProductDeliveryOrder(id);
    CommonResponse response = responseService.generateSucessfullResponse(ResponseStatus.SUCCESS,
        "Se ha eliminado un producto de la orden de entrega");
    return ResponseEntity.status(response.status()).body(response);
  }


  // TODO: AÑADIR OTROS METODOS PARA EL CONTROLADOR
  // ACTUALIZAR LA LISTA DE RELACIONES DE PRODUCTOS Y ORDENES DE ENTREGA POR SU ID

  // VERIFICAR QUE EL PRODUCTO AGREGADO TENGA UNA SUMATORIA DE CANTIDAD PENDIENTE A 0 DE CANTIDADES PENDIENTES EN LA ORDEN DE ENTREGA PARA QUE SE PUEDA ELIMINAR DE LA RELACIÓN

  
}
