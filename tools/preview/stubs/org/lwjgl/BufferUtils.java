package org.lwjgl;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/** Stub de pre-visualizacao: buffer direto em ordem nativa, como o LWJGL. */
public final class BufferUtils {
    private BufferUtils(){}
    public static ByteBuffer createByteBuffer(int size){return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());}
}
