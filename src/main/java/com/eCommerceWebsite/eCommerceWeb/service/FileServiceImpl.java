package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    ProductRepository productRepository;

    ModelMapper modelMapper = new ModelMapper();

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        String file = image.getOriginalFilename();

        String fileName = UUID.randomUUID().toString().concat(file.substring(file.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));

        return fileName;
    }
}
