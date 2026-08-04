# Implementation Plan - Fix MainActivity.kt Errors

The `MainActivity.kt` file has several errors including unresolved references for Lottie, incorrect package name, and incorrect theme usage. Additionally, the Lottie animation file is in the wrong resource directory.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Yasa/app/baper-aplication/baper-andoid/gradle/libs.versions.toml)
- Add Lottie Compose version and library definition.

#### [MODIFY] [build.gradle.kts](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/build.gradle.kts)
- Add `libs.lottie.compose` to dependencies.

### Resources

#### [NEW] `app/src/main/res/raw/` directory
- Create the `raw` resource directory.

#### [MOVE] `app/src/main/res/logo_vectorized.json` to `app/src/main/res/raw/logo_vectorized.json`
- Move the Lottie animation file to the correct location so `R.raw.logo_vectorized` can be resolved.

### Source Code

#### [MODIFY] [MainActivity.kt](file:///C:/Yasa/app/baper-aplication/baper-andoid/app/src/main/java/com/example/baper_andoid/MainActivity.kt)
- Update package name to `com.example.baper_andoid`.
- Update theme import and usage from `MyApplicationTheme` to `BaperandoidTheme`.
- Ensure all Lottie imports are correct once the dependency is added.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the project builds without errors.

### Manual Verification
- The IDE should no longer show red underlines for Lottie, the theme, or the `R.raw` resource.
