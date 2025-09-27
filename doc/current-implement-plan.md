# 當前實作狀態

## 已完成功能

### 後端 API (Ktor + PostgreSQL)
- ✅ 基礎專案架構設定
- ✅ PostgreSQL 資料庫連接與 Exposed 整合
- ✅ Firebase Authentication 整合
- ✅ 寵物管理 API 端點：
    - `POST /api/v1/pets` - 新增寵物
    - `DELETE /api/v1/pets/{petId}` - 刪除寵物
    - `POST /api/v1/pets/{petId}/managers` - 新增寵物管理者
    - `DELETE /api/v1/pets/{petId}/managers` - 移除寵物管理者

### Android 應用
- ✅ 基礎專案架構（Hilt, Retrofit, Compose）
- ✅ Firebase Authentication 整合
- ✅ 寵物管理畫面:
    - 寵物列表顯示
    - 新增寵物功能
    - 刪除寵物功能（長按）
    - 管理者設定切換
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

### Phase 2 剩餘任務
- ❌ 餵食記錄功能
- ❌ 主畫面餵食歷史顯示
- ❌ 首次啟動個人資料設定

### Phase 3 功能
- ❌ 體重追蹤
- ❌ 相簿功能
- ❌ 推播通知

## 已解決的技術問題

1. **Firebase 認證整合**: 解決 401 認證錯誤，確保 Android 端正確附加 ID Token
2. **寵物管理邏輯**: 修復管理者已存在時的 404 錯誤
3. **UI 狀態同步**: 修復 toggle 顯示狀態與實際資料不一致問題