# RestAPI Session04 — Spring Boot + Docker + CI/CD VPS

## 1. Tổng quan

- Spring Boot `4.0.6`, Java `17` (`build.gradle:11-15`), Gradle Wrapper
- MySQL `8.0`, JPA `ddl-auto=update` (`application.properties:12`)
- Port app `8080` (`application.properties:2`, `docker-compose.yml:25`)
- Repo: `https://github.com/An256122006/PTIT_CNTT1_IT209_Session14.git`, nhánh `master`
- Image Docker Hub: `trongan2006/devops-exam-app` (`.github/workflows/deploy.yml:8`)

File quan trọng:

| File | Vai trò |
|---|---|
| `Dockerfile:1-9` | Runtime `eclipse-temurin:17-jre`, copy `build/libs/app.jar` |
| `docker-compose.yml:1-36` | 2 service `mysql` + `backend`, `backend` có cả `build:` và `image:` |
| `.env.example:1-19` | Mẫu biến môi trường, copy thành `.env` khi chạy local |
| `.env` | File thật, đã ignore (` .gitignore:40`), không commit |
| `src/main/resources/application.properties:1-17` | Đọc config từ biến môi trường, fallback chạy local |
| `.github/workflows/deploy.yml:1-75` | Pipeline build + push + SSH deploy VPS |

## 2. Yêu cầu

- JDK 17, Docker + Compose plugin, Git
- Tài khoản Docker Hub + VPS Ubuntu (mở port `22, 8080`)

## 3. Chạy local từ đầu

```bash
# 1. Clone
git clone https://github.com/An256122006/PTIT_CNTT1_IT209_Session14.git
cd PTIT_CNTT1_IT209_Session14
git checkout master

# 2. Tạo .env từ mẫu
cp .env.example .env
# Local để nguyên:
# DOCKER_IMAGE=restapi-session04:latest
# SERVER_PORT=8080

# 3. Build jar (bắt buộc trước khi build Docker vì Dockerfile COPY build/libs/app.jar)
chmod +x gradlew
./gradlew clean bootJar -x test

# 4. Chạy compose (dùng --build, KHÔNG dùng pull ở local)
docker compose up -d --build
docker compose ps
docker compose logs backend --tail=50
```

Nếu báo `port is already allocated` (do project khác như `campus-hub-backend` đang chiếm `8080`), đổi trong `.env`:
```
SERVER_PORT=8081
```
rồi `docker compose up -d`, test ở `http://localhost:8081/...`.

Dừng / xóa:
```bash
docker compose down
docker compose down -v  # xóa luôn data MySQL
```

## 4. API để verify

| Method | URL |
|---|---|
| GET | `http://localhost:8080/api/employees/count` |
| GET | `http://localhost:8080/api/v1/categories` |
| POST | `http://localhost:8080/api/v1/categories` |
| PUT | `http://localhost:8080/api/v1/categories/{cateId}` |
| DELETE | `http://localhost:8080/api/v1/categories/{cateId}` |
| GET | `http://localhost:8080/api/v1/products?page=1` |
| GET | `http://localhost:8080/api/v1/products/list-products?page=1&orderBy=proId&sortBy=asc` |
| POST | `http://localhost:8080/api/v1/products` |

```bash
curl -f http://localhost:8080/api/employees/count
curl -f http://localhost:8080/api/v1/categories
curl -f "http://localhost:8080/api/v1/products?page=1"
```

`/api/...` là placeholder nên trả `404`, phải dùng URL đầy đủ ở trên.

## 5. Chuẩn bị Docker Hub (1 lần)

1. Tạo repo public `trongan2006/devops-exam-app`.
2. Tạo Access Token: Docker Hub > Account Settings > Security > New Access Token (Read/Write).
3. Test ở máy local:
```bash
docker logout
docker login -u trongan2006
```

## 6. Chuẩn bị VPS (1 lần)

```bash
ssh user@VPS_IP
sudo apt update && sudo apt install -y git
# cài Docker theo docs.docker.com
docker --version
docker compose version
sudo usermod -aG docker $USER  # logout/login lại

cd ~
git clone https://github.com/An256122006/PTIT_CNTT1_IT209_Session14.git devops-exam
cd ~/devops-exam
git checkout master
```

Tạo SSH key cho GitHub Actions (chạy ở máy local):
```powershell
ssh-keygen -t ed25519 -C "github-actions"
# sinh ~/.ssh/id_ed25519 (private) + id_ed25519.pub (public)
ssh-copy-id user@VPS_IP
ssh -i ~/.ssh/id_ed25519 user@VPS_IP  # test ok là được
```

