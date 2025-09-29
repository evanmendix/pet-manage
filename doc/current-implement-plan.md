# 當前實作狀態

## 已完成功能

### 後端 API (Ktor + PostgreSQL)
- ✅ 基礎專案架構設定（統一使用 5070 端口）
- ✅ PostgreSQL 資料庫連接與 Exposed 整合
- ✅ Firebase Authentication 整合
- ✅ 使用者管理 API 端點:
    - `POST /api/v1/users` - 建立使用者
    - `GET /api/v1/users/{userId}` - 取得使用者資料
    - `PUT /api/v1/users/{userId}` - 更新使用者資料
    - `POST /api/v1/users/batch` - 批量取得使用者資料
- ✅ 寵物管理 API 端點:
    - `GET /api/v1/pets` - 取得寵物列表
    - `POST /api/v1/pets` - 新增寵物
    - `DELETE /api/v1/pets/{petId}` - 刪除寵物
    - `POST /api/v1/pets/{petId}/managers` - 新增寵物管理者
    - `DELETE /api/v1/pets/{petId}/managers` - 移除寵物管理者
- ✅ 餵食記錄 API 端點:
    - `GET /api/v1/feedings?petId={petId}` - 取得餵食記錄
    - `POST /api/v1/feedings` - 新增餵食記錄
    - `POST /api/v1/feedings/overwrite` - 覆蓋上一餐記錄
    - `GET /api/v1/feedings/status/current?petId={petId}` - 取得當前餵食狀態

### Android 應用
- ✅ 基礎專案架構（Hilt, Retrofit, Compose）
- ✅ Firebase Authentication 與 Google SSO 整合
- ✅ 使用者個人資料管理：
    - 首次登入自動預填 Google 顯示名稱
    - 個人資料建立畫面
    - 設定頁面名稱編輯功能
- ✅ 寵物管理畫面：
    - 寵物列表顯示
    - 新增/刪除寵物功能
    - 管理者設定切換
- ✅ 餵食功能畫面：
    - 主畫面餵食按鈕與狀態顯示
    - 餵食記錄功能
    - 重複餵食確認機制
    - 餵食歷史記錄畫面（History Screen）
- ✅ 全中文介面

## 技術架構決策

### 網絡配置
- **後端端口**: 統一使用 5070 端口（開發與部署環境一致）
- **Android 模擬器**: 使用 `10.0.2.2:5070` 連接宿主機
- **Docker 部署**: `5070:5070` 端口映射

### Firebase 角色定位
- **Authentication**: 使用者身份驗證與 ID Token 生成
- **Google SSO**: 整合 Google 登入，取得使用者基本資料
- **Future FCM**: 預留推播通知功能

### 資料庫架構
- **Exposed**: Kotlin ORM 函式庫
- **優勢**: 支援複雜關聯查詢、ACID 特性、成熟穩定

## 待實作功能

### 未來計畫
- ❌ 體重追蹤功能
- ❌ 相簿功能
- ❌ 推播通知功能
- ❌ App Widgets（桌面小工具）
- ❌ App Shortcuts（長按圖示快捷選單）

## 部署配置

### 開發環境
- **後端**: 直接運行 `./gradlew run` 或使用 Docker Compose
- **端口**: 5070
- **資料庫**: PostgreSQL (Docker: port 5071)
- **Android**: 模擬器連接 `10.0.2.2:5070`

### 生產環境
- **Docker**: 使用 `docker-compose.yml` 部署
- **端口映射**: `5070:5070`
- **環境變數**: 透過 Docker Compose 設定資料庫連接