# 開發進度：Phase 2 - 後端遷移

本文件記錄 Phase 2 核心功能的後端開發進度。

## 開發目標

將後端資料儲存從 `Firestore` 完全遷移至 `PostgreSQL`，並確保 API 功能正常。

## 任務狀態

-   [x] **完成 `User` 功能遷移**
    -   [x] 建立 `users` 資料表。
    -   [x] 重構 `UserService` 與 `UserController` 以使用 `PostgreSQL`。

-   [x] **完成 `Pet` 功能遷移**
    -   [x] 建立 `pets` 與 `pet_managers` 資料表。
    -   [x] 重構 `PetService` 與 `PetController`。
    -   [x] 實現寵物與使用者的多對多管理關係。

-   [x] **完成 `Feeding` 功能遷移**
    -   [x] 建立 `feedings` 資料表。
    -   [x] 重構 `FeedingService` 與 `FeedingController`。
    -   [x] 實作「重複餵食」的防呆機制。
    -   [x] 實作「取得當前狀態」的 API 端點。

-   [x] **同步資料庫腳本**
    -   [x] 建立並更新 `db/init.sql`，使其與程式碼中的 `Exposed` 資料表定義保持一致。
    *   [x] 為所有外鍵加上 `ON DELETE CASCADE` 規則，以確保資料完整性。

-   [x] **修正資料庫連線**
    *   [x] 更新 `DatabaseFactory.kt` 中的預設連線資訊，以符合 `docker-compose.yml` 的設定 (使用 `5071` port)。
