# Rencana Perbaikan HomeScreen Errors

Saya telah menemukan penyebab error di `HomeScreen.kt`. Masalah utamanya adalah:
1.  **Missing Dependency**: Icon-icon seperti `Notifications`, `ExitToApp`, dan `Storefront` memerlukan library `material-icons-extended` yang belum ada di project.
2.  **Experimental API**: Penggunaan `TopAppBar` di Material 3 memerlukan anotasi `@OptIn`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/gradle/libs.versions.toml)
- Tambahkan library `androidx-compose-material-icons-extended`.

#### [MODIFY] [app/build.gradle.kts](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/build.gradle.kts)
- Tambahkan dependensi `libs.androidx.compose.material.icons.extended`.

### Source Code

#### [MODIFY] [HomeScreen.kt](file:///media/pratama/Data1/FTI/baper/backend/baper-andoid/app/src/main/java/com/example/baper_andoid/ui/screen/home/HomeScreen.kt)
- Tambahkan `@OptIn(ExperimentalMaterial3Api::class)` di atas fungsi `HomeScreen`.
- Pastikan import untuk `Icons` dan material component sudah benar.

## Verification Plan

### Automated Tests
- Sinkronisasi Gradle.
- Jalankan `analyze_file` pada `HomeScreen.kt` untuk memastikan error hilang.
- Build project dengan `./gradlew assembleDebug`.
