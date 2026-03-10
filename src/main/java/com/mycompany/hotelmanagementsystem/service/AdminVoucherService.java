package com.mycompany.hotelmanagementsystem.service;

import com.mycompany.hotelmanagementsystem.model.Voucher;
import com.mycompany.hotelmanagementsystem.dao.VoucherRepository;
import java.math.BigDecimal;
import java.util.List;

public class AdminVoucherService {
    private final VoucherRepository voucherRepository;

    public AdminVoucherService() {
        this.voucherRepository = new VoucherRepository();
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Voucher getVoucherById(int voucherId) {
        return voucherRepository.findById(voucherId);
    }

    public int createVoucher(String code, BigDecimal discountAmount, BigDecimal minOrderValue, boolean isActive) {
        Voucher voucher = new Voucher();
        voucher.setCode(code.toUpperCase());
        voucher.setDiscountAmount(discountAmount);
        voucher.setMinOrderValue(minOrderValue);
        voucher.setActive(isActive);
        return voucherRepository.insert(voucher);
    }

    
}
