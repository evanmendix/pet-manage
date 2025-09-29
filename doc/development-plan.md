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

*   **目標**: 使用者能在 App 中完成個人資料設定與寵物管理。
*   **已完成任務**:
    1.  **後端 API**: 使用者管理與寵物管理 API 已完成。
    2.  **Android App**:
        *   **專案設定**: 初始化 `Android` 專案，整合 `Hilt`, `Retrofit`, `Compose` 等核心函式庫。
        *   **身份驗證**: 實作 Firebase 匯名登入功能。
        *   **個人資料設定**: 在設定畫面中實作個人資料（名稱）的編輯功能 (`US-01`)。
        *   **主畫面**: 實作主畫面，用於顯示最近的餵食紀錄 (`US-05`)。
        *   **記錄功能**: 實作基礎的餵食記錄功能。
        *   **寵物管理功能**: 實作寵物的新增、刪除與管理者設定功能 (`US-09`, `US-10`)。
        *   **網絡配置**: 統一後端端口為 5070，確保開發與部署環境一致。

### Phase 3: 餵食記錄功能 - ✅ 完全完成

*   **目標**: 實作核心的餵食記錄與歷史查看功能。
*   **已完成任務**:
    1.  **後端 API**:
        *   ✅ 實作餵食記錄的 `API` (`POST/GET /feedings`)
        *   ✅ 實作餵食狀態查詢 (`GET /feedings/status/current`)
        *   ✅ 實作覆蓋上一餐功能 (`POST /feedings/overwrite`)
    2.  **Android App**:
        *   ✅ 實作主畫面餵食按鈕與狀態顯示（`FeedingScreen`）
        *   ✅ 實作重複餵食確認機制
        *   ✅ 實作餵食歷史記錄畫面（`HistoryScreen` + `HistoryViewModel`）
        *   ✅ 實作導航系統（Home/History 按鈕切換）

### Phase 4: 進階功能 (未來計畫)

*   **目標**: 擴充 App 功能，加入體重追蹤、相簿與提醒功能。
*   **計畫任務**:
    1.  **API**:
        *   體重紀錄 API (`POST/GET /pets/{petId}/weights`)
        *   相簿 API (`GET /albums/photos`)
        *   FCM 推播通知功能
    2.  **Android App**:
        *   相機拍照上傳流程
        *   體重歷史與趨勢圖 UI
        *   寵物專屬相簿 UI

### Phase 5: 系統整合與優化

*   **目標**: 讓 App 功能更完整、易用，並進行效能優化。
*   **計畫任務**:
    1.  **Android App**:
        *   實作「餵正餐 / 餵點心」的桌面小工具 (`App Widget`)
        *   實作長按圖示的 `App Shortcuts`
        *   整合 `Firebase Cloud Messaging`，接收並顯示來自後端的推播通知
        *   根據 `UI/UX` 設計進行最後的介面調整與使用者體驗優化

### Phase 6: 部署與測試

*   **目標**: 將應用程式正式上線，並進行全面的端對端測試。
*   **計畫任務**:
    1.  **後端部署**: 將 `Ktor` 後端部署到正式環境（使用現有的 Docker 配置）
    2.  **Android 發布**: 將 `App Bundle` 上傳到 `Google Play Store` 進行內部測試或公開發布
    3.  **Beta 測試**: 招募使用者進行測試，收集回饋並修正問題
