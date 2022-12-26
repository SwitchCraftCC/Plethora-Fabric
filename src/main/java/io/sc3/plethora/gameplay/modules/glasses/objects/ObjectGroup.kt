package io.sc3.plethora.gameplay.modules.glasses.objects

import io.sc3.plethora.gameplay.modules.glasses.canvas.CanvasHandler

/**
 * Represents a holder for [BaseObject]s.
 */
interface ObjectGroup {
  /**
   * @return The ID for this group.
   */
  val id: Int

  /**
   * A group for 2D objects
   */
  interface Group2d : ObjectGroup

  /**
   * A group for 2D objects with a fixed size
   */
  interface Frame2d : Group2d {
    val width: Int
      get() = CanvasHandler.WIDTH
    val height: Int
      get() = CanvasHandler.HEIGHT
  }

  /**
   * A group for 3D objects
   */
  interface Group3d : ObjectGroup

  /**
   * The "origin" for all 3D objects
   */
  interface Origin3d : ObjectGroup
}
