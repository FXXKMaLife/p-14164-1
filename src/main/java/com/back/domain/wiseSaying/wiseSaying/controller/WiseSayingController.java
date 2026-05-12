package com.back.domain.wiseSaying.wiseSaying.controller;


import com.back.domain.wiseSaying.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.wiseSaying.service.WiseSayingService;
import lombok.RequiredArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WiseSayingController {

    private final WiseSayingService wiseSayingService;
    private final HtmlRenderer htmlRenderer;
    private final Parser parser;

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

        WiseSaying wiseSaying = wiseSayingService.write(content, author);

        return "%d번 명언이 생성되었습니다.".formatted(wiseSaying.getId());
    }
    //다건조화
    @GetMapping("/wiseSayings")
    @ResponseBody
    public String list() {
        return "<ul>"
                + wiseSayingService.findAll()
                .stream()
                .map(wiseSaying ->
                        "<li>%d / %s / %s</li>".formatted(wiseSaying.getId(), wiseSaying.getAuthor(), wiseSaying.getContent())
                )
                .collect(Collectors.joining(""))
                + "</ul>";
    }
    //단건 조회 상세페이지
    @GetMapping("/wiseSayings/{id}")
    @ResponseBody
    public String detail(@PathVariable int id) {
        WiseSaying wiseSaying = wiseSayingService.findById(id).get();
        // 문자열을 파싱해서 Node 트리 구조로 변환
        Node document = parser.parse(wiseSaying.getContent());
        // Node를 HTML 문자열로 렌더링
        String html = htmlRenderer.render(document);

        return """
             
                <div>번호 : %d</div>
                <div>작가 : %s</div>
                <div>%s</div>
                """.formatted(wiseSaying.getId(), wiseSaying.getAuthor(), html);
    }

    @GetMapping("/wiseSayings/{id}/delete")
    @ResponseBody
    public String delete(
            @PathVariable int id
    ) {
        WiseSaying wiseSaying = wiseSayingService.findById(id)
                .orElseThrow( //내용이 있으면 리턴하고 아니면 throw
                        () -> new IllegalArgumentException("%d번 명언은 존재하지 않습니다.".formatted(id))
                );

        wiseSayingService.delete(wiseSaying);

        return "%d번 명언이 삭제되었습니다.".formatted(id);
    }
    @GetMapping("/wiseSayings/{id}/modify")//주소 수정
    @ResponseBody
    public String modify( //수정기능
            @PathVariable int id,
            @RequestParam(defaultValue = "") String content, //미리 작성해주면 null값이 들어간 경우를 걱정하지 않아도 됨
            @RequestParam(defaultValue = "") String author
    ) {
        if (content.isBlank()) {//그래서 예외처리 조건이 깔끔해진다
            throw new IllegalArgumentException("Content cannot be null or blank");
        }

        if (author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be null or blank");
        }

        WiseSaying wiseSaying = wiseSayingService.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("%d번 명언은 존재하지 않습니다.".formatted(id))
                );

        wiseSayingService.modify(wiseSaying, content, author);

        return "%d번 명언이 수정되었습니다.".formatted(id);
    }

}