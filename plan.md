# **貓咪智慧餵食管理系統 \- 綜合設計文件**

## **1\. 專案概述**

### **1.1 背景**

在多位家庭成員共同飼養寵物的環境中，時常發生「資訊不對稱」的問題，例如不確定寵物是否已被餵食，導致重複餵食或遺漏餵食。本專案旨在開發一套包含行動應用程式與後端服務的完整系統，透過簡單直覺的操作，同步家庭成員間的餵食資訊，並提供智慧提醒與紀錄查詢功能，以確保寵物定時定量飲食，促進其健康。

### **1.2 目標**

* **主要目標**: 解決家庭成員間寵物餵食資訊不同步的問題。  
* **次要目標**: 建立寵物飲食日誌，提供包含體重追蹤、事件記錄與專屬相簿的完整健康與生活履歷。  
* **技術目標**: 建立一個穩定、安全且可擴展的後端服務，並透過 Cloudflare Tunnel 對外提供 API，供 Android 及未來的 iOS 客戶端使用。

## **2\. 系統架構 (System Architecture)**

本系統採前後端分離架構，客戶端 (Android App) 透過一個獨立的後端 API 服務與核心數據庫進行溝通。

### **2.1 架構圖**

\[Android App\] \<---\> \[網際網路\] \<---\> \[Cloudflare Tunnel\] \<---\> \[後端 API 服務\] \<---\> \[Firebase 服務\]  
                                                               (Node.js/Express)     (Firestore, Storage, Auth)

### **2.2 組件說明**

* **客戶端 (Client)**:  
  * 初期為原生 Android App。  
  * 負責所有使用者介面與互動。  
  * 透過呼叫後端 API 來收發數據。  
  * 圖片等大型檔案可直接上傳至 Firebase Cloud Storage 以提升效率。  
* **網路通道 (Network Tunnel)**:  
  * **Cloudflare Tunnel**: 負責建立一個從後端伺服器到 Cloudflare 網路的安全連線。這使得在家中或任何地方運行的伺服器，都能安全地對外提供服務，無需公有 IP 或複雜的防火牆設定。  
* **後端 API 服務 (Backend API Service)**:  
  * **技術棧**: 建議使用 **Node.js \+ Express.js** 框架，搭配 **Firebase Admin SDK**。  
  * **核心職責**:  
    * 處理所有業務邏輯 (例如：檢查是否重複餵食、產生異常報告)。  
    * 提供 RESTful API 端點供客戶端呼叫。  
    * 驗證來自客戶端的請求（例如：驗證使用者身份）。  
    * 執行定時任務（例如：檢查是否忘記餵食並發送通知）。  
* **Firebase 服務 (Backend as a Service \- BaaS)**:  
  * **Firestore**: 作為核心資料庫，儲存所有結構化數據 (餵食紀錄、使用者資料等)。  
  * **Cloud Storage**: 儲存使用者上傳的檔案，如大頭貼與餵食照片。  
  * **Authentication**: 用於管理使用者身份驗證。

## **3\. 系統分析 (System Analysis \- SA)**

### **3.1 系統參與者 (Actors)**

* **家庭成員 (User)**:  
  * 描述: 任何一位共同照顧貓咪的家庭成員。  
  * 職責:  
    * 設定個人名稱與大頭貼。  
    * 透過 App、桌面小工具 (Widget) 或 App 快捷選單 (Shortcut) 記錄餵食行為。  
    * 拍攝並上傳餵食照片。  
    * 查詢歷史餵食紀錄、寵物相簿與異常報告。  
    * 記錄寵物體重與其他重要事件。  
    * 接收系統發出的提醒通知。

### **3.2 功能範圍 (Scope)**

* **App 端**:  
  * 使用者身份設定 (名稱、大頭貼)。  
  * 快捷餵食流程 (Widget, Shortcuts)。  
  * 完整餵食流程 (狀態檢查、拍照、上傳)。  
  * 健康中心 (體重紀錄與圖表、事件日誌)。  
  * 回憶中心 (寵物相簿、寵物檔案)。  
  * 歷史紀錄與異常報告顯示。  
* **後端 API**:  
  * 提供所有 App 功能所需的數據接口。  
  * 使用者身份驗證與授權。  
  * 圖片上傳授權。  
  * 定時任務與推播通知服務。

### **3.3 使用案例 (Use Cases)**

* **記錄餵食**:  
  * 參與者: 家庭成員  
  * 進入點: App主畫面按鈕、App Widget、App Shortcut。  
  * 流程: 包含選擇正餐/點心、(條件式)狀態檢查、拍照、上傳。  
* **查詢餵食紀錄**:  
  * 參與者: 家庭成員  
  * 流程: 查看最近紀錄列表與異常報告。  
* **接收未餵食提醒**:  
  * 參與者: 家庭成員  
  * 觸發: 系統後端於特定時間自動觸發。

## **4\. 系統設計 (System Design \- SD)**

### **4.1 後端 API 設計**

* **端點 (Endpoints) 規劃 (以 /api/v1 為前綴)**:  
  * POST /users: 建立新使用者。  
  * PUT /users/{userId}: 更新使用者名稱與大頭貼 URL。  
  * POST /feedings: 新增一筆餵食紀錄。  
  * GET /feedings?limit=30: 取得最近的餵食紀錄。  
  * GET /status/current: 取得當前早餐/晚餐的餵食狀態。  
  * POST /pets/{petId}/weights: 新增一筆體重紀錄。  
  * GET /pets/{petId}/weights: 取得歷史體重紀錄。  
  * GET /albums/photos: 取得相簿的所有照片 URL。  
