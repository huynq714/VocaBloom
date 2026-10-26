# VocaBloom v0.5 — Stitch UI adaptation

## Nguồn
- Android Java/XML: project v0.3 do người dùng cung cấp.
- Stitch: 6 màn hình PNG + HTML mẫu và DESIGN.md trong gói Stitch được người dùng cung cấp.

## Thay đổi
- Hệ màu theo Stitch: nền #FBF9F6, primary #A93200, chữ #1B1C1A, card trắng/viền mảnh.
- Cập nhật Trang chủ: lời chào tài khoản, mục tiêu học, lộ trình, ôn tập và từ gợi ý.
- Cập nhật trang Học từ: thanh tìm kiếm mang tính hiển thị, card chủ đề từ dữ liệu mẫu và điều hướng đến flashcard.
- Cập nhật Hồ sơ: ảnh đại diện chữ cái, mục tiêu ngày, nút đổi tên, đặt lại mật khẩu, kiểm tra Firebase và đăng xuất xác nhận.
- Cập nhật thanh điều hướng với 4 mục: Trang chủ, Học từ, Tiến độ, Hồ sơ.
- Cập nhật giao diện Đăng nhập, thẻ Flashcard và header Tiến độ nhất quán màu sắc.

## Cách chạy
1. Sao lưu project v0.3 đang chạy tốt.
2. Giải nén ZIP này và mở thư mục `VocaBloom` bằng Android Studio.
3. Chép **chỉ trên máy bạn** file `google-services.json` của v0.3 vào `VocaBloom/app/google-services.json`.
4. Sync Gradle, Rebuild, chạy emulator Pixel 4a API 34.
5. Đăng nhập tài khoản cũ. Thử cả 4 tab dưới, đổi tên, lưu mục tiêu, đổi mật khẩu, đăng xuất, flashcard.

## Giới hạn cần biết
- Đây là bản chuyển **có chức năng**, không phải pixel-perfect 100%: thiết kế Stitch là HTML/CSS tĩnh, Android render khác.
- Chưa build/run trên Android SDK trong môi trường soạn thảo này; cần xác nhận compile trên máy bạn.
- Chủ đề và thẻ từ vẫn lấy từ `SampleData`; tiến độ vẫn có lớp local progress và đồng bộ cloud best-effort như v0.3.
- Ô tìm kiếm chủ đề hiện là mô phỏng giao diện, chưa có chức năng lọc.
- Màn hình Stitch “Tiến độ” gồm 3 card chủ đề demo không khớp cấu trúc thống kê hiện tại; màn thống kê hiện giữ số liệu thực.
- Bộ font gốc Stitch (Literata / Plus Jakarta Sans) không được nhúng; Android dùng font hệ thống sans/serif để tránh lỗi font tiếng Việt.
