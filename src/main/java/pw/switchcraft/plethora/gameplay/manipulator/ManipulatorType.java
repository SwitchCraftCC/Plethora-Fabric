package pw.switchcraft.plethora.gameplay.manipulator;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import pw.switchcraft.plethora.util.MatrixHelpers;

import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.OFFSET;
import static pw.switchcraft.plethora.gameplay.manipulator.ManipulatorBlock.PIX;

public enum ManipulatorType implements StringIdentifiable {
    MARK_1(0.5f, new Box(PIX * 5, OFFSET, PIX * 5, PIX * 11, OFFSET + PIX, PIX * 11)),
    MARK_2(0.25f,
        new Box(PIX * 3, OFFSET, PIX * 3, PIX * 5, OFFSET + PIX, PIX * 5),
        new Box(PIX * 3, OFFSET, PIX * 11, PIX * 5, OFFSET + PIX, PIX * 13),
        new Box(PIX * 11, OFFSET, PIX * 3, PIX * 13, OFFSET + PIX, PIX * 5),
        new Box(PIX * 11, OFFSET, PIX * 11, PIX * 13, OFFSET + PIX, PIX * 13),
        new Box(PIX * 7, OFFSET, PIX * 7, PIX * 9, OFFSET + PIX, PIX * 9)
    );

    private final String name;

    private final Box[] boxes;
    private final Box[][] facingBoxes;
    public final float scale;

    ManipulatorType(float scale, Box... boxes) {
        name = name().toLowerCase();
        this.scale = scale;
        this.boxes = boxes;
        facingBoxes = new Box[6][];
    }

    @Override
    public String asString() {
        return name;
    }

    public int size() {
        return boxes.length;
    }

    public Box[] boxesFor(Direction facing) {
        Box[] cached = facingBoxes[facing.ordinal()];
        if (cached != null) return cached;

        Matrix4f m = MatrixHelpers.matrixFor(facing);
        cached = new Box[boxes.length];
        for (int i = 0; i < boxes.length; i++) cached[i] = MatrixHelpers.transform(boxes[i], m);

        return facingBoxes[facing.ordinal()] = cached;
    }
}
