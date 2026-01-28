package de.brickforceaurora.launcher;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import de.brickforceaurora.launcher.util.IOUtil;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.resource.source.IDataSource;

public final class TextureAtlas {

    @Retention(RetentionPolicy.RUNTIME)
    private static @interface Texture {

        String path();

    }

    public static class ImTexture {

        public final String name;
        public final int id, width, height;
        public final float aspect;

        private ImTexture(final String name, final int id, final int width, final int height) {
            this.name = name;
            this.id = id;
            this.width = width;
            this.height = height;
            this.aspect = width / (float) height;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj == this || obj instanceof final ImTexture tex && id == tex.id
                || obj instanceof final Number num && id == num.intValue();
        }

    }

    public static class ImTextureBundle {

        public final String name;
        public final ReferenceList<ImTexture> textures;

        private ImTextureBundle(final String name, final ReferenceList<ImTexture> textures) {
            this.name = name;
            this.textures = textures;
        }

    }

    private TextureAtlas() {
        throw new UnsupportedOperationException();
    }

    static void load(final SnowFrame<LauncherApp> frame) {
        for (final Field field : TextureAtlas.class.getDeclaredFields()) {
            final Texture textureInfo = field.getDeclaredAnnotation(Texture.class);
            if (textureInfo == null) {
                continue;
            }
            final Class<?> type = field.getType();
            boolean bundle = false;
            if (!type.equals(ImTexture.class) && !(bundle = type.equals(ImTextureBundle.class))) {
                frame.logger().error("Invalid field type for texture '{0}'", textureInfo.path());
                continue;
            }
            try {
                final IDataSource source = frame.externalResource("jar://image/%s".formatted(textureInfo.path()),
                    "data://resources/image/%s".formatted(textureInfo.path()), true);
                if (!source.exists()) {
                    frame.logger().error("Couldn't find texture '{0}'", textureInfo.path());
                    continue;
                }
                if (bundle) {
                    final IDataSource[] contents = source.getContents();
                    final ReferenceList<ImTexture> textures = new ReferenceArrayList<>(contents.length);
                    for (final IDataSource content : contents) {
                        if (content.isContainer()) {
                            continue;
                        }
                        try (MemoryStack stack = MemoryStack.stackPush()) {
                            textures.add(loadTexture(stack, source.name() + '/' + content.name(), content));
                        } catch (final Throwable thr) {
                            frame.logger().error("Failed to load texture '{0}'", thr, textureInfo.path() + '/' + content.name());
                        }
                    }
                    field.set(null, new ImTextureBundle(source.name(),
                        textures.isEmpty() ? ReferenceLists.emptyList() : ReferenceLists.unmodifiable(textures)));
                    continue;
                }
                try (MemoryStack stack = MemoryStack.stackPush()) {
                    field.set(null, loadTexture(stack, source.name(), source));
                }
            } catch (final Throwable thr) {
                frame.logger().error("Failed to load texture '{0}'", thr, textureInfo.path());
            }
        }
    }

    private static ImTexture loadTexture(final MemoryStack stack, final String name, final IDataSource source) throws IOException {
        final IntBuffer channelsBuf = stack.mallocInt(1);
        final IntBuffer widthBuf = stack.mallocInt(1);
        final IntBuffer heightBuf = stack.mallocInt(1);
        final ByteBuffer image = STBImage.stbi_load(IOUtil.asPath(source).toAbsolutePath().toString(), widthBuf, heightBuf, channelsBuf, 4);
        if (image == null) {
            throw new IOException("Unable to load image from resource");
        }
        final int width = widthBuf.get();
        final int height = heightBuf.get();

        final int id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        return new ImTexture(name, id, width, height);
    }

    @Texture(path = "logo.png")
    public static ImTexture LOGO;

    @Texture(path = "banner.png")
    public static ImTexture BANNER;

    @Texture(path = "banner_whitebg.png")
    public static ImTexture BANNER_WHITEBG;

    @Texture(path = "panorama")
    public static ImTextureBundle PANORAMA;

}
