package net.thumbtack.onlineshop.response;

import net.thumbtack.onlineshop.AppConfig;

public class ServerConfigurationResponse {

    private int maxNameLength;
    private int minPasswordLength;

    public ServerConfigurationResponse(AppConfig config) {
        maxNameLength = config.getMaxNameLength();
        minPasswordLength = config.getMinPasswordLength();
    }

    public ServerConfigurationResponse(int maxNameLength, int minPasswordLength) {
        this.maxNameLength = maxNameLength;
        this.minPasswordLength = minPasswordLength;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public void setMaxNameLength(int maxNameLength) {
        this.maxNameLength = maxNameLength;
    }

    public int getMinPasswordLength() {
        return minPasswordLength;
    }

    public void setMinPasswordLength(int minPasswordLength) {
        this.minPasswordLength = minPasswordLength;
    }
}
