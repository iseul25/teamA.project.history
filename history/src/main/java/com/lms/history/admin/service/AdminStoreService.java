package com.lms.history.admin.service;

import com.lms.history.admin.repository.AdminStoreRepository;
import com.lms.history.pointStore.entity.PointStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AdminStoreService {
    private final AdminStoreRepository adminStoreRepository;
    private final String uploadDir = "src/main/resources/static/images/store/";

    public AdminStoreService(AdminStoreRepository adminStoreRepository) {
        this.adminStoreRepository = adminStoreRepository;
    }

    public Map<String, Object> getProductsWithPaging(int page, int size) {
        List<PointStore> products = adminStoreRepository.findProductsByPage(page, size);
        int totalProducts = adminStoreRepository.countAllProducts();
        int totalPages = (int) Math.ceil((double) totalProducts / size);

        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalProducts", totalProducts);
        result.put("pageSize", size);

        return result;
    }

    // 상품 추가 (매개변수 수정)
    @Transactional
    public PointStore addProduct(String itemName, int cost, String category, String brand, MultipartFile imgFile) throws IOException {
        String imgUrl = saveImage(imgFile);

        PointStore product = new PointStore();
        product.setItemName(itemName);
        product.setCost(cost);
        product.setCategory(category);
        product.setBrand(brand);
        product.setImgUrl(imgUrl);

        return adminStoreRepository.saveProduct(product);
    }

    // 이미지 파일 저장 로직 (새로 추가)
    private String saveImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/store/" + fileName;
    }

    public boolean updateProduct(int itemId, String itemName, int cost, String category, String brand, MultipartFile imgFile) throws IOException {
        PointStore product = adminStoreRepository.findProductById(itemId);
        if (product == null) {
            return false;
        }

        String imgUrl = product.getImgUrl(); // 기존 이미지 URL 유지
        if (imgFile != null && !imgFile.isEmpty()) {
            imgUrl = saveImage(imgFile); // 새 이미지가 있으면 저장하고 URL 업데이트
        }

        return adminStoreRepository.updateProduct(itemId, itemName, cost, brand, category,  imgUrl);
    }

    public boolean deleteProduct(int itemId) {
        return adminStoreRepository.deleteProduct(itemId);
    }

    public PointStore getProductById(int itemId) {
        return adminStoreRepository.findProductById(itemId);
    }

    public Map<String, Object> getOrdersWithPaging(int page, int size) {
        List<Map<String, Object>> orders = adminStoreRepository.findOrdersByPage(page, size);
        int totalOrders = adminStoreRepository.countAllOrders();
        int totalPages = (int) Math.ceil((double) totalOrders / size);

        Map<String, Object> result = new HashMap<>();
        result.put("orders", orders);
        result.put("currentPage", page);
        result.put("totalPages", totalPages);
        result.put("totalOrders", totalOrders);
        result.put("pageSize", size);

        return result;
    }
}