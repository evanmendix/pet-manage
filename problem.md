目前執行 android app後出現錯誤，附上局部錯誤資訊，請嘗試替我修復


> Task :android:app:checkDebugAarMetadata FAILED
> Execution failed for task ':android:app:checkDebugAarMetadata'.
> Configuration `:android:app:debugRuntimeClasspath` contains AndroidX dependencies, but the `android.useAndroidX` property is not enabled, which may cause runtime issues.
Set `android.useAndroidX=true` in the `gradle.properties` file and retry.
The following AndroidX dependencies are detected:
:android:app:debugRuntimeClasspath -> androidx.core:core-ktx:1.12.0
:android:app:debugRuntimeClasspath -> androidx.hilt:hilt-navigation-compose:1.1.0 -> androidx