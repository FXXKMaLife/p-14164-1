package com.back.domain.wiseSaying.wiseSaying.controller;


import com.back.domain.wiseSaying.wiseSaying.entity.WiseSaying;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WiseSayingController {
    private int lastId = 0;
    private final List<WiseSaying> wiseSayings = new ArrayList<>() {{
        add(new WiseSaying(++lastId, "명언 1", "작가 1"));
        add(new WiseSaying(++lastId, "명언 2", "작가 2"));
        add(new WiseSaying(++lastId, "명언 3", "작가 3"));
        add(new WiseSaying(++lastId, "명언 4", "작가 4"));
        add(new WiseSaying(++lastId, "명언 5", "작가 5"));
    }};

    @GetMapping("/wiseSayings/write")//액션메서드: 브라우저가 바로 호출할 수 있는 메서드
    @ResponseBody//응답이 본문이 된다
    public String write(
            @RequestParam(defaultValue = "내용") String content,
            @RequestParam(defaultValue = "작가") String author
    ) {
        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }

        if (content.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }

        int id = ++lastId;

        WiseSaying wiseSaying = new WiseSaying(id, content, author); //리모콘 만들기

        wiseSayings.add(wiseSaying);//만든 리모콘 수집

        return "%d번 명언이 생성되었습니다.".formatted(id);
    }

    @GetMapping("/wiseSayings")
    @ResponseBody
    public String list() {
        return "<ul>"
                + wiseSayings
                .stream()
                .map(wiseSaying ->
                        "<li>%d / %s / %s</li>".formatted(wiseSaying.getId(), wiseSaying.getAuthor(), wiseSaying.getContent())
                )
                .collect(Collectors.joining(""))
                + "</ul>";
    }

    @GetMapping("/wiseSayings/delete/{id}")
    @ResponseBody
    public String delete(
            @PathVariable int id
    ) {
        WiseSaying wiseSaying = findById(id)
                .orElseThrow( //내용이 있으면 리턴하고 아니면 throw
                        () -> new IllegalArgumentException("%d번 명언은 존재하지 않습니다.".formatted(id))
                );

        wiseSayings.remove(wiseSaying);

        return "%d번 명언이 삭제되었습니다.".formatted(id);
    }
    @GetMapping("/wiseSayings/modify/{id}")
    @ResponseBody
    public String modify( //수정기능
            @PathVariable int id,
            @RequestParam(defaultValue = "") String content,
            @RequestParam(defaultValue = "") String author
    ) {
        if (content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or blank");
        }

        if (author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }

        WiseSaying wiseSaying = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("%d번 명언은 존재하지 않습니다.".formatted(id))
                );

        wiseSaying.modify(content, author);

        return "%d번 명언이 수정되었습니다.".formatted(id);
    }
    private Optional<WiseSaying> findById(int id) {
        return wiseSayings
                .stream()
                .filter(wiseSaying -> wiseSaying.getId() == id)
                .findFirst();
    }
}