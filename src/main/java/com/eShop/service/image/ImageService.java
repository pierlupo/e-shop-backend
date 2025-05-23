package com.eShop.service.image;

import com.eShop.dto.ImageDto;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Image;
import com.eShop.model.Product;
import com.eShop.repository.ImageRepository;
import com.eShop.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final ImageRepository imageRepository;
    private final IProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No Image found with id : " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, ()-> {
            throw new ResourceNotFoundException("No Image found with id : " + id);
        });
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> Files, long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : Files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                Image savedImage = imageRepository.save(image);

                String buildDownloadUrl = "/api/v1/images/image/download/" + savedImage.getId();
                savedImage.setDownloadUrl(buildDownloadUrl);
                imageRepository.save(savedImage);

                ImageDto imageDTO = new ImageDto();
                imageDTO.setImageId(savedImage.getId());
                imageDTO.setDownloadUrl(savedImage.getDownloadUrl());
                imageDTO.setImageName(savedImage.getFileName());

                savedImageDto.add(imageDTO);

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage (MultipartFile file, long ImageId){
        Image image = getImageById(ImageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Image getImageByUrl(String url) {
        return null;
    }
}