package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.service.PurchasesService;
import net.thumbtack.onlineshop.service.PurchasesService.Target;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class PurchasesController {

    private PurchasesService purchasesService;

    public PurchasesController(PurchasesService purchasesService) {
        this.purchasesService = purchasesService;
    }

    @GetMapping("purchases")
    public String getPurchases(
            @CookieValue("JAVASESSIONID") String session,
            @RequestParam(name = "target") String target,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) throws ServiceException {

        Target requestTarget = Target.CLIENT;

        if (target.equals("product")) {
            requestTarget = Target.PRODUCT;
        }

        return purchasesService.getPurchases(session, requestTarget, offset, limit);
    }
}
