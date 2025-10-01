# 部署指南 (Deployment Guide)

本文件提供將專案部署至本地或正式環境的詳細步驟。

## 先決條件

- [Docker](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## 本地部署 (Local Deployment)

本地部署是透過 `docker-compose` 來啟動後端服務與資料庫，快速建立一個與正式環境一致的開發與測試環境。

### 步驟

1.  **建立圖片儲存目錄**:
    *   在專案的根目錄下，建立一個名為 `storage` 的資料夾。
    *   **重要**: 此目錄已在 `docker-compose.yml` 中設定為掛載點。所有由後端服務儲存的圖片，都會被保存在這個目錄下。

2.  **啟動服務**:
    *   在專案的根目錄下，開啟一個終端機。
    *   執行以下指令來建置並啟動所有服務：
        ```bash
        docker-compose up --build -d
        ```
    *   `-d` 參數會讓容器在背景執行。

3.  **驗證服務狀態**:
    *   執行 `docker-compose ps` 來確認 `pet_feeder_backend` 與 `pet_feeder_db` 兩個容器都正在運行 (`running`)。
    *   後端 API 將會運行在 `http://localhost:5070`。
    *   資料庫將會運行在 `localhost:5071`。

## 圖片儲存目錄結構

所有上傳的圖片將會儲存在掛載的 `./storage` 目錄下，並遵循以下結構。後端應用程式將會從容器內的 `/storage` 路徑來存取這些檔案。

-   **使用者大頭照**:
    -   路徑: `/storage/users/profile/{userId}.jpg`

-   **寵物大頭照**:
    -   路徑: `/storage/pets/{petId}/profile/profile.jpg`

-   **餵食紀錄照片**:
    -   路徑: `/storage/pets/{petId}/feedings/{yyyy-MM-dd}/{timestamp}.jpg`
    -   備註: 透過日期分層，避免單一目錄下檔案過多。

-   **寵物相簿**:
    -   路徑: `/storage/pets/{petId}/albums/{albumName}/{photoName}.jpg`

## 停止服務

若要停止所有正在運行的容器，請在專案根目錄執行：
```bash
docker-compose down
```