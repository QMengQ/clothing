package com.example.clothing.controller;
import com.example.clothing.entity.Clothing;
import com.example.clothing.entity.Image;
import com.example.clothing.repository.ClothingRepository;
import com.example.clothing.repository.ImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clothing")
@CrossOrigin
public class ClothingController {

    private static final Logger logger = LoggerFactory.getLogger(ClothingController.class);

    @Autowired
    private ClothingRepository repository;

    @Autowired
    private ImageRepository imageRepository;

    // 图片存储目录
    @Value("${upload.dir:C:/Users/ASUS/Desktop/clothing-frontend/clothing/uploads}")
    private String uploadDir;

    // 图片访问基础URL
    @Value("${upload.base-url:http://localhost:8080/uploads}")
    private String uploadBaseUrl;

    @GetMapping("/my")
    public List<Clothing> list(HttpServletRequest request){
        Long userId=(Long)request.getAttribute("userId");
        return repository.findByUserId(userId);
    }

    @GetMapping("")
    public ResponseEntity<?> query(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            logger.info("开始处理衣物查询请求，用户ID: {}, 关键词: {}, 页码: {}, 每页大小: {}", userId, keyword, page, pageSize);
            
            // 获取用户的所有衣物
            List<Clothing> clothingList = repository.findByUserId(userId);
            
            // 应用关键词搜索
            if (keyword != null && !keyword.isEmpty()) {
                String lowerKeyword = keyword.toLowerCase();
                clothingList = clothingList.stream()
                        .filter(item -> {
                            // 检查衣物的所有字符串属性
                            if (item.getName() != null && item.getName().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getType() != null && item.getType().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getSize() != null && item.getSize().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getSeason() != null && item.getSeason().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getStatus() != null && item.getStatus().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getLocation() != null && item.getLocation().toLowerCase().contains(lowerKeyword)) return true;
                            if (item.getColor() != null && item.getColor().toLowerCase().contains(lowerKeyword)) return true;
                            return false;
                        })
                        .collect(java.util.stream.Collectors.toList());
            }
            
            // 应用过滤条件
            if (filters != null && !filters.isEmpty()) {
                try {
                    // 解析filters JSON字符串
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, Object> filterMap = mapper.readValue(filters, java.util.Map.class);
                    
                    for (java.util.Map.Entry<String, Object> entry : filterMap.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value != null && !value.toString().isEmpty()) {
                            clothingList = clothingList.stream()
                                    .filter(item -> {
                                        // 根据属性名获取值并比较
                                        try {
                                            java.lang.reflect.Field field = Clothing.class.getDeclaredField(key);
                                            field.setAccessible(true);
                                            Object itemValue = field.get(item);
                                            return itemValue != null && itemValue.toString().equals(value.toString());
                                        } catch (Exception e) {
                                            return false;
                                        }
                                    })
                                    .collect(java.util.stream.Collectors.toList());
                        }
                    }
                } catch (Exception e) {
                    logger.warn("解析过滤条件失败: {}", e.getMessage());
                }
            }
            
