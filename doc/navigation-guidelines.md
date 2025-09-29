# 導覽規範（Navigation Guidelines)

本文件描述 App 的導覽規範、底部導覽列（Bottom Navigation）設計，以及首頁/寵物管理/歷史紀錄之間的互動行為。

## 底部導覽列（Bottom Navigation）

- **項目**：
  - Home（首頁，`Screen.Feeding`）
  - History（歷史紀錄，`Screen.History`）
- **圖示與標籤**：
  - 僅顯示圖示（Icons），不顯示文字標籤（Labels）。
  - 實作位置：`android/app/src/main/java/com/supercatdev/catfeeder/MainActivity.kt` 中 `NavigationBarItem` 的 `label = null`。

## 首頁（Feeding）與狀態重置

- **首頁起始狀態**：
  - 由 `FeedingViewModel` 載入 `managedPets` 與 `currentStatus`，若只有一隻受管寵物則直接選中。
- **從寵物管理返回首頁**：
  - 當使用者目前位於「寵物管理」頁（`Screen.PetManagement`）時，點擊底部導覽列的「首頁」圖示，會將首頁重置為起始狀態（不復原先前的狀態）。
  - 實作方式：
    - `MainActivity.kt` 內針對 `Screen.Feeding.route` 的 `onClick` 使用：
      - `popUpTo(navController.graph.findStartDestination().id)` 並 `inclusive = true`、`saveState = false`
      - `launchSingleTop = true`、`restoreState = false`
  - 目的：確保從「寵物管理」回到首頁時，不會殘留上一個首頁堆疊或狀態，使用者感知為「回到首頁初始畫面」。

## 歷史紀錄（History）

- **狀態保存**：
  - 在底部導覽切換到 History 時，保留既有瀏覽狀態（預設 `saveState = true`/`restoreState = true`）。
  - 實作位置：`MainActivity.kt`。

## 寵物管理（Pet Management）

- **進入方式**：
  - 由首頁右上角的寵物圖示（`TopAppBar` Action）進入。
- **返回首頁**：
  - 兩種方式：
    1. 使用 Android 系統返回鍵。
    2. 點底部導覽列「首頁」圖示，直接回到首頁初始狀態（如上節所述）。

## 設定（Settings）

- **進入方式**：
  - 由首頁或歷史紀錄頁右上角的設定圖示（`TopAppBar` Action）進入。
- **編輯個人資料**：
  - 在設定畫面的「帳號設定」區塊，點擊「編輯」圖示，即可導航至 `EditProfileScreen`。
- **返回設定畫面**：
  - 在 `EditProfileScreen` 中，可以透過 `TopAppBar` 的返回箭頭或系統返回鍵，返回到設定畫面。
  - 成功儲存變更後，也會自動返回設定畫面。

## 國際化（i18n）

- 文字資源放置於 `android/app/src/main/res/values/strings.xml`。
- 底部導覽目前不顯示文字標籤，未來如需開啟，請確保文字資源符合本專案的本地化規範（繁體中文、台灣用語）。

## 相關程式碼位置

- `MainActivity.kt`：底部導覽列與 NavHost 的整合、首頁重置行為、設定與編輯畫面的導航。
- `ui/navigation/Screen.kt`: 定義所有畫面的路由。
- `ui/feeding/FeedingViewModel.kt`：首頁資料載入與狀態管理。
- `ui/history/HistoryViewModel.kt`：歷史紀錄資料載入與狀態管理。
- `ui/settings/SettingsViewModel.kt`: 設定畫面的資料載入。
- `ui/settings/EditProfileViewModel.kt`: 編輯個人資料畫面的邏輯處理。
- `ui/feeding/FeedingScreen.kt`、`ui/history/HistoryScreen.kt`、`ui/pet_management/PetManagementScreen.kt`、`ui/settings/SettingsScreen.kt`、`ui/settings/EditProfileScreen.kt`：對應頁面 UI。
