package net.thumbtack.onlineshop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerConfigurationDto {

    private int maxNameLength;
    private int minPasswordLength;

    public ServerConfigurationDto(int maxNameLength, int minPasswordLength) {
        this.maxNameLength = maxNameLength;
        this.minPasswordLength = minPasswordLength;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }

}
