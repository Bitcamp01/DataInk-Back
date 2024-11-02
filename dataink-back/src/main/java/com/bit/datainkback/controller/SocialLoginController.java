//package com.bit.datainkback.controller;
//
//import com.bit.datainkback.service.GoogleService;  // 경로 수정 완료
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/login")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//@Slf4j
//public class SocialLoginController {
//
//    private final GoogleService googleService;
//
//    // 구글 로그인: 액세스 토큰을 통해 사용자 정보 반환
//    @GetMapping("/google")
//    public String googleLogin(@RequestParam("access_token") String accessToken) {
//        log.info("google login access token: " + accessToken);
//        // 액세스 토큰을 통해 사용자 정보 가져오기
//        return googleService.getUserInfo(accessToken);
//    }
//
//    // 카카오 로그인: 미완성 상태이므로 주석 처리
//    /*
//    private final KakaoService kakaoService;
//    private final LoginService loginService;
//
//    @GetMapping("/kakao")
//    public String KakaoLogin (@RequestParam String code) {
//        String token = kakaoService.getKaKaoAccessToken(code);
//        KakaoDataForm res = kakaoService.createKakaoUser(token);
//        return loginService.KakaoLogin(res);
//    }
//    */
//
//    // 네이버 로그인: 미완성 상태이므로 주석 처리
//    /*
//    private final NaverService naverService;
//
//    @GetMapping("/naver")
//    public String NaverLogin (@RequestParam String code, String state) {
//        String accessToken = naverService.getNaverAccessToken(code);
//        return naverService.getUserInfo(accessToken);
//    }
//    */
//}
