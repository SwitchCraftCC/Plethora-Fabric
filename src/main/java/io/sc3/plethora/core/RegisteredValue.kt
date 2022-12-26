package io.sc3.plethora.core

/**
 * A node which can be registered and enabled and disabled at runtime.
 */
open class RegisteredValue(open val regName: String, val mod: String) {
  fun enabled(): Boolean {
    // TODO: Module blacklist
    return true
    //		return !Helpers.blacklisted(ConfigCore.Blacklist.blacklistProviders, name)
//			&& (mod == null || !Helpers.modBlacklisted(mod));
  }
}