* **安全性**:  
  * 所有 API 請求的 Header 中必須包含由 Firebase Authentication 簽發的 Authorization: Bearer \<ID\_TOKEN\>。  
  * 後端 API 服務會使用 Firebase Admin SDK 驗證此 Token 的有效性，確保只有登入的家庭成員可以存取數據。

### **4.2 App 架構**

* **架構模式**: **MVVM (Model-View-ViewModel)**  
* **數據流**: View (Activity/Fragment) \-\> ViewModel \-\> Repository \-\> Retrofit Client \-\> **後端 API 服務**。  
* **關鍵模組**:  
  * ui: 介面層，包含所有 Activity, Fragment, WidgetProvider。  
  * data:  
    * repository: 封裝所有數據操作，決定是呼叫 API 還是直接操作 Firebase (如檔案上傳)。  
    * remote: 包含 Retrofit 的 API interface 定義與 client 實作。  
    * model: 定義 App 內使用的數據類別。  
  * util: 工具層，包含 Shortcut 管理、圖片處理等。

### **4.3 資料庫設計 (Firestore Schema)**

* **Collection: families**  
  * **Document: {familyId}**  
    * **Collection: users**  
      * **Document: {userId}** (來自 Firebase Auth 的 UID)  
        * name: String  
        * profilePictureUrl: String (指向 Cloud Storage 的 URL)  
        * fcmToken: String (用於推播的裝置 Token)  
    * **Collection: pets**  
      * **Document: {petId}**  
        * name: String  
        * birthdate: Timestamp  
        * profileImageUrl: String  
        * **Collection: feedings**  
          * **Document: {feedingId}**  
            * feederId: String (關聯到 users 的 {userId})  
            * feederName: String (冗餘欄位，方便查詢)  
            * feederProfilePictureUrl: String (冗餘欄位，方便查詢)  
            * feedType: String ("meal" / "snack")  
            * timestamp: Timestamp  
            * photoUrl: String  
        * **Collection: weights**  
          * **Document: {weightId}**  
            * timestamp: Timestamp  
            * weightInKg: Number

### **4.4 Android 系統整合**

* **App Widgets**:  
  * **設計**: 一個簡潔的 2x1 或 2x2 小工具，包含「餵正餐」和「餵點心」按鈕。  
  * **行為**: 點擊按鈕會觸發一個無畫面的 Activity，直接呼叫後端 API 進入餵食流程。  
* **App Shortcuts**:  
  * **設計**: 長按 App 圖示時出現「餵正餐」、「餵點心」的靜態捷徑。  
  * **行為**: 與 Widget 類似，點擊後直接進入對應的餵食流程。

## **5\. UI/UX 流程 (UI/UX Flow)**

* 首次啟動與設定:  
  App 啟動 \-\> Firebase 匿名登入 \-\> 檢查是否已設定個人資料 \-\> (否) \-\> 進入設定畫面 \-\> 使用者輸入名稱並上傳大頭貼 \-\> 呼叫 API 儲存 \-\> 進入主畫面  
* 快捷餵食流程 (Widget/Shortcut):  
  點擊桌面捷徑 \[餵正餐\] \-\> 啟動無畫面 Activity \-\> 呼叫 API 檢查狀態 \-\> (未餵食) \-\> 直接啟動相機...  
* 查看餵食紀錄:  
  主畫面 \-\> 進入歷史紀錄頁 \-\> App 呼叫 GET /feedings \-\> API 返回數據 \-\> UI 顯示列表，包含餵食者的大頭貼與名稱。

## **6\. 使用者故事 (User Stories)**

| 編號 | 角色 | 我想要 (I want to...) | 以便於 (so that...) |
| :---- | :---- | :---- | :---- |
| **US-01** | 家庭成員 | 首次使用 App 時設定我的名字與大頭貼 | 之後的餵食紀錄能清楚地展示是誰餵的 |
| **US-02** | 家庭成員 | 將一個「餵食」小工具放在手機桌面上 | 不用打開整個 App 就能直接點擊按鈕開始記錄 |
| **US-03** | 家庭成員 | 長按 App 圖示時，能直接選擇要餵「正餐」還是「點心」 | 我能用最快的路徑啟動最常用的功能 |
| **US-04** | 家庭成員 | 在記錄「正餐」時，如果已經有人餵過，系統能跳出警告 | 避免重複餵食，防止貓咪過胖 |
| **US-05** | 家庭成員 | 隨時查看最近的餵食紀錄，包含是誰餵的、照片和時間 | 快速了解貓咪近期的飲食狀況是否規律 |
| **US-06** | 家庭成員 | 如果到了該吃飯的時間還沒有人餵貓，我能收到手機通知 | 確保貓咪不會餓肚子 |
| **US-07** | 家庭成員 | 記錄並查看貓咪的歷史體重與趨勢圖 | 監控牠的健康狀況，並在看獸醫時提供數據參考 |
| **US-08** | 家庭成員 | 擁有一個自動整理所有餵食照片的專屬相簿 | 回味貓咪的可愛瞬間，就像牠的成長日記 |

