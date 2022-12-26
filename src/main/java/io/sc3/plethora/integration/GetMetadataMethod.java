package pw.switchcraft.plethora.integration;

import dan200.computercraft.api.lua.IArguments;
import pw.switchcraft.plethora.api.PlethoraAPI;
import pw.switchcraft.plethora.api.converter.IConverterExcludeMethod;
import pw.switchcraft.plethora.api.meta.IMetaRegistry;
import pw.switchcraft.plethora.api.method.BasicMethod;
import pw.switchcraft.plethora.api.method.FutureMethodResult;
import pw.switchcraft.plethora.api.method.IPartialContext;
import pw.switchcraft.plethora.api.method.IUnbakedContext;

import javax.annotation.Nonnull;

public final class GetMetadataMethod extends BasicMethod<Object> implements IConverterExcludeMethod {
    public GetMetadataMethod() {
        super("getMetadata", Integer.MIN_VALUE, "function():table -- Get metadata about this object");
    }

    @Override
    public boolean canApply(@Nonnull IPartialContext<Object> context) {
        IMetaRegistry registry = PlethoraAPI.instance().metaRegistry();
        Object target = context.getTarget();

        if (!registry.getMetaProviders(target.getClass()).isEmpty()) return true;

        // Convert all and check if any matches
        for (Object converted : PlethoraAPI.instance().converterRegistry().convertAll(target)) {
            if (!registry.getMetaProviders(converted.getClass()).isEmpty()) return true;
        }

        return false;
    }

    @Nonnull
    @Override
    public FutureMethodResult apply(@Nonnull IUnbakedContext<Object> context, @Nonnull IArguments args) {
        return FutureMethodResult.nextTick(() -> FutureMethodResult.result(context.bake().getMeta()));
    }
}
