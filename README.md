# Simple Drive Project

A lightweight application for storing and retrieving files (blobs) using various storage types like S3, local file system, database, and FTP.

---

## Prerequisites
- **Java Development Kit (JDK):** Version 21
- **Build Tool:** Gradle
- **Database:** PostgreSQL
- **S3-compatible storage**
- **FTP server**

---

## Getting Started

### Clone the Repository
```
git clone https://github.com/Shaar03/rekaz.git
cd rekaz
```

### Set Up the Environment Variables
Create a `.env` file in the `src/main/resources/` directory and populate it with the following:

```
DB_URL=jdbc:postgresql://localhost:5432/your_database
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

S3_ENDPOINT=https://s3.your-region.amazonaws.com
S3_BUCKET_NAME=your_bucket_name
S3_ACCESS_KEY=your_access_key
S3_SECRET_KEY=your_secret_key

FTP_HOST=your_ftp_host
FTP_PORT=21
FTP_USERNAME=your_ftp_username
FTP_PASSWORD=your_ftp_password
STORAGE_ROOT_PATH=/path/to/local/storage
JWT_SECRET=your_secret_key_for_jwt
```

Replace the placeholder values with your actual configuration.

---

### Run the Project
```
./gradlew bootRun
```

---

## API Endpoints

### Authentication

#### Login
- **URL:** `/auth/login`
- **Method:** `POST`
- **Request Body (JSON):**
  ```json
  {
    "username": "dummyuser",
    "password": "password"
  }
  ```

- For simplicity, use the predefined credentials:
  - **Username:** `dummyuser`
  - **Password:** `password`

Include the received `token` in the `Authorization` header (`Bearer token`) for all subsequent requests.

---

### Blobs API

#### Upload Blob
- **URL:** `/v1/blobs`
- **Method:** `POST`
- **Request Body (JSON):**
  ```json
  {
    "id": "any_valid_string_or_identifier",
    "data": "SGVsbG8gU2ltcGxlIFN0b3JhZ2UgV29ybGQh",
    "type": "S3"
  }
  ```
- **Supported Types:**  
  - `S3`
  - `DB_TABLE`
  - `LOCAL_FILE_SYSTEM`
  - `FTP`

---

#### Retrieve Blob
- **URL:** `/v1/blobs/{id}`
- **Method:** `GET`
- **Headers:**
  - `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id`: The identifier of the blob.
- **Retrieve Blob Example Response:**
  ```json
  {
      "id": "any_valid_string_or_identifier",
      "data": "SGVsbG8gU2ltcGxlIFN0b3JhZ2UgV29ybGQh",
      "size": "27",
      "created_at": "2023-01-22T21:37:55Z"
  }
  ```

---

## Storage Types
The application supports the following storage types:

```java
public enum StorageTypeEnum {
    S3,
    DB_TABLE,
    LOCAL_FILE_SYSTEM,
    FTP
}
```

Specify the storage type in the `type` field when uploading a blob.
