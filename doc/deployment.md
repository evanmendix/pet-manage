# 部署指南 (Deployment Guide)

本文件提供將專案部署至本地或正式環境的詳細步驟，特別針對在 Windows 作業系統上使用 Docker 進行部署。

## 先決條件

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) for Windows

## 本地部署 (Local Deployment)

本地部署是透過 `docker-compose` 來啟動後端服務與資料庫，快速建立一個與正式環境一致的開發與測試環境。

### 步驟

1.  **設定圖片儲存目錄**:
    *   在您的 Windows 電腦上，建立一個用於存放上傳圖片的資料夾，路徑為 `C:\pet-manage`。
    *   **重要**: 此路徑已在 `docker-compose.yml` 中設定為掛載點 (`/c/pet-manage:/storage`)。所有由後端服務儲存的圖片，都會被保存在這個目錄下。

2.  **啟動服務**:
    *   在專案的根目錄下，開啟一個終端機 (例如 PowerShell 或 CMD)。
    *   執行以下指令來建置並啟動所有服務：
        ```bash
        docker-compose up --build -d
        ```
    *   `-d` 參數會讓容器在背景執行。
    *   **注意**: 根據您的 Docker 版本，您可能需要使用 `docker compose` (中間有空格) 而非 `docker-compose`。

3.  **驗證服務狀態**:
    *   執行 `docker-compose ps` 來確認 `pet_feeder_backend` 與 `pet_feeder_db` 兩個容器都正在運行 (`running`)。
    *   後端 API 將會運行在 `http://localhost:5070`。
    *   資料庫將會透過主機的 `5071` 連接埠對外提供服務。

## 組態詳情 (Configuration Details)

### 資料庫初始化

-   專案啟動時，會自動執行 `db/init.sql` 腳本來初始化資料庫的結構 (Schema) 與基礎資料。

### 圖片儲存目錄結構

所有上傳的圖片將會儲存在掛載的 `C:\pet-manage` 目錄下，並遵循以下結構。後端應用程式將會從容器內的 `/storage` 路徑來存取這些檔案。

-   **使用者大頭照**:
    -   路徑: `/storage/users/profile/{userId}.jpg`

-   **寵物大頭照**:
    -   路徑: `/storage/pets/{petId}/profile/profile.jpg`

-   **餵食紀錄照片**:
    -   路徑: `/storage/pets/{petId}/feedings/{yyyy-MM-dd}/{timestamp}.jpg`

-   **寵物相簿**:
    -   路徑: `/storage/pets/{petId}/albums/{albumName}/{photoName}.jpg`

## 停止服務

若要停止所有正在運行的容器，請在專案根目錄執行：
```bash
docker-compose down
```