package com.back;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;


@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final MemberService memberService;
    @Autowired
    @Lazy
    private AppConfig self;

    @Bean
    HtmlRenderer htmlRenderer() {
        return HtmlRenderer.builder().build();
    }

    @Bean
    Parser parser() {
        return Parser.builder().build();
    }

    @Bean
    int version(){
        return 55;
    }
    @Bean
    ApplicationRunner makeSampleMemberDataApplicationRunner() {
        return args -> {
            self.work1(); //프록시
            this.work2(); //진짜객체
            //셀프나 디스나 결과는 같다
        };
    }
    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        Member memberSystem = memberService.join("system", "1234", "시스템");
        Member memberAdmin = memberService.join("admin", "1234", "관리자");
        Member memberUser1 = memberService.join("user1", "1234", "유저1");
        Member memberUser2 = memberService.join("user2", "1234", "유저2");
        Member memberUser3 = memberService.join("user3", "1234", "유저3");
    }
    @Transactional
    public void work2() {
        Member memberUser2 = memberService.findByUsername("user2").get();

        memberUser2.setNickname("유저2 New");
    }

}