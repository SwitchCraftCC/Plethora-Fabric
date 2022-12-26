package io.sc3.plethora.api.converter;

/**
 * Marker interface stating this method should not consume converted objects.
 *
 * This will only target the original class, and not converted objects. This is useful if you will
 * do conversion yourself.
 */
public interface IConverterExcludeMethod {
}
