package data.denarius.radarius.enums;

public enum AlertSourceEnum {
    SYSTEM("System Generated"),
    MANUAL("Manual Entry"),
    EXTERNAL("External Source");
    
    private final String description;
    
    AlertSourceEnum(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
