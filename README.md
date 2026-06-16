# 文件審核系統 Doc Review System

一個基於 Spring Boot 的文件審核後端系統，實作 JWT 身份驗證、角色權限控制、檔案上傳下載與操作日誌功能。

## 技術棧

- Java 17
- Spring Boot 3
- Spring Security 6 + JWT（jjwt 0.11.5）
- Spring Data JPA + Hibernate
- MySQL 8
- Lombok
- Springdoc OpenAPI（Swagger UI）

## 系統架構
```
前端請求
  ↓
JwtAuthenticationFilter（驗證 token，寫入 SecurityContextHolder）
  ↓
SecurityConfig（路徑權限規則）
  ↓
Controller → Service → Repository → MySQL
  ↓
GlobalExceptionHandler（統一錯誤格式）
```

## 快速啟動

### 前置條件
- JDK 17+
- MySQL 8
- Maven

### 步驟

1. 建立資料庫
```sql
CREATE DATABASE doc_review;
```

2. 設定 `application.yml`
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/doc_review
    username: 你的帳號
    password: 你的密碼
jwt:
  secret: my-super-secret-key-that-is-long-enough-32chars
  expiration: 86400000
file:
  upload-dir: uploads
```

3. 啟動專案
```bash
mvn spring-boot:run
```

4. 開啟 Swagger UI
```bash
http://localhost:8080/swagger-ui/index.html
```

## API 清單

### 認證
| 方法 | 路徑 | 說明 | 需要 token |
|---|---|---|---|
| POST | /api/auth/login | 登入取得 JWT token | 否 |
| GET | /api/auth/me | 取得當前使用者資訊 | 是 |

### 使用者
| 方法 | 路徑 | 說明 | 需要 token |
|---|---|---|---|
| POST | /api/users | 新增使用者 | 否 |
| GET | /api/users | 查詢所有使用者 | 否 |
| GET | /api/users/{id} | 查詢單一使用者 | 否 |
| PUT | /api/users/{id} | 更新使用者 | 否 |
| DELETE | /api/users/{id} | 刪除使用者 | 否 |

### 文件
| 方法 | 路徑 | 說明 | 需要 token |
|---|---|---|---|
| POST | /api/documents | 新增文件 | 是 |
| POST | /api/documents/upload | 上傳檔案 | 是 |
| GET | /api/documents | 查詢自己的文件 | 是 |
| GET | /api/documents/{id} | 查詢單一文件 | 是 |
| GET | /api/documents/{id}/download | 下載文件 | 是 |
| DELETE | /api/documents/{id} | 刪除文件 | 是 |
| GET | /api/documents/admin/all | 查詢所有文件 | 是（ADMIN） |

### 審核日誌
| 方法 | 路徑 | 說明 | 需要 token |
|---|---|---|---|
| GET | /api/audit-logs | 查詢所有日誌 | 是（ADMIN） |
| GET | /api/audit-logs/document/{id} | 查詢文件日誌 | 是（ADMIN） |

## JWT 驗證流程

1. 用戶 POST `/api/auth/login` 取得 token
2. 後續請求在 Header 帶入 `Authorization: Bearer {token}`
3. `JwtAuthenticationFilter` 攔截每個請求，驗證 token
4. 驗證成功後將使用者資訊寫入 `SecurityContextHolder`
5. Controller 透過 `SecurityContextHolder` 取得當前使用者

## 角色權限

| 角色 | 權限 |
|---|---|
| USER | 上傳、查詢、下載自己的文件 |
| ADMIN | 查詢所有文件、查看審核日誌 |