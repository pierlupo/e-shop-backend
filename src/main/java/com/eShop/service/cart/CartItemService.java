package com.eShop.service.cart;

import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Cart;
import com.eShop.model.CartItem;
import com.eShop.model.Product;
import com.eShop.repository.CartItemRepository;
import com.eShop.repository.CartRepository;
import com.eShop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService{

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final ProductService productService;


    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1.Get the cart
        //2.Get the product
        //3.Check if the product is already in the cart
        //4.If yes, then increase the quantity with the requested quantity
        //5.If no, then initiate a new CartItem entry
    Cart cart = cartService.getCart(cartId);
    Product product = productService.getProductById(productId);
    CartItem cartItem = cart.getItems()
            .stream()
            .filter(item->item.getProduct().getId().equals(productId))
            .findFirst().orElse(new CartItem());
    if (cartItem.getId()== null) {
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(product.getPrice());
    } else {
        cartItem.setQuantity(cartItem.getQuantity()+quantity);
    }
    cartItem.setTotalPrice();
    cart.addItem(cartItem);
    cartItemRepository.save(cartItem);
    cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
    Cart cart = cartService.getCart(cartId);
    CartItem itemToRemove = getCartItem(cartId, productId);
    cart.removeItem(itemToRemove);
    cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
    Cart cart = cartService.getCart(cartId);
    cart.getItems().stream()
            .filter(item->item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item->{
                item.setQuantity(quantity);
                item.setUnitPrice(item.getProduct().getPrice());
                item.setTotalPrice();
            });
        BigDecimal totalAmount = cart.getItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
         cart.setTotalAmount(totalAmount);
         cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
    return  cart.getItems()
                .stream()
                .filter(item->item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }
}