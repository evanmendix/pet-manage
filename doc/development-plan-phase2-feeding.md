# 開發計畫：Phase 2 - 餵食紀錄功能實作

本文件記錄 Phase 2 核心功能「餵食紀錄」的後端 API 開發任務。此計畫接續 `development-plan.md` 的規劃，並根據 `user-stories.md` (特別是 US-04) 的具體需求進行細化。

## 開發目標

完成 `development-plan.md` 中 Phase 2 所定義的餵食紀錄後端功能，補齊目前已實作版本所缺少的部分。

## 任務分解

### 1. 實作「重複餵食」的防呆機制

*   **需求來源**: `US-04` - 使用者希望在記錄「正餐」時，若短時間內已有餵食紀錄，系統應給予提示。
*   **實作細節**:
    *   **目標檔案**: `backend/src/main/kotlin/com/example/features/feeding/FeedingService.kt`
    *   **邏輯**:
        1.  在 `addFeeding` 函式中，針對 `type` 為 `"meal"` 的請求進行檢查。
        2.  查詢資料庫，確認在過去 **4 小時** 內，是否已存在 `type` 為 `"meal"` 的紀錄。
        3.  若存在，則拋出一個 `DuplicateFeedingException` 例外。
    *   **API 回應**:
        *   **目標檔案**: `backend/src/main/kotlin/com/example/features/feeding/FeedingController.kt`
        *   `POST /feedings` 端點需捕捉此例外，並回傳 `HTTP 409 Conflict` 狀態碼，告知客戶端此為重複操作。

### 2. 實作「取得當前狀態」的 API 端點

*   **需求來源**: `development-plan.md`
*   **實作細節**:
    *   **目標**: 建立 `GET /status/current` 端點。
    *   **`FeedingService.kt`**: 新增 `getCurrentStatus()` 函式，用於從資料庫取得最新的一筆餵食紀錄。
    *   **`FeedingController.kt`**: 新增對應的路由，處理 `GET` 請求並回傳最新狀態。

## 待辦事項

- [ ] 在 `FeedingService.kt` 中加入重複餵食檢查邏輯。
- [ ] 在 `FeedingController.kt` 中加入 `HTTP 409` 錯誤處理。
- [ ] 在 `FeedingService.kt` 中新增 `getCurrentStatus` 函式。
- [ ] 在 `FeedingController.kt` 中新增 `GET /status/current` 路由。
- [ ] 透過程式碼審查 (Code Review) 驗證所有變更。
