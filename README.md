# VocaBloom

VocaBloom là ứng dụng Android hỗ trợ học và ôn từ vựng tiếng Anh theo chủ đề. Giao diện được triển khai bằng Java và Android XML, dựa trên thiết kế Stitch. Project dùng Firebase Authentication cho tài khoản và Cloud Firestore cho hồ sơ, mục tiêu học tập và dữ liệu tiến độ.

- **Package:** `com.vocabloom.app`
- **Ngôn ngữ/UI:** Java, Android XML
- **Min SDK:** 24 (Android 7.0)
- **Compile/Target SDK:** 35
- **Build:** Android Gradle Plugin 8.7.3, Gradle Wrapper 8.9, JDK 17
- **Dữ liệu từ vựng hiện có:** 50 từ trong 5 chủ đề

## Chức năng

- Đăng ký bằng email, xác nhận mật khẩu và đăng nhập Firebase Authentication.
- Trang chủ hiển thị mục tiêu hằng ngày, tiến độ, chuỗi ngày học và từ gợi ý.
- Duyệt các chủ đề Food & Drinks, University Life, Daily Life, IT và Environment; tìm kiếm và mở bộ thẻ.
- Học flashcard, nghe phát âm, lưu từ yêu thích và tự đánh giá mức độ nhớ.
- Màn Tiến độ tổng hợp từ đã học, chủ đề hoàn thành và các từ cần ôn lại.
- Hồ sơ cho phép chỉnh mục tiêu, khung giờ nhắc học, sửa tên và mật khẩu.
- Thông báo nhắc học được lên lịch trên Android; cài đặt nhắc học gắn với UID Firebase.

## Công nghệ và dữ liệu

- Firebase Authentication quản lý tài khoản; mật khẩu không được lưu trong Firestore.
- Cloud Firestore lưu dữ liệu tài khoản và tiến độ. Firestore Android SDK dùng cache offline và đồng bộ lại khi có mạng.
- Nội dung từ vựng và danh sách chủ đề hiện nằm trong `SampleData.java`; chưa phải collection Firestore.
- Migration tiến độ cũ giữ dữ liệu trên máy cho đến khi các ghi Firestore được xác nhận thành công.
- Android lưu cache cài đặt nhắc học để `AlarmManager` có thể tạo báo thức cho thiết bị.

Các collection/document chính:

| Đường dẫn | Nội dung |
|---|---|
| `users/{uid}` | Hồ sơ, email, mục tiêu ngày và thiết lập nhắc học |
| `userProgress/{uid}_{wordId}` | Cấp độ nhớ và thời điểm ôn tiếp theo của mỗi từ |
| `reviewLogs/{autoId}` | Lịch sử đánh giá từ |
| `users/{uid}/favorites/{wordId}` | Từ yêu thích của người dùng |

Mô hình dữ liệu vẫn là Firestore schemaless; xem logic ghi dữ liệu trong `app/src/main/java/com/vocabloom/app/data/`.

## Cài đặt và chạy trên máy phát triển

### Yêu cầu

- Android Studio có hỗ trợ Android Gradle Plugin 8.7.3.
- JDK 17.
- Android SDK Platform 35 và Build Tools tương ứng.
- Kết nối mạng ở lần build đầu để tải Gradle và dependencies.
- Một Firebase project nếu cần đăng ký, đăng nhập và đồng bộ dữ liệu.

### Thiết lập Firebase

1. Tạo Firebase project và đăng ký Android app với package chính xác `com.vocabloom.app`.
2. Tải cấu hình Android từ Firebase Console và đặt vào `app/google-services.json` trên máy phát triển. File này bị Git loại trừ, không commit lên repo.
3. Trong Firebase Authentication, bật phương thức **Email/Password**.
4. Tạo Cloud Firestore database.
5. Đọc và đối chiếu `firestore.rules` với Rules đang chạy trong Firebase Console. File trong repo là bản đề xuất; không triển khai đè lên rules hiện tại nếu chưa kiểm tra các collection đang dùng.
6. Nếu dùng Firebase CLI để triển khai rules sau khi đã rà soát: đăng nhập, chọn đúng project bằng `firebase use --add`, rồi chạy `firebase deploy --only firestore:rules`. Lệnh này thay đổi cấu hình Rules trên Firebase.

Không cần đặt service-account key vào project Android. Không chia sẻ file cấu hình cục bộ hoặc signing key trong issue/chat công khai.

### Build và cài APK debug

Mở project bằng Android Studio, chọn thư mục chứa `settings.gradle`, chờ Gradle Sync rồi chạy cấu hình `app`.

Hoặc dùng terminal ở thư mục gốc project:

**Windows PowerShell**

```powershell
.\gradlew.bat :app:assembleDebug
```

**macOS/Linux**

```bash
./gradlew :app:assembleDebug
```

APK debug được tạo tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Để cài lên thiết bị đã bật USB debugging:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Cho phép thông báo khi Android yêu cầu. Android 13 trở lên cần quyền thông báo để nhận nhắc học.

> Không có `app/google-services.json` thì project có thể biên dịch, nhưng Firebase Authentication/Firestore sẽ không hoạt động trong APK đó.

## GitHub Actions

Workflow `.github/workflows/android.yml` build APK debug cho mỗi push, pull request hoặc chạy thủ công. Để APK của workflow kết nối Firebase, thêm GitHub Actions repository secret tên `GOOGLE_SERVICES_JSON` với nội dung JSON cấu hình Firebase. Nếu secret không được thiết lập, workflow chỉ kiểm tra việc build; APK không đăng nhập Firebase được.

Workflow không triển khai Firestore Rules và không ghi vào Firebase.

## Cấu trúc thư mục

```text
app/src/main/java/com/vocabloom/app/
├── data/       Firebase, tiến độ, lịch ôn và dữ liệu mẫu
├── model/      Model từ vựng
└── ui/         Auth, Trang chủ, Chủ đề, Flashcard, Tiến độ, Hồ sơ
app/src/main/res/  Layout XML, drawable, font và resource Android
firebase-seed/     Script seed dữ liệu thử nghiệm
stitch-reference/  Tài liệu và tài nguyên thiết kế Stitch
firestore.rules    Rules đề xuất, cần rà soát trước khi deploy
```

## Bảo mật và đóng góp

`.gitignore` loại trừ `app/google-services.json`, `.firebaserc`, `local.properties`, signing keys, credentials, APK/AAB và output build. Trước khi push thay đổi, kiểm tra danh sách file được stage bằng `git status` và không thêm secrets.

Hiện chưa có test suite tự động. Sau khi build, nên đăng ký tài khoản thử nghiệm và kiểm tra đăng nhập, học từ, Tiến độ, yêu thích, nhắc học và hoạt động offline trước khi demo hoặc dùng với dữ liệu thật.