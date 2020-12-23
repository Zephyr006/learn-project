package learn.light4j.constants;

import io.undertow.util.AttachmentKey;

import java.util.Map;

/**
 * @author Zephyr
 * @date 2020/12/2.
 */
public class AttachmentConstants extends com.networknt.httpstring.AttachmentConstants {

    public static final AttachmentKey<Map> USER_INFO = AttachmentKey.create(Map.class);

}
