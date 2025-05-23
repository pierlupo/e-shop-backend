package com.dailycodework.dreamshops.controller;


import com.dailycodework.dreamshops.dto.ImageDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Image;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {

    private final IImageService imageService;

    private static final String API_PREFIX = "/api";
    private static final String IMAGES_PATH = "/images";
    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_ID_PATH = "/{imageId}";
    private static final String IMAGE_DOWNLOAD_PATH = "/download";
    private static final String IMAGE_UPLOAD_PATH = "/upload";
    private static final String IMAGE_DELETE_PATH = "/delete";
    private static final String IMAGE_UPDATE_PATH = "/update";
    private static final String IMAGE_ADD_PATH = "/add";
    private static final String IMAGE_LIST_PATH = "/list";
    private static final String IMAGE_COUNT_PATH = "/count";
    private static final String IMAGE_BY_NAME_PATH = "/byName";
    private static final String IMAGE_BY_BRAND_AND_NAME_PATH = "/byBrandAndName";
    private static final String IMAGE_BY_CATEGORY_AND_BRAND_AND_NAME_PATH = "/byCategoryAndBrandAndName";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_PATH = "/byCategoryAndName";
    private static final String IMAGE_BY_ID_PATH = "/byId";
    private static final String IMAGE_BY_CATEGORY_PATH = "/byCategory";
    private static final String IMAGE_BY_BRAND_PATH = "/byBrand";
    private static final String IMAGE_BY_NAME_AND_CATEGORY_PATH = "/byNameAndCategory";
    private static final String IMAGE_BY_NAME_AND_BRAND_PATH = "/byNameAndBrand";
    private static final String IMAGE_BY_NAME_AND_CATEGORY_AND_BRAND_PATH = "/byNameAndCategoryAndBrand";
    private static final String IMAGE_BY_CATEGORY_AND_BRAND_PATH = "/byCategoryAndBrand";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_AND_BRAND_AND_PRICE_PATH = "/byCategoryAndNameAndBrandAndPrice";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_AND_BRAND_AND_PRICE_GREATER_THAN_PATH = "/byCategoryAndNameAndBrandAndPriceGreaterThan";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_AND_BRAND_AND_PRICE_LESS_THAN_PATH = "/byCategoryAndNameAndBrandAndPriceLessThan";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_AND_BRAND_AND_PRICE_BETWEEN_PATH = "/byCategoryAndNameAndBrandAndPriceBetween";
    private static final String IMAGE_BY_CATEGORY_AND_NAME_AND_BRAND_AND_PRICE_NOT_BETWEEN_PATH = "/byCategoryAndNameAndBrandAndPriceNotBetween";


    @PostMapping(IMAGE_UPLOAD_PATH)
    public ResponseEntity<ApiResponse> saveImages(List<MultipartFile> files, @RequestParam Long productId) {
        try {
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ResponseEntity.ok(new ApiResponse("Uploaded image successfully!", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Upload of image failed", e.getMessage()));
        }
    }

    @GetMapping(IMAGE_PATH + IMAGE_DOWNLOAD_PATH + IMAGE_ID_PATH)
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping(IMAGE_PATH + IMAGE_ID_PATH + IMAGE_UPDATE_PATH)
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file) {
        try {
            Image image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiResponse("Updated image successfully!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Image upload failed", INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping(IMAGE_PATH + IMAGE_ID_PATH + IMAGE_DELETE_PATH)
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId, @RequestBody MultipartFile file) {
        try {
            Image image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse("Deleted image successfully!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse( e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Deletion of image failed", INTERNAL_SERVER_ERROR));
    }

}