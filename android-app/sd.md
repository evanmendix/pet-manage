# 系統設計 (Android App)

## App 架構

*   **架構模式**: **MVVM (Model-View-ViewModel)**
*   **數據流**: `View` (`Activity`/`Fragment`) -> `ViewModel` -> `Repository` -> `Retrofit Client` -> **後端 API 服務**。
*   **關鍵模組**:
    *   `ui`: 介面層，包含所有 `Activity`, `Fragment`, `WidgetProvider`。
    *   `data`:
        *   `repository`: 封裝所有數據操作，決定是呼叫 `API` 還是直接操作 `Firebase` (如檔案上傳)。
        *   `remote`: 包含 `Retrofit` 的 `API interface` 定義與 `client` 實作。
        *   `model`: 定義 `App` 內使用的數據類別。
    *   `util`: 工具層，包含 `Shortcut` 管理、圖片處理等。

## Android 系統整合

*   **App Widgets**:
    *   **設計**: 一個簡潔的 2x1 或 2x2 小工具，包含「餵正餐」和「餵點心」按鈕。
    *   **行為**: 點擊按鈕會觸發一個無畫面的 `Activity`，直接呼叫後端 `API` 進入餵食流程。
*   **App Shortcuts**:
    *   **設計**: 長按 `App` 圖示時出現「餵正餐」、「餵點心」的靜態捷徑。
    *   **行為**: 與 `Widget` 類似，點擊後直接進入對應的餵食流程。
