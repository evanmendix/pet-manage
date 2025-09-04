# 系統設計 (Android App)

## App 架構

*   **語言 (Language)**: `Kotlin`
*   **架構模式 (Pattern)**: **MVVM (Model-View-ViewModel)**，基於 Google 官方推薦的應用程式架構指南。
*   **核心函式庫 (Key Libraries)**:
    *   **UI**: `Jetpack Compose` (建議用於新畫面) 或 `Android Views` 搭配 `View Binding`。
    *   **非同步處理**: `Kotlin Coroutines` 與 `Flow`，用於管理背景任務和響應式地更新 UI。
    *   **依賴注入**: `Hilt`，用於管理整個 App 的依賴關係。
    *   **網路請求**: `Retrofit` 與 `OkHttp`，用於和後端 Ktor API 進行通訊。
    *   **JSON 解析**: `kotlinx.serialization` 或 `Moshi`。
    *   **圖片載入**: `Coil`，一個現代化的圖片載入函式庫，對 Coroutines 支援良好。
    *   **ViewModel**: `Jetpack ViewModel`，用於儲存和管理與 UI 相關的資料。

## Android 系統整合

*   **App Widgets**:
    *   **設計**: 一個簡潔的 2x1 或 2x2 小工具，包含「餵正餐」和「餵點心」按鈕。
    *   **行為**: 點擊按鈕會觸發一個無畫面的 `Activity`，直接呼叫後端 `API` 進入餵食流程。
*   **App Shortcuts**:
    *   **設計**: 長按 `App` 圖示時出現「餵正餐」、「餵點心」的靜態捷徑。
    *   **行為**: 與 `Widget` 類似，點擊後直接進入對應的餵食流程。
