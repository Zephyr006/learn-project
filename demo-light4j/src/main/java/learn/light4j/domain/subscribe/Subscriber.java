package learn.light4j.domain.subscribe;

import io.undertow.server.ServerConnection;
import learn.light4j.domain.subscribe.request.SubscribeCriteria;
import lombok.Builder;
import lombok.Data;
import org.xnio.channels.StreamSinkChannel;

/**
 * @author: cao
 * @time: 2020-12/10 6:01 下午
 */
@Data
@Builder
public class Subscriber {

    private Integer id;
    private SubscribeCriteria criteria;
    private ServerConnection connection;
    private StreamSinkChannel channel;
}
