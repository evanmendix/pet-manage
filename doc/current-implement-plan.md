# 當前實作狀態

## 已完成功能

### 後端 API (Ktor + PostgreSQL)
- ✅ 基礎專案架構設定
- ✅ PostgreSQL 資料庫連接與 Exposed 整合
- ✅ Firebase Authentication 整合
- ✅ **寵物管理 API (`/api/v1/pets`)**:
    - `GET /` - 取得所有寵物
    - `POST /` - 新增寵物
    - `DELETE /{petId}` - 刪除寵物
    - `POST /{petId}/managers` - 新增寵物管理者
    - `DELETE /{petId}/managers` - 移除寵物管理者
- ✅ **餵食記錄 API (`/api/v1/feedings`)**:
    - `GET ?petId={petId}` - 取得餵食記錄 (可選 `startTime`, `endTime` 過濾)
    - `POST /` - 新增餵食記錄
    - `POST /overwrite` - 覆蓋上一餐紀錄
    - `GET /status/current?petId={petId}` - 取得寵物當前狀態
- ✅ **使用者資料 API (`/api/v1/users`)**:
    - `POST /` - 建立使用者資料
    - `GET /{userId}` - 取得使用者資料
    - `PUT /{userId}` - 更新使用者資料
    - `POST /batch` - 批次取得多位使用者資料

### Android 應用
- ✅ 基礎專案架構（Hilt, Retrofit, Compose）
- ✅ Firebase Authentication 整合
- ✅ **主畫面**:
    - 顯示寵物目前餵食狀態
    - 快速新增餵食紀錄按鈕
- ✅ **寵物管理畫面**:
    - 寵物列表顯示
    - 新增/刪除寵物
    - 管理者設定
- ✅ **餵食歷史記錄畫面**:
    - 顯示歷史餵食列表
- ✅ **設定畫面**:
    - 使用者個人資料設定
- ✅ **通用功能**:
    - 重複餵食確認對話框（防呆機制）：若偵測到最近已餵過正餐，先提示使用者確認是否要重複餵食。

## 技術架構決策

### Firebase 角色定位
- **Authentication**: 使用者身份驗證與 ID Token 生成
- **Future FCM**: 預留推播通知功能
- **NOT USED**: Firestore（改用 PostgreSQL）

### 資料庫選擇
- **PostgreSQL**: 主要資料儲存
- **Exposed**: Kotlin ORM 函式庫
- **優勢**: 支援複雜關聯查詢、ACID 特性、成熟穩定

## 待實作功能

### Phase 3 功能
- ❌ 體重追蹤
- ❌ 相簿功能
- ❌ 推播通知
- ⚠️ **後端程式碼整理**: `features/pets` 路徑底下有舊的 `petRoutes` 未被使用，建議移除以避免混淆。

## 已解決的技術問題

1. **Firebase 認證整合**: 解決 401 認證錯誤，確保 Android 端正確附加 ID Token
2. **寵物管理邏輯**: 修復管理者已存在時的 404 錯誤
3. **UI 狀態同步**: 修復 toggle 顯示狀態與實際資料不一致問題