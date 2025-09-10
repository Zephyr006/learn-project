package learn.springcloud.gateway.client;


// import learn.springcloud.config.client.api.ProductApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("GOODS")
public interface ProductClient
        // extends ProductApi
{
}
