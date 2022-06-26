package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler;

/**
 * Represents a holder for {@link BaseObject}s.
 */
public interface ObjectGroup {
    /**
     * @return The ID for this group.
     */
    int id();

    /**
     * A group for 2D objects
     */
    interface Group2d extends ObjectGroup {
    }

    /**
     * A group for 2D objects with a fixed side
     */
    interface Frame2d extends Group2d {
        default int getWidth() {
            return CanvasHandler.WIDTH;
        }

        default int getHeight() {
            return CanvasHandler.HEIGHT;
        }
    }

    /**
     * A group for 3D objects
     */
    interface Group3d extends ObjectGroup {
    }

    /**
     * The "origin" for all 3D objects
     */
    interface Origin3d extends ObjectGroup {
    }
}
