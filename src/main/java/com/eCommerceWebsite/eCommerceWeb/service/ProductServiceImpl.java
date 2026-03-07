package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.exceptions.APIException;
import com.eCommerceWebsite.eCommerceWeb.exceptions.ResourceNotFoundException;
import com.eCommerceWebsite.eCommerceWeb.model.Cart;
import com.eCommerceWebsite.eCommerceWeb.model.Category;
import com.eCommerceWebsite.eCommerceWeb.model.Product;
import com.eCommerceWebsite.eCommerceWeb.payload.CartDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.ProductDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.ProductResponse;
import com.eCommerceWebsite.eCommerceWeb.repositories.CartRepository;
import com.eCommerceWebsite.eCommerceWeb.repositories.CategoryRepository;
import com.eCommerceWebsite.eCommerceWeb.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CartRepository  cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId,  ProductDTO productDto) {
        Product product = modelMapper.map(productDto,Product.class);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product p : products) {
            if(p.getProductName().equals(productDto.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
        product.setImage("default.png");
        product.setCategory(category);
        product.setSpecialPrice(product.getDiscount()!=null
                ? product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice())
                : product.getPrice());
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }else  {
        throw new APIException("Product already exists");
    }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<Product> products = productPage.getContent();
        List<ProductDTO> productDTOList = products.stream()
                .map(p -> modelMapper.map(p,ProductDTO.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException("No products found");
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> product = productRepository.findByCategoryOrderByPriceAsc(pageable,category);

        List<Product> products = product.getContent();

        List<ProductDTO> productDTOList = products.stream()
                .map(p -> modelMapper.map(p,ProductDTO.class))
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(product.getNumber());
        productResponse.setPageSize(product.getSize());
        productResponse.setTotalElements(product.getTotalElements());
        productResponse.setTotalPages(product.getTotalPages());
        productResponse.setLastPage(product.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                :  Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> product = productRepository.findByProductNameLikeIgnoreCase(pageable,'%'+keyword+'%');

        List<Product> products = product.getContent();

        if(products.isEmpty()){
            throw new APIException("No products found By the search keyword");
        }

        List<ProductDTO> productDTOList = products.stream()
                .map(p -> modelMapper.map(p,ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOList);
        productResponse.setPageNumber(product.getNumber());
        productResponse.setPageSize(product.getSize());
        productResponse.setTotalElements(product.getTotalElements());
        productResponse.setTotalPages(product.getTotalPages());
        productResponse.setLastPage(product.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDto) {

        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));
        Product product = modelMapper.map(productDto,Product.class);
//        product.setSpecialPrice(product.getDiscount()!=null
//                ? product.getPrice() - (product.getDiscount() * 0.01 * product.getPrice())
//                : product.getPrice());
//        product.setProductId(productFromDb.getProductId());

        productFromDb.setProductName(productFromDb.getProductName());
        productFromDb.setPrice(productFromDb.getPrice());
        productFromDb.setDiscount(productFromDb.getDiscount());
        productFromDb.setQuantity(productFromDb.getQuantity());
        productFromDb.setDescription(productFromDb.getDescription());
        productFromDb.setSpecialPrice(productFromDb.getSpecialPrice());

        Product updatedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream().map(cart ->{
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(),ProductDTO.class)).
                    toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();

        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(),productId));


        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),productId));

        productRepository.delete(productFromDb);
        return modelMapper.map(productFromDb, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        String fileName = fileService.uploadImage(path,image);

        product.setImage(fileName);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

}
