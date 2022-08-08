package pw.switchcraft.plethora.gameplay.modules.glasses.canvas;

import it.unimi.dsi.fastutil.ints.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup;

import static pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_2D;
import static pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_3D;

public class CanvasClient {
    public final int id;

    private final Int2ObjectMap<BaseObject> objects = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<IntSortedSet> childrenOf = new Int2ObjectOpenHashMap<>();

    public CanvasClient(int id) {
        this.id = id;
        childrenOf.put(ID_2D, new IntAVLTreeSet());
        childrenOf.put(ID_3D, new IntAVLTreeSet());
    }

    public void updateObject(BaseObject object) {
        IntSet parent = childrenOf.get(object.parent());
        if (parent == null) {
            Plethora.LOG.error("Trying to add " + object.id() + " to group " + object.parent() + " (" + object + ")");
            return; // Should never happen but...
        }

        if (objects.put(object.id(), object) == null) {
            // If this is a new instance then set up the children
            parent.add(object.id());
            if (object instanceof ObjectGroup) childrenOf.put(object.id(), new IntAVLTreeSet());
        }
    }

    public void remove(int id) {
        BaseObject object = objects.remove(id);
        childrenOf.remove(id); // We handle the removing of children in the canvas version

        if (object != null) {
            // Remove from the parent set if needed.
            IntSet parent = childrenOf.get(object.parent());
            if (parent != null) parent.remove(id);
        }
    }

    public BaseObject getObject(int id) {
        return objects.get(id);
    }

    public IntSet getChildren(int id) {
        return childrenOf.get(id);
    }

    @Environment(EnvType.CLIENT)
    public void drawChildren(IntIterator children, MatrixStack matrices) {
        while (children.hasNext()) {
            int id = children.nextInt();
            BaseObject object = getObject(id);
            if (object != null) object.draw(this, matrices);
        }
    }
}
