package learn.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.net.URI;
import java.nio.CharBuffer;

/**
 * 一个文件对象，用来表示从string中获取到的source，以下类容是按照jdk给出的例子写的
 */
public class SourceCodeJavaFileObject extends SimpleJavaFileObject {
    // The source code of this "file".
    final String sourceCode;

    /**
     * @param name 此文件对象表示的编译单元的name ,name必须是".java"结尾,并且需要与源码中的类名一致,表示一个java源码文件
     * @param code 此文件对象表示的编译单元source的code
     */
    public SourceCodeJavaFileObject(String name, String code) {
        super(toURI(parseIfNecessary(name, code)), Kind.SOURCE);
        this.sourceCode = code;
    }

    @Override
    public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
        return CharBuffer.wrap(sourceCode);
    }

    // @SuppressWarnings("unused")
    // public Reader openReader() {
    //     return new StringReader(sourceCode);
    // }

    /**
     * 如果没有给出明确的名字,则可以从'规范的代码'中匹配出正确的类名,如果类名比较特殊,则必须手动指定
     */
    private static String parseIfNecessary(String name, String code) {
        if (name == null || name.length() == 0) {
            boolean nameStarted = false;
            StringBuilder classNameBuilder = new StringBuilder();
            for (int i = code.indexOf("class") + 5; i > 0 && i < code.length(); i++) {
                char c = code.charAt(i);
                if (nameStarted) {
                    if (Character.isLetterOrDigit(c) || c == '$' || c == '_') {
                        classNameBuilder.append(c);
                    } else {
                        break;
                    }
                } else {
                    // 在Java中，类文件名必须以字母、下划线或美元符号开头，并且不能以数字开头
                    if (Character.isLetter(c) || c == '$' || c == '_') {
                        nameStarted = true;
                        classNameBuilder.append(c);
                    }
                }
            }
            name = classNameBuilder.append(Kind.SOURCE.extension).toString();
        }
        assert name.endsWith(Kind.SOURCE.extension);
        return name;
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
                if (name.endsWith(Kind.SOURCE.extension)) {
                    newUri.replace(newUri.length() - Kind.SOURCE.extension.length(), newUri.length(), Kind.SOURCE.extension);
                }
                return URI.create(newUri.toString());
            } catch (Exception exp) {
                return URI.create("mfm:///com/sun/script/java/java_source");
            }
        }
    }
}
