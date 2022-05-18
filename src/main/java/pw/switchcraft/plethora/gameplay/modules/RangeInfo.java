package pw.switchcraft.plethora.gameplay.modules;

import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.gameplay.modules.scanner.ScannerModuleItem;
import pw.switchcraft.plethora.gameplay.modules.sensor.SensorModuleItem;

import javax.annotation.Nonnull;
import java.util.function.IntUnaryOperator;

/**
 * Provides the range for a {@link SensorModuleItem} or {@link ScannerModuleItem}
 */
public interface RangeInfo extends ConstantReference<RangeInfo> {
    /**
     * The maximum range this module operates at.
     *
     * @return This module's range.
     */
    int getRange();

    /**
     * The cost for some bulk operation (sense/scan).
     *
     * @return The cost of a bulk operation.
     * @see pw.switchcraft.plethora.api.method.ICostHandler
     */
    int getBulkCost();

    @Nonnull
    @Override
    default RangeInfo get() {
        return this;
    }

    @Nonnull
    @Override
    default RangeInfo safeGet() {
        return this;
    }

    static RangeInfo of(int level, IntUnaryOperator cost, IntUnaryOperator range) {
        return new RangeInfo() {
            @Override
            public int getRange() {
                return range.applyAsInt(level);
            }

            @Override
            public int getBulkCost() {
                return cost.applyAsInt(level);
            }
        };
    }
}
