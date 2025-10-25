package data.denarius.radarius.enums;

public enum VehicleTypeEnum {
    CARRO("Carro"),
    MOTOCICLETA("Moto"),
    CAMINHAO("Caminhão Grande"),
    VAN("Van"),
    CAMIONETE("Camionete"),
    ONIBUS("Ônibus"),
    OUTRO("Outro");

    private final String displayName;

    VehicleTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static VehicleTypeEnum fromString(String vehicleType) {
        if (vehicleType == null) return CARRO;
        
        String type = vehicleType.toUpperCase().trim();
        
        if (type.contains("MOTO") || type.contains("MOTORCYCLE")) {
            return MOTOCICLETA;
        } else if (type.contains("CAMINHÃO") || type.contains("CAMINHAO") || type.contains("TRUCK")) {
            return CAMINHAO;
        } else if (type.contains("VAN")) {
            return VAN;
        } else if (type.contains("CAMIONETE") || type.contains("PICKUP")) {
            return CAMIONETE;
        } else if (type.contains("ÔNIBUS") || type.contains("ONIBUS") || type.contains("BUS")) {
            return ONIBUS;
        } else {
            return CARRO;
        }
    }
}
