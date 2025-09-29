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

1.  **設定儲存路徑 (首次執行)**:
    -   為了讓後端服務知道在哪裡儲存圖片，您需要先設定 `IMAGE_STORAGE_PATH` 環境變數。
    -   **開啟 PowerShell (以系統管理員身分)**，並在專案根目錄下執行腳本：
        ```powershell
        .\set-dev-env.ps1
        ```
    -   此腳本會為您的 Windows 帳戶建立一個指向 `C:\pet-manage` 的永久環境變數。
    -   **重要**: 設定完畢後，請 **重新啟動** 您的 IntelliJ 或終端機，以確保新的環境變數生效。

2.  **啟動服務**: 在專案根目錄下，執行以下指令：
    ```bash
    ./gradlew run
    ```

3.  **移除環境變數 (可選)**: 如果您未來想清理開發環境，可以執行以下腳本來移除環境變數：
    ```powershell
    .\remove-dev-env.ps1
    ```
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