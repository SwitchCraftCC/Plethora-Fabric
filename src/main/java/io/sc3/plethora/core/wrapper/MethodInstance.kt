package pw.switchcraft.plethora.core.wrapper

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import net.minecraft.util.Identifier
import pw.switchcraft.plethora.api.method.FutureMethodResult
import pw.switchcraft.plethora.api.method.IMethod
import pw.switchcraft.plethora.api.method.IPartialContext
import pw.switchcraft.plethora.api.method.IUnbakedContext
import pw.switchcraft.plethora.api.module.IModuleContainer
import pw.switchcraft.plethora.core.RegisteredMethod
import java.lang.reflect.Method
import javax.annotation.Nonnull

internal class MethodInstance<T>(
  refMethod: Method,
  target: Class<T>,
  modId: String,
  override val regName: String,
  private val documentation: String,
  val worldThread: Boolean,
  private val requiredContext: Array<ContextInfo>,
  val totalContext: Int,
  val modules: Array<Identifier>?,
  private val markerIfaces: Array<Class<*>>?,
  private val subtarget: Class<*>
) : RegisteredMethod<T>(
  "${refMethod.declaringClass.name}#${refMethod.name}(${target.simpleName})",
  modId, target
), IMethod<T> {
  override val method: IMethod<T>
    get() = this

  override fun canApply(@Nonnull context: IPartialContext<T>): Boolean {
    // Ensure we have all required modules.
    if (modules != null) {
      val moduleContainer = if (IModuleContainer::class.java.isAssignableFrom(target)) {
        context.target as IModuleContainer
      } else {
        context.modules
      }

      for (module in modules) {
        if (!moduleContainer.hasModule(module)) return false
      }
    }

    // Ensure we have all required context info
    for (info in requiredContext) {
      if (info.key == null) {
        if (!context.hasContext(info.klass)) return false
      } else {
        var any = false
        for (key in info.key) {
          if (context.hasContext(key, info.klass)) {
            any = true
            break
          }
        }

        if (!any) return false
      }
    }

    return true
  }

  @Nonnull
  @Throws(LuaException::class)
  override fun apply(@Nonnull context: IUnbakedContext<T>, @Nonnull args: IArguments): FutureMethodResult {
    // TODO
    return FutureMethodResult.empty()
  }

  override fun getName() = regName
  override fun getDocString() = documentation

  override fun has(@Nonnull iface: Class<*>): Boolean {
    if (markerIfaces == null) return false
    return markerIfaces.any { iface.isAssignableFrom(it) }
  }

  @Nonnull
  override fun getModules(): Collection<Identifier> =
    if (modules == null) emptyList() else listOf(*modules)

  internal class ContextInfo(val key: Array<String>?, @param:Nonnull val klass: Class<*>)
}
