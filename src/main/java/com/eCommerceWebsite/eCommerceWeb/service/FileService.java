package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.payload.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public String uploadImage(String path, MultipartFile image) throws IOException;
}
