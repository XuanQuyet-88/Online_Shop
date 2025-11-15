# 🛍️ Online Shop - Ứng dụng Bán Quần Áo

Một ứng dụng bán hàng thời trang hiện đại được xây dựng cho nền tảng Android bằng **Kotlin + Jetpack Compose**.

Ứng dụng cho phép người dùng xem sản phẩm, đặt hàng, tìm kiếm theo tên và quản lý đơn hàng.

Dữ liệu được xử lý qua **Firebase Authentication**, **Realtime Database**, và API **OkHttp** để thay đổi ảnh đại diện người dùng.

---

## 📸 Ảnh chụp màn hình

### 1️⃣ Nhóm màn hình chính

| Màn hình Đăng nhập | Màn hình Sản phẩm | Chi tiết sản phẩm |
|:------------------:|:-----------------:|:------------------:|
| ![Login](https://github.com/user-attachments/assets/e683f656-e7ad-4890-a896-af4a10904d9d) | ![Home](https://github.com/user-attachments/assets/01a8add2-7d95-4610-88d3-e19feaa3cf66) | ![Detail](https://github.com/user-attachments/assets/3e6899cd-ebcb-4535-839e-5056111e502e) |

### 2️⃣ Tìm kiếm – Giỏ hàng – Đơn hàng

| Tìm kiếm sản phẩm | Giỏ hàng | Danh sách đơn hàng |
|:-----------------:|:--------:|:------------------:|
| ![Search](https://github.com/user-attachments/assets/ad747dec-d07f-4ee5-93bd-87894e4b6814) | ![Cart](https://github.com/user-attachments/assets/3b655dd0-2073-4804-99f9-5812e0791c30) | ![Orders](https://github.com/user-attachments/assets/b645c9d8-79bc-4ac9-a564-63e07b63c731) |

---

## ✨ Tính năng chính

### 🔐 Xác thực người dùng (FirebaseAuth)

- Đăng ký & đăng nhập bằng Email/Password
- Lưu phiên đăng nhập
- Đăng xuất nhanh chóng

### 👕 Danh mục & Chi tiết sản phẩm

- Xem danh sách sản phẩm quần áo theo dạng lưới / list
- Xem chi tiết sản phẩm: Tên, Giá, Ảnh, Mô tả
- Thêm vào giỏ hàng
- Mua ngay

### 🔍 Tìm kiếm & Lọc

- Tìm kiếm sản phẩm theo **tên** (real-time)
- Lọc theo danh mục

### 🛒 Đặt hàng (Order)

- Tạo đơn hàng mới
- Lưu thông tin đơn hàng vào Firebase
- Xem tất cả đơn đã đặt

### 📦 Quản lý đơn hàng

- Xem danh sách đơn hàng
- Phân loại theo trạng thái: Pending, Shipping, Completed, Cancelled

### 👤 Hồ sơ người dùng

- Hiển thị thông tin user
- Upload & thay đổi ảnh đại diện thông qua **OkHttp API**
- Lưu đường dẫn avatar vào Firebase

---

## 🏗️ Kiến trúc & Công nghệ sử dụng

### Kiến trúc

- **MVVM** - Model View ViewModel
- **State-based UI** - Jetpack Compose
- **Repository Pattern** cho Firebase + API

### Công nghệ

**Ngôn ngữ & UI**

- Kotlin
- Jetpack Compose
- Material3

**Firebase**

- Firebase Authentication
- Realtime Database

**Networking**

- OkHttp (upload/update avatar)
- Multipart Request để gửi ảnh

**Thư viện khác**

- Coil - để hiển thị ảnh
- Lottie - hiệu ứng và ảnh động
- Navigation Compose

---

## 🌳 Cấu trúc thư mục dự án

```
OnlineShop (com.example.onlineshop)
├── activity/
│   ├── BaseActivity.kt
│   ├── ListItems.kt
│   └── MainActivity.kt
│
├── data/
│   ├── model/
│   │   ├── CartItem.kt
│   │   ├── CategoryModel.kt
│   │   ├── ItemsModel.kt
│   │   ├── Order.kt
│   │   └── UserModel.kt
│   │
│   └── repository/
│       ├── AuthRepository.kt
│       ├── MainRepository.kt
│       └── OrderRepository.kt
│
├── helper/
│   ├── CartManager.kt
│   └── CloudinaryUploader.kt
│
├── navigation/
│   ├── OnlineShopApp.kt
│   └── Routes.kt
│
└── ui/
    ├── screens/
    │   ├── auth/
    │   │   ├── LoginScreen.kt
    │   │   ├── RegisterScreen.kt
    │   │   └── ResetPasswordScreen.kt
    │   │
    │   └── home/
    │       ├── CartScreen.kt
    │       ├── CheckoutScreen.kt
    │       ├── DetailScreen.kt
    │       ├── HomeScreen.kt
    │       ├── ListItemsScreen.kt
    │       ├── OrderScreen.kt
    │       ├── ProfileScreen.kt
    │       └── SearchScreen.kt
    │
    ├── intro/
    │   └── (Intro-related files)
    │
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    │
    └── viewModel/
        ├── AuthViewModel.kt
        ├── CheckoutViewModel.kt
        ├── MainViewModel.kt
        └── OrderViewModel.kt
```

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

- Android Studio Hedgehog trở lên
- Android SDK 24 trở lên
- Kotlin 1.9+
- Gradle 8.0+

### Các bước cài đặt

**1. Clone repository**

```bash
git clone https://github.com/XuanQuyet-88/Online_Shop.git
```

**2. Cấu hình Firebase**

- Tạo dự án Firebase tại [firebase.google.com](https://firebase.google.com)
- Tải file `google-services.json` và đặt vào thư mục `app/`
- Bật các dịch vụ trong Firebase Console:
  - Authentication (Email/Password)
  - Realtime Database
  - Storage

**3. Cấu hình OkHttp API**

- Thêm URL API để upload avatar vào `build.gradle` hoặc file config
- Cấu hình Multipart request trong `CloudinaryUploader.kt` hoặc helper tương tự

**4. Mở dự án trong Android Studio**

- Chọn "Open" → chọn thư mục dự án
- Android Studio sẽ tự động sync Gradle dependencies

**5. Chạy ứng dụng**

```bash
./gradlew build
```

- Nhấn Run → chọn emulator hoặc thiết bị Android kết nối

---

## 📚 Các thành phần chính

### Authentication (AuthRepository)

Xử lý logic xác thực người dùng:

- Đăng ký tài khoản mới
- Đăng nhập với Email/Password
- Lưu/load thông tin người dùng từ Realtime Database
- Quản lý phiên đăng nhập

### Product Management (MainRepository)

Quản lý dữ liệu sản phẩm:

- Tải danh sách sản phẩm từ Realtime Database
- Tìm kiếm sản phẩm theo tên (real-time)
- Lọc theo danh mục
- Lưu thông tin chi tiết sản phẩm

### Shopping Cart (CartManager)

Quản lý giỏ hàng:

- Thêm/xóa sản phẩm khỏi giỏ
- Cập nhật số lượng
- Tính toán tổng giá
- Lưu trạng thái giỏ hàng

### Orders (OrderRepository)

Xử lý đơn hàng:

- Tạo đơn hàng mới
- Lưu đơn hàng vào Firebase Realtime Database
- Tải danh sách đơn hàng của người dùng
- Cập nhật trạng thái đơn hàng

### Image Upload (CloudinaryUploader)

Quản lý upload ảnh:

- Upload ảnh đại diện người dùng qua OkHttp API
- Xử lý Multipart request
- Lưu đường dẫn ảnh vào Firebase

---

## 🎨 Giao diện & Thiết kế

Ứng dụng sử dụng **Jetpack Compose** với Material Design 3:

- **Giao diện hiện đại**: Smooth animations, transitions
- **Responsive**: Tích hợp tốt trên các kích thước màn hình khác nhau
- **Dark Mode Support**: Hỗ trợ chế độ tối
- **Bottom Navigation**: Điều hướng dễ sử dụng

---

## 📦 Dependencies

Thêm các dependency này vào file `build.gradle` (Module: app):

```gradle
dependencies {
    // Kotlin & Core
    implementation 'androidx.core:core-ktx:1.12.0'
    
    // Jetpack Compose
    implementation 'androidx.compose.ui:ui:1.6.x'
    implementation 'androidx.compose.material3:material3:1.1.x'
    implementation 'androidx.compose.runtime:runtime-livedata:1.6.x'
    
    // Navigation Compose
    implementation 'androidx.navigation:navigation-compose:2.7.x'
    
    // ViewModel & LiveData
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.6.x'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.x'
    
    // Firebase
    implementation 'com.google.firebase:firebase-auth-ktx:22.3.x'
    implementation 'com.google.firebase:firebase-database-ktx:20.3.x'
    implementation 'com.google.firebase:firebase-storage-ktx:20.3.x'
    
    // Coil for image loading
    implementation 'io.coil-kt:coil-compose:2.5.x'
    
    // OkHttp
    implementation 'com.squareup.okhttp3:okhttp:4.11.x'
    
    // Lottie
    implementation 'com.airbnb.android:lottie-compose:6.1.x'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.x'
}
```

---

## 🔧 Cấu hình Firebase

### Tệp `google-services.json`

Tải từ Firebase Console và đặt tại: `app/google-services.json`

### Kích hoạt các dịch vụ

Vào [Firebase Console](https://console.firebase.google.com) → chọn dự án:

1. **Authentication** → Enable Email/Password
2. **Realtime Database** → Create Database (Start in test mode)
3. **Storage** → Create Bucket

---

## 📄 Giấy phép

Dự án này được cấp phép dưới giấy phép **MIT** - xem file [LICENSE](LICENSE) để chi tiết.

---

## 👤 Tác giả

**Nguyễn Xuân Quyết** - [@XuanQuyet](https://github.com/XuanQuyet-88)

---

## 📞 Liên hệ & Hỗ trợ

- Email: nguyenxuanquyetk17@gmail.com

