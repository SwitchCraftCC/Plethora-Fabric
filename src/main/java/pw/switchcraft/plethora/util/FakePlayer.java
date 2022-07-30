package pw.switchcraft.plethora.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.message.MessageHeader;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.OptionalInt;

/**
 * A wrapper for {@link ServerPlayerEntity} which denotes a "fake" player.
 *
 * Please note that this does not implement any of the traditional fake player behaviour. It simply exists to prevent
 * me passing in normal players.
 */
public abstract class FakePlayer extends ServerPlayerEntity {
    public FakePlayer(ServerWorld world, GameProfile profile) {
        super(world.getServer(), world, profile, null);
        networkHandler = new FakeNetworkHandler(this);
    }

    @Override
    public void enterCombat() {}
    @Override
    public void endCombat() {}
    @Override
    public void tick() {}
    @Override
    public void playerTick() {}
    @Override
    public void kill() {}
    @Nullable @Override
    public Entity moveToWorld(ServerWorld destination) { return this; }
    @Override
    public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers) {}
    @Override
    public boolean startRiding(Entity entity, boolean force) { return false; }
    @Override
    public void stopRiding() {}
    @Override
    public void openEditSignScreen(SignBlockEntity sign) {}
    @Override
    public OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory) { return OptionalInt.empty(); }
    @Override
    public void sendTradeOffers(int syncId, TradeOfferList offers, int levelProgress, int experience, boolean leveled, boolean refreshable) {}
    @Override
    public void openHorseInventory(AbstractHorseEntity horse, Inventory inventory) {}
    @Override
    public void useBook(ItemStack book, Hand hand) {}
    @Override
    public void openCommandBlockScreen(CommandBlockBlockEntity commandBlock) {}
    @Override
    public void closeHandledScreen() {}
    @Override
    public void closeScreenHandler() {}
    @Override
    public int unlockRecipes(Collection<Recipe<?>> recipes) { return 0;}
    @Override
    public void unlockRecipes(Identifier[] ids) {}
    @Override
    public int lockRecipes(Collection<Recipe<?>> recipes) { return 0; }
    @Override
    public void sendMessage(Text message, boolean actionBar) {}
    @Override
    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {}
    @Override
    public void lookAtEntity(EntityAnchorArgumentType.EntityAnchor anchorPoint, Entity targetEntity, EntityAnchorArgumentType.EntityAnchor targetAnchor) {}
    @Override
    protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {}
    @Override
    protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {}
    @Override
    protected void onStatusEffectRemoved(StatusEffectInstance effect) {}
    @Override
    public void requestTeleport(double destX, double destY, double destZ) {}
    @Override
    public void sendMessageToClient(Text message, boolean overlay) {}
    @Override
    public void sendChatMessage(SentMessage message, boolean overlay, MessageType.Parameters parameters) {}
    @Override
    public void sendMessageHeader(MessageHeader header, MessageSignatureData headerSignature, byte[] bodyDigest) {}

    @Override
    public String getIp() {
        return "[Fake Player]";
    }

    @Override
    public void setCameraEntity(@Nullable Entity entity) {}
    @Override
    public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch) {}
    @Override
    public void sendChunkPacket(ChunkPos chunkPos, Packet<?> chunkDataPacket) {}
    @Override
    public void sendUnloadChunkPacket(ChunkPos chunkPos) {}
    @Override
    public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {}
}
