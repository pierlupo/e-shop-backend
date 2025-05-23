package com.eShop.service.cart;

import com.eShop.model.CartItem;

public interface ICartItemService {

    void addItemToCart(Long cartId, Long itemId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItem(Long cartId, Long productId);
}