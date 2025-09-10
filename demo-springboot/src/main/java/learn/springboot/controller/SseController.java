package learn.springboot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class SseController {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    /**
     * To send events with only the data field, it should be used the SseEmitter.send(Object object) method.
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnection(){
        SseEmitter emitter = new SseEmitter();
        threadPool.execute(() -> {
            try {
                emitter.send("Hello");
                emitter.complete();
            } catch (Exception e) {
                String message = e.getMessage();
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }


    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    /**
     * To send events with the fields data, id, event, retry and comments, it should be used the SseEmitter.send(SseEmitter.SseEventBuilder builder) method.
     */
    @GetMapping(value = "/sse2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseConnection2(){
        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            emitter.complete();
            this.emitters.remove(emitter);
        });

        threadPool.execute(() -> {
            try {
                emitter.send(SseEmitter.event().reconnectTime(1000)
                        .id("main")
                        .data("Hello")
                        .comment("这里是注释，发送的数据不会被数据接收端处理"));
                emitter.complete();
            } catch (Exception e) {
                String message = e.getMessage();
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        });


        return emitter;
    }


}
