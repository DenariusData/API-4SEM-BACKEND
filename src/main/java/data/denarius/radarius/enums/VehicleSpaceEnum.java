package data.denarius.radarius.enums;

import java.math.BigDecimal;

public enum VehicleSpaceEnum {
    CARRO("Carro", new BigDecimal("4.5")),
    MOTO("Moto", new BigDecimal("3.0")),
    CAMINHAO_GRANDE("Caminhão Grande", new BigDecimal("12.0")),
    VAN("Van", new BigDecimal("6.0")),
    CAMIONETE("Camionete", new BigDecimal("6.5")),
    ONIBUS("Ônibus", new BigDecimal("12.0"));

    private final String displayName;
    private final BigDecimal spaceOccupied;

    VehicleSpaceEnum(String displayName, BigDecimal spaceOccupied) {
        this.displayName = displayName;
        this.spaceOccupied = spaceOccupied;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getSpaceOccupied() {
        return spaceOccupied;
    }

    public boolean isLargeVehicle() {
        return this == VAN || this == CAMIONETE || this == CAMINHAO_GRANDE;
    }

    public boolean isExcludedFromDensityCalculation() {
        return this == ONIBUS;
    }

    public static VehicleSpaceEnum fromString(String vehicleType) {
        if (vehicleType == null) return CARRO;
        
        String type = vehicleType.toUpperCase().trim();
        
        if (type.contains("MOTO") || type.contains("MOTORCYCLE")) {
            return MOTO;
        } else if (type.contains("CAMINHÃO") || type.contains("CAMINHAO") || type.contains("TRUCK")) {
            return CAMINHAO_GRANDE;
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
