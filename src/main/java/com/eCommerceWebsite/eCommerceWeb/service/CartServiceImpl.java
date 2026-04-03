package com.eCommerceWebsite.eCommerceWeb.service;

import com.eCommerceWebsite.eCommerceWeb.exceptions.APIException;
import com.eCommerceWebsite.eCommerceWeb.exceptions.ResourceNotFoundException;
import com.eCommerceWebsite.eCommerceWeb.model.Cart;
import com.eCommerceWebsite.eCommerceWeb.model.CartItem;
import com.eCommerceWebsite.eCommerceWeb.model.Product;
import com.eCommerceWebsite.eCommerceWeb.payload.CartDTO;
import com.eCommerceWebsite.eCommerceWeb.payload.ProductDTO;
import com.eCommerceWebsite.eCommerceWeb.repositories.CartItemRepository;
import com.eCommerceWebsite.eCommerceWeb.repositories.CartRepository;
import com.eCommerceWebsite.eCommerceWeb.repositories.ProductRepository;
import com.eCommerceWebsite.eCommerceWeb.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = createCart();
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId",productId));

       CartItem cartItem = cartItemRepository.findCartItemBYProductIdAndCartId(cart.getCartId(),productId);

       if(cartItem != null){
           throw  new APIException("Product "+ product.getProductName()+"already exists in cart !!!");
       }

       if(product.getQuantity()==0){
           throw new APIException( product.getProductName()+" is out of stock ");
       }

       if(product.getQuantity()<quantity){
           throw   new APIException("Please, Place the order of "+ product.getProductName()+" is less than or equal to quantity "+product.getQuantity());
       }

       CartItem newCartItem = new CartItem();
       newCartItem.setProduct(product);
       newCartItem.setQuantity(quantity);
       newCartItem.setCart(cart);
       newCartItem.setDiscount(product.getDiscount());
       newCartItem.setProductPrice(product.getSpecialPrice());

       cartItemRepository.save(newCartItem);

       product.setQuantity(product.getQuantity());

       cart.setTotalPrice(cart.getTotalPrice()+ (product.getSpecialPrice()*quantity));
       cartRepository.save(cart);
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream =cartItems.stream().map(item -> {ProductDTO map = modelMapper.map(item,ProductDTO.class);
        map.setQuantity(item.getQuantity());
        return map; });
        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()){
            throw new APIException("No carts exist");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart ->  {
            CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map( cartItem -> {
                        ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                        productDTO.setQuantity(cartItem.getQuantity());
                        return productDTO;
                    }).collect(Collectors.toList());

            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());

        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null){
            throw new ResourceNotFoundException("Cart", "cartId",cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream().map(p -> modelMapper.map(p,ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }


    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId",cartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId",productId));

        if(product.getQuantity()==0){
            throw new APIException( product.getProductName()+" is out of stock ");
        }

        if(product.getQuantity()<quantity){
            throw   new APIException("Please, Place the order of "+ product.getProductName()+" is less than or equal to quantity "+product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemBYProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product"+product.getProductName()+" is not in Cart");
        }

        int newQuantity = cartItem.getQuantity()+quantity;

        if(newQuantity <0){
            throw new APIException("Quantity can not be negative");
        }

        if(newQuantity == 0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {ProductDTO prd = modelMapper.map(item.getProduct(),ProductDTO.class);
        prd.setQuantity(item.getQuantity());
        return prd;
        });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId",cartId));

        CartItem cartItem = cartItemRepository.findCartItemBYProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product", "productId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product "+cartItem.getProduct().getProductName()+" has been removed from the cart!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId",cartId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId",productId));
        CartItem cartItem = cartItemRepository.findCartItemBYProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw new APIException("Product "+product.getProductName()+" inot available in the cart!!");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice()*cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItem = cartItemRepository.save(cartItem);
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if(userCart != null){
            return  userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());
        return  cartRepository.save(cart);
    }
}