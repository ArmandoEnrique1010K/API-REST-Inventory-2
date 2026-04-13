package com.pe.inventoryapp.backend.product.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pe.inventoryapp.backend.common.data.ResponseStatus;
import com.pe.inventoryapp.backend.common.exception.BusinessException;
import com.pe.inventoryapp.backend.common.model.response.CloudinaryImageResponse;
import com.pe.inventoryapp.backend.common.model.response.PageResponse;
import com.pe.inventoryapp.backend.product.model.entity.Model;
import com.pe.inventoryapp.backend.product.model.entity.Product;
import com.pe.inventoryapp.backend.product.model.mapper.ModelMapper;
import com.pe.inventoryapp.backend.product.model.request.ModelRequest;
import com.pe.inventoryapp.backend.product.model.response.ModelDetailsResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchResponse;
import com.pe.inventoryapp.backend.product.model.response.ModelListSearchFirstTenResponse;
import com.pe.inventoryapp.backend.product.repository.ModelRepository;
import com.pe.inventoryapp.backend.product.repository.ProductRepository;
import com.pe.inventoryapp.backend.product.repository.specifications.ModelSpecifications;

@Service
public class ModelServiceImpl implements ModelService {
  private final ProductRepository productRepository;
  private final ModelRepository modelRepository;
  private final ModelDomainService modelDomainService;
  private final CloudinaryDomainService cloudinaryDomainService;

  public ModelServiceImpl(
      ProductRepository productRepository,
      ModelRepository modelRepository,
      ModelDomainService modelDomainService,
      CloudinaryDomainService cloudinaryDomainService) {
    this.productRepository = productRepository;
    this.modelRepository = modelRepository;
    this.modelDomainService = modelDomainService;
    this.cloudinaryDomainService = cloudinaryDomainService;
  }

  @Override
  @Transactional
  public void saveModelInProductId(ModelRequest modelRequest, MultipartFile file, Long productId) {
    String name = modelRequest.getName().trim();

    modelDomainService.verifyModelNameAvailableByProductId(name, productId);

    if (productId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND,
            "El producto no existe"));
    product.setQuantityModels(product.getQuantityModels() + 1);
    productRepository.save(product);

    // CONFIGURACION DE CLOUDINARY
    String urlImage = "";
    String publicImageId = "";

    if (file != null && !file.isEmpty()) {
      CloudinaryImageResponse image = cloudinaryDomainService.uploadImage(file);
      urlImage = image.imageUrl();
      publicImageId = image.publicId();
      System.out.println("SE SUBIO UNA IMAGEN A CLOUDINARY");
    } else {
      System.out.println("EL USUARIO NO SUBIO UNA IMAGEN, COLOCANDO IMAGEN POR DEFECTO");
    }

    Model model = new Model();
    model.setName(name);
    model.setImageUrl(modelDomainService.resolveImageUrl(urlImage));
    model.setPublicImageId(publicImageId);
    // model.setEntryDate(modelDomainService.resolveAnyLocalDate(modelRequest.getEntryDate()));
    model.setEntryDate(modelRequest.getEntryDate());

    model.setCaducityDate(modelRequest.getCaducityDate());
    model.setTotalQuantityAvailable(0);
    model.setTotalQuantityReceived(0);
    model.setTotalQuantityTaken(0);
    model.setTotalQuantityDelivered(0);
    model.setStatus(true);
    model.setProduct(product);

    modelRepository.save(model);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ModelListResponse> searchAllModelsByParams(Pageable pageable, String keyword, Integer minStock,
      Integer maxStock,
      LocalDate minEntryDate, LocalDate maxEntryDate, Boolean status, Long categoryId, Long typeId) {

    // if (categoryId != null && !categoryRepository.existsById(categoryId)) {
    //   throw new BusinessException(ResponseStatus.NOT_FOUND, "La categoria no existe");
    // }

    // if (typeId != null && !typeRepository.existsById(typeId)) {
    //   throw new BusinessException(ResponseStatus.NOT_FOUND, "El tipo no existe");
    // }

    // No utilizar una query
    // Page<Model> models = modelRepository.findAllByParams(pageable, keyword,
    // minStock, maxStock, minEntryDate,
    // maxEntryDate, status, categoryId, typeId);

    // Sino un specification

    // El metodo where esta deprecado, en su lugar utiliza unrestricted
    // Specification<Model> spec = Specification.where(null);
    Specification<Model> spec = Specification.unrestricted();

    spec = spec.and(ModelSpecifications.keywordContains(keyword));
    spec = spec.and(ModelSpecifications.stockBetween(minStock, maxStock));
    spec = spec.and(ModelSpecifications.entryDateBetween(minEntryDate, maxEntryDate));
    spec = spec.and(ModelSpecifications.hasStatus(status));
    spec = spec.and(ModelSpecifications.hasCategory(categoryId));
    spec = spec.and(ModelSpecifications.hasType(typeId));
    // spec = spec.and(ModelSpecifications.fetchRelations());

    // Para ordenar los elementos de forma descendente de acuerdo al ID se utiliza el siguiente codigo
    Pageable sortedPageable = PageRequest.of(
    pageable.getPageNumber(),
    pageable.getPageSize(),
    Sort.by("id").descending());

    //* Cuando usas Page, Spring Data JPA automáticamente ejecuta 2 queries
    //* SELECT ... FROM modelos JOIN ... LIMIT ?, ? → Cuenta el total de registros
    //* SELECT count(m1_0.id) FROM modelos m1_0 WHERE ... → Cuenta el total de
    // registros
    Page<Model> models = modelRepository.findAll(spec, sortedPageable);

    List<ModelListResponse> result = models.getContent().stream().map(
        model -> ModelMapper.builder()
            .setModel(model).buildModelListResponse())
        .toList();

    PageResponse<ModelListResponse> pageResponse = new PageResponse<>(
        result,
        models.getNumber(),
        models.getSize(),
        models.getTotalElements(),
        models.getTotalPages(),
        models.isFirst(),
        models.isLast());

    return pageResponse;
  }

