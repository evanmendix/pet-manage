# 系統設計 (API) - Kotlin/Ktor

## 1. 技術棧 (Tech Stack)

*   **語言 (Language)**: `Kotlin`
*   **框架 (Framework)**: `Ktor`
*   **建置工具 (Build Tool)**: `Gradle` with Kotlin DSL (`build.gradle.kts`)
*   **伺服器引擎 (Engine)**: `Netty`
*   **非同步處理 (Asynchrony)**: `Kotlin Coroutines`
*   **資料庫 (Database)**: `Google Firestore`
*   **認證 (Authentication)**: `Firebase Authentication`

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
                ├── features/           // 各功能模組 (e.g., feedings, users)
                │   ├── feeding/
                │   │   ├── FeedingController.kt
                │   │   ├── FeedingService.kt
                │   │   └── Feeding.kt  // Data Class
                │   └── user/
                │       └── ...
                └── core/               // 核心服務
                    └── FirebaseAdmin.kt // Firebase Admin SDK 初始化
```

## 3. API 端點設計 (API Endpoint Design)

API 端點維持不變，前綴為 `/api/v1`。

*   `POST /users`: 建立新使用者。
*   `PUT /users/{userId}`: 更新使用者資訊。
*   `GET /pets`: 取得家庭中的寵物列表。
*   `POST /pets`: 在家庭中新增一隻寵物。
*   `POST /pets/{petId}/managers`: 將自己新增為特定寵物的管理者。
*   `DELETE /pets/{petId}/managers`: 將自己從特定寵物的管理者名單中移除。
*   `POST /feedings`: 新增餵食紀錄。
*   `GET /feedings?limit=30`: 取得最近的餵食紀錄。
*   `GET /status/current`: 取得當前餵食狀態。
*   `POST /pets/{petId}/weights`: 新增體重紀錄。
*   `GET /pets/{petId}/weights`: 取得歷史體重紀錄。
*   `GET /albums/photos`: 取得相簿照片。

## 4. 認證機制 (Authentication)

*   所有需保護的 API 請求，其 `Authorization` Header 必須包含由 `Firebase Authentication` 簽發的 `Bearer <ID_TOKEN>`。
*   Ktor 將會有一個認證 `plugin`，它會攔截請求，並使用 `Firebase Admin SDK` 來驗證 `ID Token` 的有效性。驗證成功後，會將使用者的 `UID` 等資訊附加到請求中，供後續的業務邏輯使用。

## 5. 資料庫設計 (Firestore Schema)

資料庫結構維持不變，繼續使用 `Firestore`。

*   **Collection: `families`**
    *   **Document: `{familyId}`**
        *   **Collection: `users`**
            *   **Document: `{userId}`** (來自 `Firebase Auth` 的 `UID`)
                *   `name`: String
                *   `profilePictureUrl`: String
                *   `fcmToken`: String
        *   **Collection: `pets`**
            *   **Document: `{petId}`**
                *   `id`: String
                *   `name`: String
                *   `photoUrl`: String
                *   `managingUserIds`: List<String> (管理此寵物的使用者 UID 列表)
                *   **Collection: `feedings`**
                    *   **Document: `{feedingId}`**
                        *   ... (餵食紀錄)
                *   **Collection: `weights`**
                    *   **Document: `{weightId}`**
                        *   ... (體重紀錄)
