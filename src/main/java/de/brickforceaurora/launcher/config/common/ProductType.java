package de.brickforceaurora.launcher.config.common;

public enum ProductType {
    
    AURORA("Aurora"),
    SHINING_STAR("ShiningStar");

    private final String label;
    
    private ProductType(String label) {
        this.label = label;
    }
    
    public String label() {
        return label;
    }

}
