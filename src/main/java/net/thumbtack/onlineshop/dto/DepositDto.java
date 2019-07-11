package net.thumbtack.onlineshop.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class DepositDto {

    @NotNull
    @DecimalMin("0")
    private Integer deposit;

    public DepositDto() {

    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }
}