  @Override
  public PageResponse<ModelListSearchResponse> searchAllModelsByName(Pageable pageable, String keyword) {
    // Page<Model> models = modelRepository.findAllActivesByName(pageable, keyword);

    // Otra forma de concatenar especificaciones
    Specification<Model> spec = (ModelSpecifications.isActive())
        .and(ModelSpecifications.keywordContains(keyword));

    Pageable sortedPageable = PageRequest.of(
        pageable.getPageNumber(),
        pageable.getPageSize(),
        Sort.by("id").descending());

    Page<Model> models = modelRepository.findAll(spec, sortedPageable);

    List<ModelListSearchResponse> result = models.getContent().stream().map(
        model -> ModelMapper.builder()
            .setModel(model).buildModelListSearchResponse())
        .toList();

    PageResponse<ModelListSearchResponse> pageResponse = new PageResponse<>(
        result,
        models.getNumber(),
        models.getSize(),
        models.getTotalElements(),
        models.getTotalPages(),
        models.isFirst(),
        models.isLast());

    return pageResponse;
  }

  @Override
  public List<ModelListResponse> findAllModelsByProductId(Long productId) {
    if (productId == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    List<Model> models = (List<Model>) modelRepository.findAllByProductId(productId);

    return models.stream()
        .map(model -> ModelMapper.builder().setModel(model).buildModelListResponse())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public ModelDetailsResponse findModelById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findByIdFull(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    // if (model.isStatus() == false) {
    // throw new BusinessException(ResponseStatus.CONFLICT, "El modelo se encuentra
    // inactivo");
    // }

    return ModelMapper.builder().setModel(model).buildModelResponse();
  }

  @Override
  @Transactional
  public void updateModelById(Long id, ModelRequest modelRequest, MultipartFile file) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    if (model.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El modelo se encuentra desactivado");
    }

    Long productId = model.getProduct().getId();

    Product product = productRepository.findById(productId).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto asociado al modelo no existe"));

    if (product.isStatus() == false) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El producto asociado al modelo se encuentra desactivado");
    }

    String newName = modelRequest.getName().trim();

    if (!model.getName().equals(newName)){
      modelDomainService.verifyModelNameAvailableByProductIdExcludingId(newName, productId, id);
      model.setName(newName);
    }


    // Implementación de Cloudinary
    // Solamente si el usuario ha subido una nueva imagen
    if (file != null && !file.isEmpty()) {

      // Debe borrar la imagen por el publicImageId (El ID que se asocia a la imagen
      // desde Cloudinary)

      // 1. Si ya había imagen → eliminarla
      if (model.getPublicImageId() != null && !model.getPublicImageId().isBlank()) {
        cloudinaryDomainService.deleteImage(model.getPublicImageId());
      }

      // 2. Subir nueva imagen
      CloudinaryImageResponse image = cloudinaryDomainService.uploadImage(file);

      // 3. Guardar nueva imagen
      model.setImageUrl(image.imageUrl());
      model.setPublicImageId(image.publicId());
      System.out
          .println("SE SUBIO UNA IMAGEN A CLOUDINARY, ELIMINANDO LA IMAGEN ANTERIOR Y REEMPLAZANDOLO POR LA NUEVA");
    } else {
      System.out.println("EL USUARIO NO SUBIO UNA IMAGEN, SE MANTIENE LA IMAGEN ANTERIOR");
    }

    // model.setImageUrl(modelDomainService.resolveImageUrl(modelRequest.getImageUrl()));
    // model.setEntryDate(modelDomainService.resolveAnyLocalDate(modelRequest.getEntryDate()));
    model.setEntryDate(modelRequest.getEntryDate());
    model.setCaducityDate(modelRequest.getCaducityDate());
    modelRepository.save(model);
  }

  @Override
  @Transactional
  public void changeStatusModelById(Long id) {
    if (id == null) {
      throw new BusinessException(ResponseStatus.BAD_REQUEST);
    }

    Model model = modelRepository.findById(id).orElseThrow(
        () -> new BusinessException(ResponseStatus.NOT_FOUND, "El modelo no existe"));

    // Verificar si el producto esta activo, si no esta activo debe devolver una
    // excepción
    Product product = productRepository.findById(model.getProduct().getId())
        .orElseThrow(() -> new BusinessException(ResponseStatus.NOT_FOUND, "El producto no existe"));

    if (!product.isStatus()) {
      throw new BusinessException(ResponseStatus.CONFLICT, "El producto se encuentra inactivo");
    }

    model.setStatus(!model.isStatus());
    modelRepository.save(model);
  }

  @Override
  public List<ModelListSearchFirstTenResponse> findFirstTenModelsByKeyword(String keyword) {
    // List<Model> models = (List<Model>) modelRepository.findAllFirstTenModelsByParams(keyword);

    // Limita a 10 los resultados
    // Y tambien se ordena por id de forma descendente
    Pageable pageable = PageRequest.of(
        0,
        10,
        Sort.by("id").descending());

    Specification<Model> spec = (ModelSpecifications.isActive())
        .and(ModelSpecifications.keywordContains(keyword));

    List<Model> models = modelRepository.findAll(spec, pageable).getContent();

    //* NO SE PUEDE IGNORAR EL SEGUNDO QUERY (COUNT) QUE SE HACE EN LA CONSOLA
    return models.stream().map(model -> ModelMapper.builder().setModel(model).buildModelListSearchFirstTenResponse())
        .collect(Collectors.toList());
  }

}
