package learn.compile;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 将编译好的.class文件保存到内存当中，这里的内存也就是map映射当中
 */
@Deprecated
@SuppressWarnings("rawtypes")
public final class MemoryJavaFileManager extends ForwardingJavaFileManager {
    private final static String EXT = JavaFileObject.Kind.SOURCE.extension;// Java源文件的扩展名
    private Map<String, byte[]> classBytes;// 用于存放.class文件的内存

    @SuppressWarnings("unchecked")
    public MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
        classBytes = new HashMap<String, byte[]>();
    }

    @Override
    public void close() throws IOException {
        classBytes = new HashMap<String, byte[]>();
        // super.close();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(
        JavaFileManager.Location location, String className,
        JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new ClassFileObject(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }


    /**
     * 将Java字节码存储到classBytes映射中的文件对象
     */
    private class ClassFileObject extends SimpleJavaFileObject {
        private String name;

        /**
         * @param name className
         */
        ClassFileObject(String name) {
            super(toURI(name), Kind.CLASS);
            this.name = name;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    // 这里可以用于将class的字节数组保存到其他地方
                    classBytes.put(name, bos.toByteArray());
                }
            };
        }
    }

    static URI toURI(String name) {
        File file = new File(name);
        if (file.exists()) {// 如果文件存在，返回他的URI
            return file.toURI();
        } else {
            try {
                final StringBuilder newUri = new StringBuilder();
                newUri.append("mfm:///");
                newUri.append(name.replace('.', '/'));
                if (name.endsWith(JavaFileObject.Kind.SOURCE.extension)) {
                    newUri.replace(newUri.length() - JavaFileObject.Kind.SOURCE.extension.length(), newUri.length(), JavaFileObject.Kind.SOURCE.extension);
                }
                return URI.create(newUri.toString());
            } catch (Exception exp) {
                return URI.create("mfm:///com/sun/script/java/java_source");
            }
        }
    }
}

