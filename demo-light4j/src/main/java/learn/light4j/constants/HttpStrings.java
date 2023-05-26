package learn.light4j.constants;

import com.networknt.httpstring.HttpStringConstants;
import io.undertow.util.HttpString;

/**
 * @author Zephyr
 * @since 2020-12-01.
 */
public class HttpStrings extends HttpStringConstants{

    public static final HttpString Content_Type = HttpString.tryFromString("Content-Type");
    public static final HttpString Authorization = HttpString.tryFromString("Authorization");

}
