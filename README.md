# Gyroscope Monitor

Aplikasi Android sederhana untuk memantau kecepatan rotasi perangkat melalui sensor gyroscope. Nilai pembacaan ditampilkan secara langsung untuk tiga sumbu: X, Y, dan Z.

## Cara kerja aplikasi

1. Saat aplikasi dibuka, aplikasi mencari sensor gyroscope pada perangkat dan menampilkan nama sensornya.
2. Jika sensor tersedia, aplikasi mendaftarkan listener gyroscope saat layar aplikasi aktif. Pembacaan menggunakan `SENSOR_DELAY_UI`.
3. Setiap pembaruan sensor menampilkan kecepatan rotasi pada sumbu X, Y, dan Z dalam **rad/s** (radian per detik).
4. Aplikasi menghitung besar kecepatan rotasi gabungan dari ketiga sumbu:

   ```text
   sqrt(x² + y² + z²)
   ```

   Jika hasilnya lebih besar dari `0.15 rad/s`, aplikasi menampilkan **“Rotasi terdeteksi”**. Jika tidak, aplikasi menampilkan **“Rotasi sangat kecil / HP diam”**.
5. Tombol **Jeda/Mulai** menghentikan atau melanjutkan pembacaan. Status jeda dipertahankan saat konfigurasi activity dibuat ulang.
6. Saat aplikasi tidak lagi berada di layar (misalnya, pengguna berpindah aplikasi), listener sensor dilepas. Pembacaan dilanjutkan kembali ketika aplikasi aktif lagi, selama pemantauan tidak dijeda.
7. Jika gyroscope tidak tersedia, aplikasi menampilkan informasi tersebut dan menonaktifkan tombol pemantauan.

Nilai yang ditampilkan adalah **kecepatan rotasi**, bukan sudut kemiringan perangkat. Aplikasi tidak meminta izin khusus untuk membaca sensor.

## Cara mencoba

1. Jalankan aplikasi pada perangkat Android yang memiliki gyroscope.
2. Pegang perangkat, lalu putar perlahan ke beberapa arah.
3. Amati perubahan nilai X, Y, dan Z beserta status rotasinya.
4. Hentikan gerakan untuk melihat nilai mendekati nol.
5. Tekan **Jeda** untuk menghentikan pembacaan, lalu **Mulai** untuk melanjutkannya.

## Menjalankan dan membangun

Persyaratan proyek:

- Android Studio dengan Android SDK 37 terpasang.
- Perangkat atau emulator Android API 24 (Android 7.0) atau lebih baru.
- Java 11 untuk kompilasi sumber.

Buka proyek ini di Android Studio, tunggu Gradle menyelesaikan sinkronisasi, lalu jalankan konfigurasi `app` pada perangkat atau emulator.

Untuk membangun APK debug dari PowerShell di Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

APK akan tersedia di `app/build/outputs/apk/debug/app-debug.apk`.

## Struktur utama

- `app/src/main/java/com/nbs/app/sensor/MainActivity.java` — inisialisasi gyroscope, pemantauan lifecycle, penghitungan rotasi, dan pembaruan tampilan.
- `app/src/main/res/layout/activity_main.xml` — layout pembacaan sensor, status, tombol jeda/mulai, dan petunjuk penggunaan.
- `app/src/main/AndroidManifest.xml` — deklarasi aplikasi dan fitur gyroscope opsional.