            // 应用排序
            if (sortBy != null && !sortBy.isEmpty()) {
                clothingList.sort((a, b) -> {
                    try {
                        java.lang.reflect.Field field = Clothing.class.getDeclaredField(sortBy);
                        field.setAccessible(true);
                        Object aValue = field.get(a);
                        Object bValue = field.get(b);
                        
                        if (aValue instanceof String && bValue instanceof String) {
                            return ((String) aValue).compareTo((String) bValue);
                        } else if (aValue instanceof Number && bValue instanceof Number) {
                            return ((Number) aValue).doubleValue() - ((Number) bValue).doubleValue() > 0 ? 1 : -1;
                        } else if (aValue instanceof java.util.Date && bValue instanceof java.util.Date) {
                            return ((java.util.Date) aValue).compareTo((java.util.Date) bValue);
                        } else if (aValue != null && bValue != null) {
                            return aValue.toString().compareTo(bValue.toString());
                        } else if (aValue != null) {
                            return -1;
                        } else if (bValue != null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } catch (Exception e) {
                        return 0;
                    }
                });
            }
            
            // 应用分页
            int total = clothingList.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            List<Clothing> paginatedList = new java.util.ArrayList<>();
            if (start < total) {
                paginatedList = clothingList.subList(start, end);
            }
            
            // 构建响应
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("data", paginatedList);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("查询衣物失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"查询失败：\" + e.getMessage()}");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("size") String size,
            @RequestParam("season") String season,
            @RequestParam("status") String status,
            @RequestParam("location") String location,
            @RequestParam("purchaseDate") String purchaseDate,
            @RequestParam(value = "lastWearDate", required = false) String lastWearDate,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request){

        try {
            logger.info("开始处理衣物添加请求");
            Long userId=(Long)request.getAttribute("userId");
            logger.info("用户ID: {}", userId);
            
            if (userId == null) {
                logger.error("用户未登录");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"用户未登录\"}");
            }
            
            Clothing clothing = new Clothing();
            clothing.setUserId(userId);
            clothing.setName(name);
            clothing.setType(type);
            clothing.setSize(size);
            clothing.setSeason(season);
            clothing.setStatus(status);
            clothing.setLocation(location);
            clothing.setPurchaseDate(java.sql.Date.valueOf(purchaseDate));
            if (lastWearDate != null && !lastWearDate.isEmpty()) {
                clothing.setLastWearDate(java.sql.Date.valueOf(lastWearDate));
            }

            // 保存衣物信息，获取ID
            logger.info("保存衣物信息");
            Clothing savedClothing = repository.save(clothing);
            logger.info("衣物保存成功，ID: {}", savedClothing.getId());

            // 处理图片上传
            if (image != null && !image.isEmpty()) {
                logger.info("开始处理图片上传，文件名: {}", image.getOriginalFilename());
                // 验证文件类型
                String contentType = image.getContentType();
                logger.info("图片类型: {}", contentType);
                if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
                    logger.error("文件类型不支持: {}", contentType);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"文件类型不支持，仅允许上传jpg、jpeg、png格式的图片\"}");
                }

