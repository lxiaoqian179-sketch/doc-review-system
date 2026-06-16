package com.example.docreview.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// @Component → 讓 Spring 自動掃描並管理這個 Filter，不需要手動 new
// OncePerRequestFilter → Spring 提供的基底類別，確保每個 request 只執行一次這個 Filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // 建構子注入 JwtTokenProvider，用來驗證 token 和取出使用者資訊
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 每個 HTTP request 進來都會執行這個方法
    // request  → 前端送來的請求（含 Header、Body 等）
    // response → 要回傳給前端的回應
    // filterChain → Filter 鏈，呼叫 doFilter 才會繼續往下執行
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 從 request 的 Header 取出 Authorization 欄位
        //    正確格式應為：Authorization: Bearer eyJhbGci...
        String authHeader = request.getHeader("Authorization");

        // 2. 如果沒有 Authorization Header，或開頭不是 "Bearer "
        //    代表這個 request 沒有帶 token → 直接放行，不做任何驗證
        //    後面由 SecurityConfig 的規則決定要不要擋住（例如 401）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 去掉 "Bearer " 前綴（7個字元），取出純 token 字串
        String token = authHeader.substring(7);

        // 4. 用 JwtTokenProvider 驗證 token 是否合法、未過期
        if (jwtTokenProvider.validateToken(token)) {

            // 5. token 合法 → 從 token 取出使用者帳號和角色
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);

            // 6. 建立 Spring Security 的認證物件
            //    UsernamePasswordAuthenticationToken 代表「已認證的使用者」
            //    參數一：principal（使用者帳號，之後可用 @AuthenticationPrincipal 取出）
            //    參數二：credentials（密碼，認證後不需要，設 null）
            //    參數三：authorities（權限清單，格式必須是 "ROLE_XXX"）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );

            // 7. 將認證物件存入 SecurityContextHolder
            //    SecurityContextHolder 是 Spring Security 的全域身份儲存區
            //    存進去之後，這個 request 的後續處理都能知道「現在是誰在操作」
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // token 驗證失敗的話，SecurityContextHolder 不會有任何認證資訊
        // SecurityConfig 的 .anyRequest().authenticated() 就會自動回傳 401

        // 8. 不管 token 有沒有，都要呼叫 doFilter 繼續執行下一個 Filter
        //    如果不呼叫，request 就會卡在這裡，前端收不到任何回應
        filterChain.doFilter(request, response);
    }
}