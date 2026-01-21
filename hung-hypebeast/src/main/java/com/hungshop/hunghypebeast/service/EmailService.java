package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.entity.EmailLog;
import com.hungshop.hunghypebeast.entity.Order;

public interface EmailService {

    EmailLog sendOrderConfirmation(Order order);

    EmailLog sendOrderTracking(Order order, String trackingUrl);
}
