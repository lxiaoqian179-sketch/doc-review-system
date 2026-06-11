package com.example.docreview.entity;

import jakarta.persistence.*;//→ JPA 的所有註解（@Entity、@Table 等）
import lombok.Data;//→ 自動產生 getter/setter/toString
import java.time.LocalDateTime;//→ Java 的日期時間型別

@Data//Lombok 的魔法註解。幫你自動產生：
//所有欄位的 getter / setter
//toString()、equals()、hashCode()
//沒有它你要手動寫幾十行，有了它一行搞定。

@Entity//告訴 JPA：「這個 class 對應資料庫裡的一張表」，啟動後 JPA 會自動建立或對應這張表。
@Table(name = "users")//指定對應的資料表名稱是 users。//如果不寫，預設會用 class 名稱（user），但 user 在 MySQL 是保留字，所以要明確指定 users。
public class User {

    @Id//這個欄位是主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)// id 由資料庫自動遞增（1, 2, 3...），不用自己填
    private Long id;//對應 MySQL 的 BIGINT

    @Column(nullable = false, unique = true, length = 50)
    private String username;
    //nullable = false → 不能是 NULL，對應 SQL 的 NOT NULL
    //unique = true → 不能重複，帳號不能兩個人一樣
    //length = 50 → 對應 VARCHAR(50)

    @Column(nullable = false)
    private String password;
    //密碼不加 length 限制，因為之後 BCrypt 加密後會變成固定 60 字元的長字串。

    @Column(nullable = false, unique = true, length = 100)
    private String email;
    //email 也不能重複，一個信箱只能註冊一個帳號。

    @Enumerated(EnumType.STRING)//→ 存進資料庫時存字串（"USER"），而不是數字（0）。存字串可讀性更高，未來調整 Enum 順序也不會壞掉。
    @Column(nullable = false)
    private Role role = Role.USER;//→ 預設值是 USER，新建帳號自動是一般用戶。

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();//→ Java 慣例用駝峰命名（createdAt），資料庫慣例用底線（created_at），這裡做對應

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();//→ 物件建立時自動帶入當下時間

    public enum Role {
        USER, REVIEWER, ADMIN//定義三種角色，放在 User class 裡面（內部類別），因為 Role 只有 User 在用，不需要獨立成一個檔案。
    }
}