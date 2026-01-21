package com.hungshop.hunghypebeast.config;

import com.hungshop.hunghypebeast.dto.CartContext;

public final class CartContextHolder {

    private static final ThreadLocal<CartContext> CONTEXT = new ThreadLocal<>();

    private CartContextHolder() {
    }

    public static void setContext(CartContext context) {
        CONTEXT.set(context);
    }

    public static CartContext getContext() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