                // 验证文件大小（5MB）
                logger.info("图片大小: {} bytes", image.getSize());
                if (image.getSize() > 5 * 1024 * 1024) {
                    logger.error("文件大小超过限制: {} bytes", image.getSize());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"文件大小超过限制，单个图片文件大小不超过5MB\"}");
                }

                // 生成唯一文件名
                String originalFilename = image.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID().toString() + extension;
                logger.info("生成唯一文件名: {}", uniqueFilename);

                // 强制使用当前工作目录作为上传目录，避免权限问题
                uploadDir = System.getProperty("user.dir") + "/uploads";
                File directory = new File(uploadDir);
                boolean dirCreated = directory.mkdirs();
                logger.info("使用当前工作目录创建上传目录: {}", uploadDir);
                logger.info("上传目录创建结果: {}", dirCreated);
                logger.info("上传目录是否存在: {}", directory.exists());

                // 检查目录是否存在
                if (!directory.exists()) {
                    logger.error("无法创建上传目录: {}", uploadDir);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("{\"error\": \"无法创建上传目录\"}");
                }
                logger.info("最终上传目录: {}", directory.getAbsolutePath());

                // 保存文件
                Path filePath = Paths.get(uploadDir, uniqueFilename);
                logger.info("保存文件到: {}", filePath);
                Files.write(filePath, image.getBytes());
                logger.info("文件保存成功");

                // 构建图片URL
                String imageUrl = uploadBaseUrl + "/" + uniqueFilename;
                logger.info("图片URL: {}", imageUrl);

                // 创建图片记录
                Image imageEntity = new Image();
                imageEntity.setFilename(uniqueFilename);
                imageEntity.setPath(filePath.toString());
                imageEntity.setUrl(imageUrl);
                imageEntity.setSize(image.getSize());
                imageEntity.setType(contentType);
                imageEntity.setUploadTime(new Date());
                imageEntity.setEntityId(savedClothing.getId());
                imageEntity.setEntityType("clothing");
                imageEntity.setUserId(userId);

                // 保存图片记录
                logger.info("保存图片记录");
                imageRepository.save(imageEntity);
                logger.info("图片记录保存成功");

                // 更新衣物的图片URL
                savedClothing.setImage(imageUrl);
                repository.save(savedClothing);
                logger.info("衣物图片URL更新成功");
            } else {
                logger.info("没有上传图片");
            }

            logger.info("衣物添加成功，返回结果");
            return ResponseEntity.ok(savedClothing);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"文件上传失败\"}");
        } catch (Exception e) {
            logger.error("参数错误", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"参数错误：\" + e.getMessage()}");
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        repository.deleteById(id);
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateRequest request){
        try {
            logger.info("开始更新衣物信息，ID: {}", id);
            Clothing clothing = repository.findById(id).orElseThrow(() -> new Exception("衣物不存在"));
            
            // 更新位置信息
            if (request.getLocation() != null) {
                clothing.setLocation(request.getLocation());
                logger.info("更新衣物位置为: {}", request.getLocation());
            }
            
            Clothing updatedClothing = repository.save(clothing);
            logger.info("衣物更新成功");
            return ResponseEntity.ok(updatedClothing);
        } catch (Exception e) {
            logger.error("更新衣物失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"更新失败：\" + e.getMessage()}");
        }
    }

    // 提供一个单独的图片上传接口
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        try {
            // 验证文件类型
            String contentType = image.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"文件类型不支持，仅允许上传jpg、jpeg、png格式的图片\"}");
            }

            // 验证文件大小（5MB）
            if (image.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"文件大小超过限制，单个图片文件大小不超过5MB\"}");
            }

            // 生成唯一文件名
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // 确保上传目录存在
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 保存文件
            Path filePath = Paths.get(uploadDir, uniqueFilename);
            Files.write(filePath, image.getBytes());

            // 构建图片URL
            String imageUrl = uploadBaseUrl + "/" + uniqueFilename;

            // 获取用户ID
            Long userId = (Long) request.getAttribute("userId");

            // 创建图片记录
            Image imageEntity = new Image();
            imageEntity.setFilename(uniqueFilename);
            imageEntity.setPath(filePath.toString());
            imageEntity.setUrl(imageUrl);
            imageEntity.setSize(image.getSize());
            imageEntity.setType(contentType);
            imageEntity.setUploadTime(new Date());
            imageEntity.setUserId(userId);

            // 保存图片记录
            imageRepository.save(imageEntity);

            // 构建响应
            return ResponseEntity.ok()
                    .body("{\"url\": \"" + imageUrl + "\", \"filename\": \"" + uniqueFilename + "\", \"size\": " + image.getSize() + "}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"文件上传失败\"}");
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody SaveRequest request, HttpServletRequest servletRequest) {
        try {
            Long userId = (Long) servletRequest.getAttribute("userId");
            Clothing clothing;
            
            if (request.getId() != null) {
                // 更新现有衣物
                clothing = repository.findById(request.getId()).orElseThrow(() -> new Exception("衣物不存在"));
            } else {
                // 创建新衣物
                clothing = new Clothing();
                clothing.setUserId(userId);
            }
            
            // 设置衣物属性
            clothing.setName(request.getName());
            clothing.setType(request.getType());
            clothing.setSize(request.getSize());
            clothing.setSeason(request.getSeason());
            clothing.setStatus(request.getStatus());
            clothing.setLocation(request.getLocation());
            clothing.setPurchaseDate(java.sql.Date.valueOf(request.getPurchaseDate()));
            
            if (request.getLastWearDate() != null && !request.getLastWearDate().isEmpty()) {
                clothing.setLastWearDate(java.sql.Date.valueOf(request.getLastWearDate()));
            }
            
            if (request.getColor() != null) {
                clothing.setColor(request.getColor());
            }
            
            if (request.getWearFrequency() != null) {
                clothing.setWearFrequency(request.getWearFrequency());
            }
            
            if (request.getStorageDate() != null && !request.getStorageDate().isEmpty()) {
                clothing.setStorageDate(java.sql.Date.valueOf(request.getStorageDate()));
            }
            
            // 保存衣物
            Clothing savedClothing = repository.save(clothing);
            
            return ResponseEntity.ok(savedClothing);
        } catch (Exception e) {
            logger.error("保存衣物失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"保存失败：\" + e.getMessage()}");
        }
    }

    // 更新请求的DTO类
    static class UpdateRequest {
        private String location;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    // 保存请求的DTO类
    static class SaveRequest {
        private Long id;
        private String name;
        private String type;
        private String size;
        private String season;
        private String status;
        private String location;
        private String purchaseDate;
        private String lastWearDate;
        private String color;
        private Integer wearFrequency;
        private String storageDate;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getSeason() {
            return season;
        }

        public void setSeason(String season) {
            this.season = season;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPurchaseDate() {
            return purchaseDate;
        }

        public void setPurchaseDate(String purchaseDate) {
            this.purchaseDate = purchaseDate;
        }

        public String getLastWearDate() {
            return lastWearDate;
        }

        public void setLastWearDate(String lastWearDate) {
            this.lastWearDate = lastWearDate;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getWearFrequency() {
            return wearFrequency;
        }

        public void setWearFrequency(Integer wearFrequency) {
            this.wearFrequency = wearFrequency;
        }

        public String getStorageDate() {
            return storageDate;
        }

        public void setStorageDate(String storageDate) {
            this.storageDate = storageDate;
        }
    }

}

