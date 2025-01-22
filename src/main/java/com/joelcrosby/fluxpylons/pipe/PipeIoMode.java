package com.joelcrosby.fluxpylons.pipe;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum PipeIoMode {
    INSERT_EXTRACT(0),
    INSERT(1),
    EXTRACT(2),
    DISABLED(3);

    private final int ioMode;

    public static final IntFunction<PipeIoMode> BY_ID =
            ByIdMap.continuous(
                    PipeIoMode::getId,
                    PipeIoMode.values(),
            ByIdMap.OutOfBoundsStrategy.ZERO
    );

    public int getId() {
        return ioMode;
    }

    PipeIoMode(int id) {
        ioMode = id;
    }

    public static final StreamCodec<ByteBuf, PipeIoMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PipeIoMode::getId);
}
