package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepositDto {

    @NotNull
    @DecimalMin("0")
    private Integer deposit;

    public DepositDto() {

    }

    public DepositDto(Integer deposit) {
        this.deposit = deposit;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }
}
