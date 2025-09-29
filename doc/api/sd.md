# 系統設計 (API) - Kotlin/Ktor

## 1. 技術棧 (Tech Stack)

*   **語言 (Language)**: `Kotlin`
*   **框架 (Framework)**: `Ktor`
*   **建置工具 (Build Tool)**: `Gradle` with Kotlin DSL (`build.gradle.kts`)
*   **伺服器引擎 (Engine)**: `Netty`
*   **非同步處理 (Asynchrony)**: `Kotlin Coroutines`
*   **資料庫 (Database)**: `PostgreSQL`
*   **資料庫互動 (Database Interaction)**: `Exposed`
*   **認證 (Authentication)**: `Firebase Authentication`
*   **推播通知 (Push Notifications)**: `Firebase Cloud Messaging` (未來規劃)

## 2. 專案結構 (Project Structure)

```
backend/
├── build.gradle.kts
├── settings.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── com/example/  // 我們的根 package
                ├── Application.kt      // Ktor 伺服器進入點與模組設定
                ├── plugins/            // Ktor 外掛設定 (Routing, Serialization)
                ├── security/           // Firebase 認證相關
                ├── features/           // 各功能模組 (e.g., feedings, users, pets)
                │   ├── user/
                │   │   ├── UserController.kt
                │   │   ├── UserService.kt
                │   │   ├── User.kt         // Data Class
                │   │   └── Users.kt        // Exposed Table a a a
                │   └── ...
                └── core/               // 核心服務
                    ├── FirebaseAdmin.kt // Firebase Admin SDK 初始化
                    └── DatabaseFactory.kt // Exposed 與 PostgreSQL 資料庫初始化
```

## 3. API 端點設計 (API Endpoint Design)

API 端點前綴為 `/api/v1`，後端統一運行在 **5070 端口**。

### 使用者管理
*   `POST /users`: 建立新使用者
*   `GET /users/{userId}`: 取得使用者資訊
*   `PUT /users/{userId}`: 更新使用者資訊
*   `POST /users/batch`: 批量取得使用者資料

### 寵物管理
*   `GET /pets`: 取得使用者所屬家庭的所有寵物列表
*   `POST /pets`: 新增一隻寵物，並將自己設為管理者
*   `DELETE /pets/{petId}`: 刪除寵物
*   `POST /pets/{petId}/managers`: 將自己新增為特定寵物的管理者
*   `DELETE /pets/{petId}/managers`: 將自己從特定寵物的管理者名單中移除

### 餵食記錄
*   `GET /feedings?petId={petId}`: 取得特定寵物的餵食紀錄（可選 startTime, endTime 過濾）
*   `POST /feedings`: 新增餵食紀錄
*   `POST /feedings/overwrite`: 覆蓋上一餐記錄
*   `GET /feedings/status/current?petId={petId}`: 取得特定寵物當前的餵食狀態

(未來功能)
*   `POST /pets/{petId}/weights`: 新增體重紀錄。
*   `GET /pets/{petId}/weights`: 取得歷史體重紀錄。
*   `GET /albums/photos`: 取得相簿照片。

## 4. 認證機制 (Authentication)

*   所有需保護的 API 請求，其 `Authorization` Header 必須包含由 `Firebase Authentication` 簽發的 `Bearer <ID_TOKEN>`。
*   Ktor 的認證 `plugin` 會攔截請求，並使用 `Firebase Admin SDK` 來驗證 `ID Token` 的有效性。驗證成功後，會將使用者的 `UID` 等資訊附加到請求中，供後續的業務邏輯使用。

## 5. 資料庫設計 (PostgreSQL Schema)

資料庫已從 `Firestore` 遷移至 `PostgreSQL`，採用關聯式模型。

*   **Table: `users`**
    *   `id` (VARCHAR(255), PK): 使用者的 Firebase UID。
    *   `name` (VARCHAR(255))
    *   `profile_picture_url` (VARCHAR(255), nullable)

*   **Table: `pets`**
    *   `id` (VARCHAR(255), PK): 寵物的唯一 ID。
    *   `name` (VARCHAR(255))
    *   `photo_url` (VARCHAR(255), nullable)

*   **Table: `pet_managers`** (多對多關聯表)
    *   `pet_id` (VARCHAR(255), PK, FK to `pets.id` ON DELETE CASCADE)
    *   `user_id` (VARCHAR(255), PK, FK to `users.id` ON DELETE CASCADE)

*   **Table: `feedings`**
    *   `id` (VARCHAR(255), PK): 餵食紀錄的唯一 ID。
    *   `user_id` (VARCHAR(255), FK to `users.id` ON DELETE CASCADE): 執行餵食的使用者。
    *   `pet_id` (VARCHAR(255), FK to `pets.id` ON DELETE CASCADE): 被餵食的寵物。
    *   `timestamp` (BIGINT): 事件的 UTC 毫秒時間戳。
    *   `type` (VARCHAR(50)): 類型，如 "meal" 或 "snack"。
    *   `photo_url` (VARCHAR(255), nullable)
