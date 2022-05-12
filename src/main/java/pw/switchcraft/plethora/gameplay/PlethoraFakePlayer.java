package pw.switchcraft.plethora.gameplay;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import pw.switchcraft.plethora.Plethora;
import pw.switchcraft.plethora.api.Constants;
import pw.switchcraft.plethora.util.FakePlayer;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;

public class PlethoraFakePlayer extends FakePlayer {
    public static final GameProfile PROFILE = new GameProfile(Constants.FAKEPLAYER_UUID, "[" + Plethora.MOD_ID + "]");

    private final WeakReference<Entity> owner;

    private BlockPos digPosition;
    private Block digBlock;

    private int currentDamage = -1;
    private int currentDamageState = -1;

    public PlethoraFakePlayer(ServerWorld world, Entity owner, GameProfile profile) {
        super(world, profile != null && profile.isComplete() ? profile : PROFILE);
        if (owner != null) {
            setCustomName(owner.getName());
            this.owner = new WeakReference<>(owner);
        } else {
            this.owner = null;
        }
    }

    @Nonnull
    @Override
    protected HoverEvent getHoverEvent() {
        Entity owner = getOwner();
        if (owner != null) {
            return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(
                owner.getType(),
                owner.getUuid(),
                owner.getName()
            ));
        } else {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new LiteralText("PlethoraFakePlayer - No owner!"));
        }
    }

    public Entity getOwner() {
        return owner == null ? null : owner.get();
    }
}
