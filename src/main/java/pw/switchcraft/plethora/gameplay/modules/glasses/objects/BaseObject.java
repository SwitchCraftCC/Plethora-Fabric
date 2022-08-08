package pw.switchcraft.plethora.gameplay.modules.glasses.objects;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.api.lua.LuaException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import pw.switchcraft.plethora.api.reference.ConstantReference;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasClient;
import pw.switchcraft.plethora.gameplay.modules.glasses.canvas.CanvasServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public abstract class BaseObject {
    public static final Comparator<BaseObject> SORTING_ORDER = Comparator.comparingInt(a -> a.id);

    final int id;
    private final byte type;
    private final int parent;

    private boolean dirty = true;

    public BaseObject(int id, int parent, byte type) {
        this.id = id;
        this.parent = parent;
        this.type = type;
    }

    /**
     * Get the unique ID for this object
     *
     * @return This object's ID
     */
    public final int id() {
        return id;
    }

    /**
     * Get the unique ID of this object's parent
     *
     * @return This object's parent's ID
     */
    public final int parent() {
        return parent;
    }

    /**
     * Get the type of this object
     *
     * @return The object's type
     */
    public final byte type() {
        return type;
    }

    public boolean pollDirty() {
        boolean value = dirty;
        dirty = false;
        return value;
    }

    protected void setDirty() {
        dirty = true;
    }

    /**
     * Read the initial data for this object.
     *
     * @param buf The buffer to read from.
     */
    public abstract void readInitial(@Nonnull PacketByteBuf buf);

    /**
     * Write the initial buffer for this object.
     *
     * @param buf The buffer to write to.
     */
    public abstract void writeInitial(@Nonnull PacketByteBuf buf);

    /**
     * Draw this object
     *
     * @param canvas    The canvas context we are drawing within
     * @param matrices
     * @param consumers
     */
    @Environment(EnvType.CLIENT)
    public abstract void draw(@Nonnull CanvasClient canvas, @Nonnull MatrixStack matrices,
                              @Nullable VertexConsumerProvider consumers);

    /**
     * Prepare to draw a flat object
     */
    @Environment(EnvType.CLIENT)
    protected static void setupFlat() {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
    }

    public static class BaseObjectReference<T extends BaseObject> implements ConstantReference<T> {
        private final CanvasServer canvas;
        private final int id;

        public BaseObjectReference(CanvasServer canvas, BaseObject object) {
            this.canvas = canvas;
            id = object.id;
        }

        @Nonnull
        @Override
        public T get() throws LuaException {
            @SuppressWarnings("unchecked")
            T object = (T) canvas.getObject(id);
            if (object == null) throw new LuaException("This object has been removed");
            return object;
        }

        @Nonnull
        @Override
        public T safeGet() throws LuaException {
            return get();
        }
    }

    @FunctionalInterface
    public interface Factory {
        BaseObject create(int id, int parent);
    }
}
