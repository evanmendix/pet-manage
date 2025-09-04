# 系統設計 (API)

## 後端 API 設計

*   **端點 (Endpoints) 規劃 (以 `/api/v1` 為前綴)**:
    *   `POST /users`: 建立新使用者。
    *   `PUT /users/{userId}`: 更新使用者名稱與大頭貼 `URL`。
    *   `POST /feedings`: 新增一筆餵食紀錄。
    *   `GET /feedings?limit=30`: 取得最近的餵食紀錄。
    *   `GET /status/current`: 取得當前早餐/晚餐的餵食狀態。
    *   `POST /pets/{petId}/weights`: 新增一筆體重紀錄。
    *   `GET /pets/{petId}/weights`: 取得歷史體重紀錄。
    *   `GET /albums/photos`: 取得相簿的所有照片 `URL`。
*   **安全性**:
    *   所有 `API` 請求的 `Header` 中必須包含由 `Firebase Authentication` 簽發的 `Authorization: Bearer <ID_TOKEN>`。
    *   後端 `API` 服務會使用 `Firebase Admin SDK` 驗證此 `Token` 的有效性，確保只有登入的家庭成員可以存取數據。

## 資料庫設計 (Firestore Schema)

*   **Collection: `families`**
    *   **Document: `{familyId}`**
        *   **Collection: `users`**
            *   **Document: `{userId}`** (來自 `Firebase Auth` 的 `UID`)
                *   `name`: String
                *   `profilePictureUrl`: String (指向 `Cloud Storage` 的 `URL`)
                *   `fcmToken`: String (用於推播的裝置 `Token`)
        *   **Collection: `pets`**
            *   **Document: `{petId}`**
                *   `name`: String
                *   `birthdate`: Timestamp
                *   `profileImageUrl`: String
                *   **Collection: `feedings`**
                    *   **Document: `{feedingId}`**
                        *   `feederId`: String (關聯到 `users` 的 `{userId}`)
                        *   `feederName`: String (冗餘欄位，方便查詢)
                        *   `feederProfilePictureUrl`: String (冗餘欄位，方便查詢)
                        *   `feedType`: String (`"meal"` / `"snack"`)
                        *   `timestamp`: Timestamp
                        *   `photoUrl`: String
                *   **Collection: `weights`**
                    *   **Document: `{weightId}`**
                        *   `timestamp`: Timestamp
                        *   `weightInKg`: Number
