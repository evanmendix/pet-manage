# 開發計畫 (Development Plan)

本文件旨在將專案從設計階段推向實作階段，提供一個清晰、分階段的開發路線圖。
**注意：本文件的後端部分已更新，以反映從 `Firestore` 到 `PostgreSQL` 的技術遷移。**

## 開發階段

### Phase 1: 後端基礎建設 (API)

*   **目標**: 建立一個可以管理使用者、寵物和餵食紀錄的基礎 API 服務。
*   **技術選型**:
    *   後端框架: `Ktor`
    *   資料庫: `PostgreSQL` (使用 `Exposed` 函式庫)
    *   身份驗證: `Firebase Authentication`
*   **任務**:
    1.  **專案設定**:
        *   建立 `Ktor` 專案，並設定 `PostgreSQL` 與 `Exposed` 連線。
        *   配置 `Firebase Admin SDK`，用於驗證 `ID Token`。
    2.  **核心 API**:
        *   實作使用者、寵物、餵食紀錄的 `CRUD` 端點。
        *   建立一個獨立的 `db/init.sql` 腳本，用於定義資料庫結構。
    3.  **部署與測試**:
        *   設定 `Docker` 與 `docker-compose`，方便本地開發與部署。
        *   使用 `Postman` 或 `curl` 進行 `API` 測試。

### Phase 2: 核心功能 (Android App)

*   **目標**: 使用者能在 App 中完成個人資料設定，並記錄與查看餵食歷史。
*   **任務**:
    1.  **API**: 後端 API 功能已在 Phase 1 完成。
    2.  **Android App**:
        *   **專案設定**: 初始化 `Android` 專案，整合 `Hilt`, `Retrofit`, `Coil` 等核心函式庫。
        *   **個人資料設定**: 在設定畫面中實作個人資料（名稱）的編輯功能 (`US-01`)。
        *   **主畫面**: 實作主畫面，用於顯示最近的餵食紀錄 (`US-05`)。
        *   **記錄功能**: 實作基礎的餵食記錄功能。
        *   **寵物管理功能**: 實作寵物的新增、刪除與管理者設定功能 (`US-09`, `US-10`)。
### Phase 3: 進階功能 (API & App)

*   **目標**: 擴充 App 功能，加入體重追蹤、相簿與提醒功能。
*   **任務**:
    1.  **API**:
        *   實作體重紀錄的 `API` (`POST/GET /pets/{petId}/weights`)。
        *   實作相簿 `API` (`GET /albums/photos`)。
        *   建立排程任務或使用 `FCM`，用於檢查是否「錯過餵食時間」，並觸發推播通知 (`US-06`)。
    2.  **Android App**:
        *   實作相機流程，讓使用者在餵食時能拍照上傳 (`US-08`)。
        *   實作體重歷史與趨勢圖的 `UI` (`US-07`)。
        *   實作貓咪專屬相簿的 `UI` (`US-08`)。

### Phase 4: 系統整合與優化 (App)

*   **目標**: 讓 App 功能更完整、易用，並進行效能優化。
*   **任務**:
    1.  **Android App**:
        *   實作「餵正餐 / 餵點心」的桌面小工具 (`App Widget`) (`US-02`)。
        *   實作長按圖示的 `App Shortcuts` (`US-03`)。
        *   整合 `Firebase Cloud Messaging`，接收並顯示來自後端的推播通知。
        *   根據 `UI/UX` 設計進行最後的介面調整與使用者體驗優化。

### Phase 5: 部署與測試

*   **目標**: 將應用程式正式上線，並進行全面的端對端測試。
*   **任務**:
    1.  **API**: 將 `Ktor` 後端部署到正式環境 (例如：`Google Cloud Run`, `Heroku`, 或一台 `VPS`)。
    2.  **Android App**: 將 `App Bundle` 上傳到 `Google Play Store` 進行內部測試或公開發布。
    3.  **測試**: 招募一小群使用者進行 `Beta` 測試，收集回饋並修正問題。
