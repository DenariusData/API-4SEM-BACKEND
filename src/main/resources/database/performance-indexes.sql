-- Performance optimization indexes for dados_base_radares table
-- These indexes will significantly improve query performance for radar data processing

ALTER SESSION SET CURRENT_SCHEMA = DEN_AUGUSTO;

-- Composite index for camera coordinates and datetime - most important for performance
CREATE INDEX idx_radar_coords_datetime ON dados_base_radares(CAMERA_LAT, CAMERA_LONG, DATA_HORA);

-- Index for datetime range queries
CREATE INDEX idx_radar_datetime ON dados_base_radares(DATA_HORA);

-- Index for camera coordinates
CREATE INDEX idx_radar_coordinates ON dados_base_radares(CAMERA_LAT, CAMERA_LONG);

-- Index for processed flag to optimize unprocessed data queries
CREATE INDEX idx_radar_processed ON dados_base_radares(PROCESSADO);

-- Index for speed limit and vehicle speed (for speed violation queries)
CREATE INDEX idx_radar_speed ON dados_base_radares(VELOCIDADE_VEICULO, VELOCIDADE_REGULAMENTADA);

-- Index for vehicle type (for large vehicle queries)
CREATE INDEX idx_radar_vehicle_type ON dados_base_radares(TIPO_VEICULO);

-- Composite index for camera ID and datetime
CREATE INDEX idx_radar_camera_datetime ON dados_base_radares(CAMERA_ID, DATA_HORA);

-- Index for total lanes
CREATE INDEX idx_radar_total_lanes ON dados_base_radares(QUANTIDADE_DE_FAIXAS);

-- Composite index for speed violations (coordinates + datetime + speed comparison)
CREATE INDEX idx_radar_speed_violations ON dados_base_radares(CAMERA_LAT, CAMERA_LONG, DATA_HORA, VELOCIDADE_VEICULO, VELOCIDADE_REGULAMENTADA);

-- Statistics update to help Oracle optimizer choose the best execution plans
EXEC DBMS_STATS.GATHER_TABLE_STATS('DEN_AUGUSTO', 'DADOS_BASE_RADARES');

-- Add comments for documentation
COMMENT ON INDEX idx_radar_coords_datetime IS 'Performance index for camera coordinate and datetime queries';
COMMENT ON INDEX idx_radar_datetime IS 'Performance index for datetime range queries';
COMMENT ON INDEX idx_radar_coordinates IS 'Performance index for camera coordinate queries';
COMMENT ON INDEX idx_radar_processed IS 'Performance index for processed flag queries';
COMMENT ON INDEX idx_radar_speed IS 'Performance index for speed-related queries';
COMMENT ON INDEX idx_radar_vehicle_type IS 'Performance index for vehicle type queries';
COMMENT ON INDEX idx_radar_camera_datetime IS 'Performance index for camera ID and datetime queries';
COMMENT ON INDEX idx_radar_total_lanes IS 'Performance index for total lanes queries';
COMMENT ON INDEX idx_radar_speed_violations IS 'Performance index for speed violation queries';