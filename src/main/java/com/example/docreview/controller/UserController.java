package com.example.docreview.controller;

import com.example.docreview.entity.User;
import com.example.docreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;//→ Lombok 自動產生建構子（用來注入 userRepository）
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//@RestController 相關的 web.bind.annotation.* → 所有 REST API 需要的註解
import java.time.LocalDateTime;
import java.util.List;//Java 的清單型別

@RestController//告訴 Spring：「這個 class 是 REST API 的控制器」，裡面每個方法的回傳值會自動轉成 JSON。
@RequestMapping("/api/users")//設定這個 Controller 的基底路徑，所有方法的路徑都從 /api/users 開始。
@RequiredArgsConstructor//Lombok 自動產生這段程式碼（你不用手寫）：
//public UserController(UserRepository userRepository) {
//    this.userRepository = userRepository;
//}
//這是 Spring 的「依賴注入」，讓 Controller 可以使用 UserRepository。
public class UserController {

    private final UserRepository userRepository;
    //宣告一個 UserRepository 變數，final 代表注入後不能被換掉，搭配 @RequiredArgsConstructor 使用。

    @PostMapping// 1.新增使用者
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
    //@PostMapping → 對應 POST /api/users
    //@RequestBody User user → 把前端送來的 JSON 自動轉成 User 物件
    //save(user) → 存進資料庫，回傳存好的 User（含自動產生的 id）

    @GetMapping//2.查詢所有使用者
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    //@GetMapping → 對應 GET /api/users 這個請求
    //findAll() → 呼叫 Repository 撈出所有 User
    //回傳 List<User>，Spring 自動把它轉成 JSON 陣列回傳給前端


    @GetMapping("/{id}")// 3.查詢單一使用者
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")//4.更新使用者
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User updated) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(updated.getUsername());
                    user.setEmail(updated.getEmail());
                    user.setRole(updated.getRole());
                    user.setUpdatedAt(LocalDateTime.now()); // ← 新增這行，存入修改時間
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")// 5.刪除使用者
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

