# 系統設計 (Android App)

## App 架構

*   **語言 (Language)**: `Kotlin`
*   **架構模式 (Pattern)**: **MVVM (Model-View-ViewModel)**，基於 Google 官方推薦的應用程式架構指南。
*   **核心函式庫 (Key Libraries)**:
    *   **非同步處理**: `Kotlin Coroutines` 與 `Flow`，用於管理背景任務和響應式地更新 UI。
    *   **依賴注入**: `Hilt`，用於管理整個 App 的依賴關係。
    *   **網路請求**: `Retrofit` 與 `OkHttp`，用於和後端 Ktor API 進行通訊。
    *   **JSON 解析**: `kotlinx.serialization` 或 `Moshi`。
    *   **圖片載入**: `Coil`，一個現代化的圖片載入函式庫，對 Coroutines 支援良好。
    *   **認證 (Authentication)**: `Firebase Authentication` 與 `Google Sign-In`，提供完整的使用者身份管理。
*   **網絡配置**: 連接後端 API `http://10.0.2.2:5070/api/v1/`（模擬器專用）

## 認證流程 (Authentication Flow)

App 採用 Google SSO 登入，提供完整的使用者體驗。

1.  **Google SSO 登入**:
    *   使用者點擊「使用 Google 登入」按鈕
    *   `SignInScreen` 使用正確的 Web Client ID（從 `google-services.json` 自動讀取）
    *   成功後取得 Google ID Token 並透過 `AuthRepository.signInWithGoogle()` 登入 Firebase
    *   **重要設定**：套件名稱 `com.supercatdev.catfeeder`，Firebase 專案 `pet-manage-wu`

2.  **使用者狀態管理**:
    *   `MainViewModel` 監聽 Firebase 使用者狀態變化
    *   首次登入時檢查使用者是否存在（`UserRepository.checkUserExists()`）
    *   若不存在則顯示 `CreateProfileScreen`，預填 Google 顯示名稱
    *   使用者可修改名稱後建立個人資料（`POST /users`）

3.  **自動附加 Token**:
    *   所有對後端的 API 請求都會經過一個 `AuthInterceptor` (OkHttp Interceptor)
    *   此攔截器會從 `AuthRepository` 取得當前使用者的 ID Token
    *   然後，它會自動將 `Authorization: Bearer <ID_TOKEN>` 樜頭加入到每一個請求中

4.  **元件**:
    *   `AuthRepository`: 封裝所有與 `FirebaseAuth` 的互動，提供 `signInWithGoogle`、`getIdToken` 等方法
    *   `UserRepository`: 管理使用者資料的 CRUD 操作，與後端 PostgreSQL 互動
    *   `AuthInterceptor`: 負責將 Token 注入到網路請求中
    *   `MainViewModel`: 管理認證狀態（`AuthState.NewUser`, `AuthState.Authenticated` 等）
    *   `SettingsViewModel`: 管理個人資料編輯功能

## Android 系統整合

*   **App Widgets**:
    *   **設計**: 一個簡潔的 2x1 或 2x2 小工具，包含「餵正餐」和「餵點心」按鈕。
    *   **行為**: 點擊按鈕會觸發一個無畫面的 `Activity`，直接呼叫後端 `API` 逝入餵食流程。
*   **App Shortcuts**:
    *   **設計**: 長按 `App` 圖示時出現「餵正餐」、「餵點心」的靜態捷徑。
    *   **行為**: 與 `Widget` 類似，點擊後直接進入對應的餵食流程。
