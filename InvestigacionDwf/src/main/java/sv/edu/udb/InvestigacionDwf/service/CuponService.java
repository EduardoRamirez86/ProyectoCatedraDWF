package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

public interface CuponService {
    Cupon generateCouponForUser(User user);
    boolean validateCoupon(String codigo, User user);
    void redeemCoupon(String codigo, User user);
}
