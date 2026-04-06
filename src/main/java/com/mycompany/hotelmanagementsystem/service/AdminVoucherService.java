package com.mycompany.hotelmanagementsystem.service;

<<<<<<< HEAD
import com.mycompany.hotelmanagementsystem.entity.Voucher;
import com.mycompany.hotelmanagementsystem.dal.VoucherRepository;
=======
import com.mycompany.hotelmanagementsystem.model.Voucher;
import com.mycompany.hotelmanagementsystem.dao.VoucherRepository;
>>>>>>> e968fe16406324ee01e4584da7e6dbe2840dfe5b
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

    public boolean updateVoucher(int voucherId, String code, BigDecimal discountAmount,
                                  BigDecimal minOrderValue, boolean isActive) {
        Voucher voucher = voucherRepository.findById(voucherId);
        if (voucher == null) return false;

        voucher.setCode(code.toUpperCase());
        voucher.setDiscountAmount(discountAmount);
        voucher.setMinOrderValue(minOrderValue);
        voucher.setActive(isActive);
        return voucherRepository.update(voucher) > 0;
    }

    public boolean deleteVoucher(int voucherId) {
        return voucherRepository.delete(voucherId) > 0;
    }
}
