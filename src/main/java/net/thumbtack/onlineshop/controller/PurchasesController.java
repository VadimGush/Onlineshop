package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.PurchasesDto;
import net.thumbtack.onlineshop.service.PurchasesService;
import net.thumbtack.onlineshop.service.PurchasesService.Target;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class PurchasesController {

    private PurchasesService purchasesService;

    public PurchasesController(PurchasesService purchasesService) {
        this.purchasesService = purchasesService;
    }

    @GetMapping("purchases")
    public PurchasesDto getPurchases(
            @CookieValue("JAVASESSIONID") String session,
            @RequestParam(name = "target") String target,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "id", required = false) List<Long> ids,
            @RequestParam(name = "categories", required = false) List<Long> categories
    ) throws ServiceException {

        Target requestTarget = Target.CLIENT;

        if (target.equals("product")) {
            requestTarget = Target.PRODUCT;
        }

        return purchasesService.getPurchases(
                session, requestTarget, offset, limit, ids, categories);
    }
}
