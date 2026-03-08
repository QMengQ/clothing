package com.example.clothing.controller;

import com.example.clothing.entity.RecycleItem;
import com.example.clothing.repository.RecycleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recycle")
@CrossOrigin
public class RecycleController {

    @Autowired
    private RecycleRepository repository;

    private static final String UPLOAD_DIR = "C:/Users/ASUS/Desktop/clothing-frontend/clothing/uploads/";

    @PostMapping("/publish")
    public RecycleItem publish(@RequestParam("item") String itemJson,
                               @RequestParam(value = "images", required = false) MultipartFile[] images,
                               HttpServletRequest request){
        // 解析JSON字符串为RecycleItem对象
        ObjectMapper mapper = new ObjectMapper();
        RecycleItem item = null;
        try {
            item = mapper.readValue(itemJson, RecycleItem.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid item data");
        }

        Long userId=(Long)request.getAttribute("userId");
        item.setUserId(userId);
        item.setStatus("待审核");
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());

        // 处理图片上传
        if (images != null && images.length > 0) {
            StringBuilder imageUrls = new StringBuilder();
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String filename = UUID.randomUUID().toString() + "." + getFileExtension(image.getOriginalFilename());
                        File dest = new File(UPLOAD_DIR + filename);
                        image.transferTo(dest);
                        imageUrls.append(filename).append(",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (imageUrls.length() > 0) {
                imageUrls.setLength(imageUrls.length() - 1);
                item.setImageUrls(imageUrls.toString());
            }
        }

        return repository.save(item);
    }

    @GetMapping("/my")
    public List<RecycleItem> my(HttpServletRequest request){
        Long userId=(Long)request.getAttribute("userId");
        return repository.findByUserId(userId);
    }

    @PutMapping("/update/{id}")
    public RecycleItem update(@PathVariable Long id, @RequestParam("item") String itemJson,
                              @RequestParam(value = "images", required = false) MultipartFile[] images,
                              HttpServletRequest request){
        // 解析JSON字符串为RecycleItem对象
        ObjectMapper mapper = new ObjectMapper();
        RecycleItem item = null;
        try {
            item = mapper.readValue(itemJson, RecycleItem.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid item data");
        }
        RecycleItem existingItem = repository.findById(id).orElseThrow();
        Long userId=(Long)request.getAttribute("userId");
        if (!existingItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此回收项");
        }

        // 更新字段
        existingItem.setClothingType(item.getClothingType());
        existingItem.setClothingCondition(item.getClothingCondition());
        existingItem.setSize(item.getSize());
        existingItem.setBrand(item.getBrand());
        existingItem.setMaterial(item.getMaterial());
        existingItem.setNotes(item.getNotes());
        existingItem.setQualityRating(item.getQualityRating());
        existingItem.setLatitude(item.getLatitude());
        existingItem.setLongitude(item.getLongitude());
        existingItem.setAddress(item.getAddress());
        existingItem.setUpdatedAt(LocalDateTime.now());

        // 处理图片上传
        if (images != null && images.length > 0) {
            StringBuilder imageUrls = new StringBuilder();
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String filename = UUID.randomUUID().toString() + "." + getFileExtension(image.getOriginalFilename());
                        File dest = new File(UPLOAD_DIR + filename);
                        image.transferTo(dest);
                        imageUrls.append(filename).append(",");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (imageUrls.length() > 0) {
                imageUrls.setLength(imageUrls.length() - 1);
                existingItem.setImageUrls(imageUrls.toString());
            }
        }

        return repository.save(existingItem);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id, HttpServletRequest request){
        RecycleItem existingItem = repository.findById(id).orElseThrow();
        Long userId=(Long)request.getAttribute("userId");
        if (!existingItem.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此回收项");
        }
        repository.delete(existingItem);
    }

    @GetMapping("/search")
    public List<RecycleItem> search(@RequestParam(required = false) String clothingType,
                                   @RequestParam(required = false) String recycleType,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(required = false) String keyword,
                                   HttpServletRequest request) {
        Long userId=(Long)request.getAttribute("userId");
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        try {
            if (startDate != null) {
                start = LocalDateTime.parse(startDate + "T00:00:00");
            }
            if (endDate != null) {
                end = LocalDateTime.parse(endDate + "T23:59:59");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return repository.searchByConditions(userId, clothingType, recycleType, status, start, end, keyword);
    }

    @GetMapping("/export")
    public void exportExcel(@RequestParam(required = false) String clothingType,
                           @RequestParam(required = false) String recycleType,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(required = false) String keyword,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long userId=(Long)request.getAttribute("userId");
        LocalDateTime start = null;
        LocalDateTime end = null;
        
        try {
            if (startDate != null) {
                start = LocalDateTime.parse(startDate + "T00:00:00");
            }
            if (endDate != null) {
                end = LocalDateTime.parse(endDate + "T23:59:59");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<RecycleItem> items = repository.searchByConditions(userId, clothingType, recycleType, status, start, end, keyword);
        
        // 生成Excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("回收记录");
        
        // 创建表头
        HSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("衣物类型");
        headerRow.createCell(1).setCellValue("状况");
        headerRow.createCell(2).setCellValue("回收类型");
        headerRow.createCell(3).setCellValue("状态");
        headerRow.createCell(4).setCellValue("创建时间");
        headerRow.createCell(5).setCellValue("尺码");
        headerRow.createCell(6).setCellValue("品牌");
        headerRow.createCell(7).setCellValue("材质");
        headerRow.createCell(8).setCellValue("质量评级");
        headerRow.createCell(9).setCellValue("地址");
        
        // 填充数据
        for (int i = 0; i < items.size(); i++) {
            RecycleItem item = items.get(i);
            HSSFRow dataRow = sheet.createRow(i + 1);
            dataRow.createCell(0).setCellValue(item.getClothingType());
            dataRow.createCell(1).setCellValue(item.getClothingCondition());
            dataRow.createCell(2).setCellValue(item.getRecycleType());
            dataRow.createCell(3).setCellValue(item.getStatus());
            dataRow.createCell(4).setCellValue(item.getCreatedAt().toString());
            dataRow.createCell(5).setCellValue(item.getSize());
            dataRow.createCell(6).setCellValue(item.getBrand() != null ? item.getBrand() : "");
            dataRow.createCell(7).setCellValue(item.getMaterial() != null ? item.getMaterial() : "");
            dataRow.createCell(8).setCellValue(item.getQualityRating());
            dataRow.createCell(9).setCellValue(item.getAddress() != null ? item.getAddress() : "");
        }
        
        // 设置响应头
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=recycle_records.xls");
        
        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "jpg";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "jpg";
    }
}