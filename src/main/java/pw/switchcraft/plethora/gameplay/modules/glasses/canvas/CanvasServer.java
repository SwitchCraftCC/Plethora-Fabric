package pw.switchcraft.plethora.gameplay.modules.glasses.canvas;

import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.server.network.ServerPlayerEntity;
import pw.switchcraft.plethora.api.method.IAttachable;
import pw.switchcraft.plethora.api.module.IModuleAccess;
import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.api.reference.IReference;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.BaseObject.BaseObjectReference;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasAddPacket;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasRemovePacket;
import pw.switchcraft.plethora.gameplay.modules.glasses.networking.CanvasUpdatePacket;
import pw.switchcraft.plethora.gameplay.modules.glasses.objects.ObjectGroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_2D;
import static pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasHandler.ID_3D;

public class CanvasServer implements ConstantReference<CanvasServer>, IAttachable {
    private final int canvasId;
    private final IModuleAccess access;
    private final ServerPlayerEntity player;

    private final Int2ObjectMap<BaseObject> objects = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<IntSet> childrenOf = new Int2ObjectOpenHashMap<>();

    private final IntSet removed = new IntOpenHashSet();

    private final AtomicInteger lastId = new AtomicInteger(ID_3D);

    private final ObjectGroup.Frame2d group2D = () -> ID_2D;
    private final ObjectGroup.Origin3d origin3D = () -> ID_3D;

    public CanvasServer(@Nonnull IModuleAccess access, @Nonnull ServerPlayerEntity player) {
        canvasId = CanvasHandler.nextId();
        this.access = access;
        this.player = player;

        childrenOf.put(ID_2D, new IntOpenHashSet());
        childrenOf.put(ID_3D, new IntOpenHashSet());
    }

    @Override
    public void attach() {
        access.getData().putInt("id", canvasId);
        access.markDataDirty();
        CanvasHandler.addServer(this);
    }

    @Override
    public void detach() {
        CanvasHandler.removeServer(this);
        access.getData().remove("id");
        access.markDataDirty();
    }

    public int newObjectId() {
        return lastId.incrementAndGet();
    }

    public ObjectGroup.Frame2d canvas2d() {
        return group2D;
    }

    public ObjectGroup.Origin3d canvas3d() {
        return origin3D;
    }

    @Nonnull
    synchronized CanvasAddPacket getAddPacket() {
        return new CanvasAddPacket(canvasId, objects.values().toArray(new BaseObject[0]));
    }

    @Nonnull
    CanvasRemovePacket getRemovePacket() {
        return new CanvasRemovePacket(canvasId);
    }

    @Nullable
    synchronized CanvasUpdatePacket getUpdatePacket() {
        List<BaseObject> changed = null;
        for (BaseObject object : objects.values()) {
            if (object.pollDirty()) {
                if (changed == null) changed = new ArrayList<>();
                changed.add(object);
            }
        }

        if (changed == null && removed.isEmpty()) return null;

        if (changed == null) changed = Collections.emptyList();
        CanvasUpdatePacket packet = new CanvasUpdatePacket(
            canvasId, changed, removed.toIntArray()
        );

        removed.clear();

        return packet;
    }

    public synchronized void add(@Nonnull BaseObject object) {
        IntSet parent = childrenOf.get(object.parent());
        if (parent == null) throw new IllegalArgumentException("No such parent");

        if (objects.put(object.id(), object) != null) {
            throw new IllegalStateException("An object already exists with that key");
        }

        parent.add(object.id());
        if (object instanceof ObjectGroup) childrenOf.put(object.id(), new IntOpenHashSet());
    }

    public synchronized void remove(BaseObject object) {
        if (!removeImpl(object.id())) {
            throw new IllegalStateException("No such object with this key");
        }
    }

    public synchronized BaseObject getObject(int id) {
        return objects.get(id);
    }

    public synchronized void clear(ObjectGroup object) {
        IntSet children = childrenOf.get(object.id());
        if (children == null) throw new IllegalStateException("Object has no children");

        clearImpl(children);
    }

    private boolean removeImpl(int id) {
        if (objects.remove(id) == null) return false;

        IntSet children = childrenOf.remove(id);
        if (children != null) clearImpl(children);

        removed.add(id);
        return true;
    }

    private void clearImpl(IntSet objects) {
        for (IntIterator iterator = objects.iterator(); iterator.hasNext(); ) {
            int childId = iterator.nextInt();
            removeImpl(childId);
            iterator.remove();
        }
    }

    @Nonnull
    public ServerPlayerEntity getPlayer() {
        return player;
    }

    @Nonnull
    @Override
    public CanvasServer get() {
        return this;
    }

    @Nonnull
    @Override
    public CanvasServer safeGet() {
        return this;
    }

    /**
     * Get a reference to this object
     *
     * @param baseObject@return The resulting reference.
     */
    public <T extends BaseObject> IReference<T> reference(T baseObject) {
        return new BaseObjectReference<>(this, baseObject);
    }
}
