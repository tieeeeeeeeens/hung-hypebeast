package com.hungshop.hunghypebeast.exception;

public class ReservationExpiredException extends RuntimeException {
    public ReservationExpiredException(String message) {
        super(message);
    }

    public ReservationExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
