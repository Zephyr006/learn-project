package learn.demo.webflux.controller;

import learn.demo.webflux.entity.Blog;
import learn.demo.webflux.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Zephyr
 * @date 2020/11/22.
 */
@RestController
@RequestMapping("/mongo")
public class DemoMongoController {

    @Autowired
    private BlogRepository blogRepository;

    // ====== crud 基本操作 ======

    @GetMapping("blogs")
    public Flux<Blog> getAll() {
        //return Flux.just(Blog.builder().id(String.valueOf(213)).name("测试的博客名").readCount(123).build());
        return blogRepository.findAll();
    }

    @GetMapping("/blog/{id}")
    public Mono<Blog> findById(@PathVariable("id") String id) {
        return blogRepository.findById(id);
    }

    @PostMapping("blog")
    public Mono<Blog> create(@RequestBody @NotNull @Valid Blog blog) {
        // 一律视为插入操作，不允许自带id，使用mongo自动生成的id
        blog.setId(null);
        return blogRepository.insert(blog);
    }


    /**
     * save 等价于 insertOrUpdate
     */
    @PutMapping("blog/{id}")
    public Mono<ResponseEntity<Blog>> update(@PathVariable("id") String id, @RequestBody @NotNull Blog blog) {
        return blogRepository.findById(id)
                // 如果数据库中存在对应值，删除记录并返回结果
                .flatMap(existBlog -> {
                    blog.setId(existBlog.getId());
                    return blogRepository.save(blog);
                }).map(existBlog -> new ResponseEntity<>(existBlog, HttpStatus.OK))
                // 如果没有对应的结果值，返回 404 not found
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/blog/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        // repository.deleteById 没有返回值，不能判断数据是否存在
        //Mono<Void> voidMono = blogRepository.deleteById(id);

        return blogRepository.findById(id)
                // 当要操作数据并返回一个Mono的时候，使用flatMap；如果不操作数据，只是转换数据，使用map
                .flatMap(blog -> this.blogRepository.delete(blog).then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                // 没有找到要删除的记录时，返回 404 not found
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    // =========== 高级操作 ============


    @GetMapping("/blogs/name/{name}")
    public Flux<Blog> findByName(@PathVariable("name") String name) {
        return blogRepository.findByNameLike(name);
    }

    @GetMapping("/blogs/readCount")
    public Flux<Blog> findByName() {
        return blogRepository.findReadCountByQuery();
    }
}
