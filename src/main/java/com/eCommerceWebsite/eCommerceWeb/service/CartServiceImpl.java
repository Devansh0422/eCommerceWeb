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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if(userCart != null){
            return  userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInEmail());
        return  cartRepository.save(cart);
    }
}
