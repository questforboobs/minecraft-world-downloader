package game.data.chunk.entity.metadata;

import game.Game;
import packets.DataTypeProvider;
import se.llbit.nbt.CompoundTag;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class MetaData {
    private final static int TERMINATOR = 0xFF;
    public abstract void addNbtTags(CompoundTag nbt);

    MetaData() { }

    public void parse(DataTypeProvider provider) {
        while (true) {
            int index = provider.readNext() & 0xFF;
            if (index == TERMINATOR) { break; }

            int type = provider.readVarInt();

            Consumer<DataTypeProvider> indexHandler = getIndexHandler(index);
            Consumer<DataTypeProvider> typeHandler = getTypeHandler(type);

            if (indexHandler == null && typeHandler == null) { break; }

            Objects.requireNonNullElse(indexHandler, typeHandler).accept(provider);
        }
    }

    /**
     * Returns a MetaData object of the correct version.
     * @return the metadata matching the given version
     */
    public static MetaData getVersionedMetaData() {
        if (Game.getProtocolVersion() >= 341) {
            return new MetaData_1_13();
        } else {
            return new MetaData_1_12();
        }
    }

    public abstract Consumer<DataTypeProvider> getTypeHandler(int i);
    public abstract Consumer<DataTypeProvider> getIndexHandler(int i);
}