`-C "github-actions"` chỉ là comment để nhớ key dùng cho việc gì.

Tạo `.env` đầu tiên trên VPS (`~/devops-exam/.env`):
```
MYSQL_DATABASE=db_cntt3_api_ss4
MYSQL_ROOT_PASSWORD=1234$
MYSQL_USER=app_user
MYSQL_PASSWORD=1234$
DOCKER_IMAGE=trongan2006/devops-exam-app:latest
SERVER_PORT=8080
PAGE_SIZE_PRODUCTS=3
```

Deploy tay lần đầu:
```bash
cd ~/devops-exam
docker login -u trongan2006
docker compose pull
docker compose up -d --remove-orphans
docker ps
docker compose logs backend --tail=50
```

Workflow chỉ chạy `cd ~/devops-exam + git pull`, nên folder này phải tồn tại trước.

## 7. GitHub Secrets (Repo > Settings > Secrets > Actions)

| Secret | Giá trị |
|---|---|
| `DOCKERHUB_USERNAME` | `trongan2006` |
| `DOCKERHUB_TOKEN` | `dckr_pat_...` (paste bằng tay, không commit) |
| `VPS_HOST` | IP VPS, vd `123.30.45.67` (không thêm `http://`) |
| `VPS_USER` | user SSH, vd `devops` / `root` |
| `VPS_SSH_KEY` | toàn bộ nội dung private key `id_ed25519` (từ `-----BEGIN...` đến `-----END...`) |
| `MYSQL_DATABASE` | `db_cntt3_api_ss4` |
| `MYSQL_ROOT_PASSWORD` | `1234$` |
| `MYSQL_USER` | `app_user` |
| `MYSQL_PASSWORD` | `1234$` |

Public key (`ssh-ed25519 AAAA...`) để vào `~/.ssh/authorized_keys` trên VPS.
Private key để vào `VPS_SSH_KEY`. Không làm ngược lại.

## 8. Pipeline CI/CD làm gì

Push lên `master` kích hoạt `deploy.yml`:

1. Job `build-and-push`: checkout > setup JDK 17 > `./gradlew clean bootJar -x test` (bỏ test để không cần MySQL trên CI) > login Docker Hub > build + push 2 tag `latest` và `${{ github.sha }}`.
2. Job `deploy` (SSH vào VPS): `git pull --ff-only origin master` > viết lại `.env` từ Secrets (dùng `<<'EOF'` để `$` trong `1234$` không bị expand) với `DOCKER_IMAGE=trongan2006/devops-exam-app:${{ github.sha }}` > `chmod 600 .env` > `docker compose pull` > `docker compose up -d --remove-orphans` > `docker ps` + logs.

Chạy deploy:
```bash
git add -A
git commit -m "ci: build and deploy"
git push origin master
```
Xem tiến trình ở tab Actions.

## 9. Verify trên VPS

```bash
cd ~/devops-exam
cat .env
docker ps
docker compose ps
docker compose logs backend --tail=100
curl -f http://localhost:8080/api/employees/count
curl -f http://localhost:8080/api/v1/categories
```

## 10. Lỗi hay gặp

| Lỗi | Nguyên nhân / sửa |
|---|---|
| `pull access denied for restapi-session04` | `backend` thiếu `build:` hoặc chạy `pull` ở local. Local phải `docker compose up -d --build`. `pull` chỉ dùng trên VPS khi `DOCKER_IMAGE` là image Docker Hub có thật. Đã fix `docker-compose.yml:19-21`. |
| `Bind for 0.0.0.0:8080 failed: port is already allocated` | Port bị container khác giữ (vd `campus-hub-backend`). Đổi `SERVER_PORT=8081` trong `.env` hoặc `docker stop <container-kia>`. |
| `curl .../api/... -> 404` | `/api/...` không phải endpoint. Dùng URL đầy đủ ở mục 4. `404` nghĩa là app đã chạy, chỉ sai đường dẫn. |
| `port: ${{ secrets.VPS_PORT \|\| 22 }}` lỗi | Cú pháp không hợp lệ. Để `port: 22`. |
| CI không chạy | Push nhầm nhánh. Pipeline trigger `branches: [master]`. |
| `git pull` fail trên VPS | Do commit trực tiếp trên VPS hoặc sai nhánh. Kiểm tra `git status`, dùng `--ff-only origin master`. |
