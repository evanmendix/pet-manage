# 系統分析 (API)

## 組件說明

*   **網路通道 (Network Tunnel)**:
    *   **Cloudflare Tunnel**: 負責建立一個從後端伺服器到 `Cloudflare` 網路的安全連線。這使得在家中或任何地方運行的伺服器，都能安全地對外提供服務，無需公有 `IP` 或複雜的防火牆設定。
*   **後端 API 服務 (Backend API Service)**:
    *   **技術棧**: 建議使用 **Node.js + Express.js** 框架，搭配 **Firebase Admin SDK**。
    *   **核心職責**:
        *   處理所有業務邏輯 (例如：檢查是否重複餵食、產生異常報告)。
        *   提供 `RESTful API` 端點供客戶端呼叫。
        *   驗證來自客戶端的請求（例如：驗證使用者身份）。
        *   執行定時任務（例如：檢查是否忘記餵食並發送通知）。
*   **Firebase 服務 (Backend as a Service - BaaS)**:
    *   **Firestore**: 作為核心資料庫，儲存所有結構化數據 (餵食紀錄、使用者資料等)。
    *   **Cloud Storage**: 儲存使用者上傳的檔案，如大頭貼與餵食照片。
    *   **Authentication**: 用於管理使用者身份驗證。

## 功能範圍

*   **後端 API**:
    *   提供所有 App 功能所需的數據接口。
    *   使用者身份驗證與授權。
    *   圖片上傳授權。
    *   定時任務與推播通知服務。
