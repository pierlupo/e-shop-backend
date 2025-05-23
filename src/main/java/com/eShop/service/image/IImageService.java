package com.eShop.service.image;

import com.eShop.dto.ImageDto;
import com.eShop.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {

    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> Files, long productId);
    void updateImage (MultipartFile file, long ImageId);
    Image getImageByUrl(String url);

}