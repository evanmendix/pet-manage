# 部署與環境設定指南

本文件提供將專案部署至本地開發或正式環境的詳細步驟與設定說明。

## 1. 系統架構概覽

本專案採用容器化部署模型，核心組件如下：
- **後端服務**: 一個 `Ktor` 應用程式，負責處理所有業務邏輯與 API。
- **資料庫**: 一個 `PostgreSQL` 資料庫，用於儲存所有結構化資料。
- **容器化**: 使用 `Docker` 與 `Docker Compose` 來封裝及管理後端服務與資料庫。
- **外部存取**: 正式環境使用 `Cloudflare Tunnel` 將本地執行的服務安全地暴露於公網。

---

## 2. 環境設定

### 2.1. 前置條件
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) for Windows

### 2.2. 圖片儲存目錄
在您的 Windows 主機上，建立一個用於存放所有上傳圖片的資料夾。
- **路徑**: `C:\pet-manage`
- **說明**: 此路徑已在 `docker-compose.yml` 中設定為掛載點。所有由後端服務儲存的圖片，都會被實際保存在這個目錄下。

### 2.3. 環境變數
本專案使用環境變數來設定關鍵路徑與機敏資訊。

-   **`IMAGE_STORAGE_PATH`**:
    -   **用途**: 指定圖片儲存的根目錄。
    -   **Docker 環境**: 在 `docker-compose.yml` 中被設定為 `/storage`，對應到容器內的掛載點。
    -   **本地開發 (IntelliJ)**: 後端服務會讀取此變數以決定在本機的儲存位置。請見下方「方法二」的設定說明。
-   **其他變數**: 資料庫連線資訊等，皆在 `docker-compose.yml` 中設定。

---

## 3. 運行應用程式

### 方法一：使用 Docker Compose (生產/測試環境)
此方法會同時啟動後端服務與資料庫，最適合進行端對端測試或模擬生產環境。

1.  **啟動服務**: 在專案根目錄下，執行以下指令：
    ```bash
    docker-compose up --build -d
    ```
2.  **服務端口**:
    - **後端 API**: `http://localhost:5070`
    - **PostgreSQL 資料庫**: `localhost:5071` (可使用資料庫工具連接)

### 方法二：單獨運行後端 (本地開發)
此方法讓您直接透過 IntelliJ 或終端機運行 Ktor 服務，方便進行快速開發與偵錯。

#### 步驟 1: 設定 IntelliJ 執行環境 (建議)
直接在 IntelliJ 中設定環境變數是最可靠的方法。
1.  在 IntelliJ 右上角，點擊 `Application` (或您目前的執行設定)，然後選擇 `Edit Configurations...`。
2.  在「Run/Debug Configurations」視窗中，找到 `Environment variables` 欄位，點擊右側的圖示。
3.  在彈出的視窗中，點擊 `+` (Add)，然後輸入：
    -   **Name**: `IMAGE_STORAGE_PATH`
    -   **Value**: `C:\pet-manage`
4.  點擊 `OK` 儲存設定。現在，當您從 IntelliJ 執行後端時，它將會把圖片儲存在 `C:\pet-manage` 目錄下。

#### 步驟 2: 啟動服務
設定好環境變數後，您可以直接點擊 IntelliJ 的「執行」(Run) 按鈕，或在終端機中執行以下指令：
```bash
./gradlew run
```

#### 備用方法：使用 PowerShell 腳本
如果您偏好使用終端機，我們也提供了 PowerShell 腳本來設定系統層級的環境變數。
- **設定變數**: 以系統管理員身分開啟 PowerShell，並執行 `.\set-dev-env.ps1`。
- **移除變數**: 執行 `.\remove-dev-env.ps1`。
- **注意**: 使用此方法後，您 **必須重新啟動** IntelliJ 或您的終端機，變更才會生效。
---

## 4. 連接 Android 模擬器
若要在 Android 模擬器上測試 App，您需要使用特殊的 IP 位址來連接到在您的電腦 (宿主機) 上運行的後端服務。

- **API 位址**: `http://10.0.2.2:5070/api/v1/`

---

## 5. 圖片儲存目錄結構 (參考)
所有上傳的圖片將會儲存在掛載的 `C:\pet-manage` 目錄下，並遵循以下結構。後端應用程式將會從容器內的 `/storage` 路徑來存取這些檔案。

- **使用者大頭照**: `/storage/users/profile/{userId}.jpg`
- **寵物大頭照**: `/storage/pets/{petId}/profile/profile.jpg`
- **餵食紀錄照片**: `/storage/pets/{petId}/feedings/{yyyy-MM-dd}/{timestamp}.jpg`
- **寵物相簿**: `/storage/pets/{petId}/albums/{albumName}/{photoName}.jpg`