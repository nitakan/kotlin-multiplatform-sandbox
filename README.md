# KMP と Platform 間接続デモアプリ

このリポジトリは Kotlin Multiplatform (以下、KMP) を用いてビジネスロジックを共通化しつつ、Android/iOS など各プラットフォームで独自に実装した機能を呼び出すサンプルコードです。  
**インターフェース + 依存性注入(DI)** を活用して **KMP → Platform 固有コード** を利用するアプローチを実装しています。

---

## 目次
1. [目的](#目的)
2. [サンプル概要](#サンプル概要)
3. [実装のポイント](#実装のポイント)
4. [ディレクトリ構成](#ディレクトリ構成)
5. [動作確認手順](#動作確認手順)
6. [参考](#参考)

---

## 目的

- 本サンプルでは **インターフェース + DI** を採用し、KMP コード（ViewModel など）から Android/iOS の固有実装を呼び出す構成を示しています。
- 「既存資産やネイティブライブラリをそのまま利用したい」「DIパターンを適用しやすくしたい」といった要件を満たせることを確認するのが狙いです。

---

## サンプル概要

### 全体の流れ

1. **KMP 共有コード (shared module) にインターフェースを定義**
    - `PlatformRepository` / `PlatformRepositoryFactory` 等

2. **KMP 側の ViewModel でインターフェースを使う**
    - `DisplayDataViewModel` が `PlatformRepository` を利用し、同期/非同期でデータ取得

3. **各プラットフォームでインターフェースの実装を用意**
    - Android: `PlatformRepositoryImpl` が実装され、Koin などで依存注入
    - iOS: `ClientRepositoryImpl` + 拡張 `ClientRepository` が実装され、SwiftUI などで注入

4. **ViewModel にプラットフォーム固有の実装を渡して利用**
    - Android: `MainApp` や `MainScreen` で依存を注入・呼び出し
    - iOS: `MainView` / `DisplayView` などで依存を注入・利用

この流れにより、KMP のビジネスロジックから、Android/iOS それぞれで固有の実装を呼び出せるようになります。

---

## 実装のポイント

1. **インターフェース (`PlatformRepository`) と工場 (`PlatformRepositoryFactory`) の定義**
   ```kotlin
   interface PlatformRepository {
       fun getDataSync(): PlatformData
       suspend fun getDataAsync(): PlatformData
   }

   interface PlatformRepositoryFactory {
       fun createClientRepository(): PlatformRepository
   }
   ```
    - 共通化したい機能を `PlatformRepository` で定義し、実際の実装はプラットフォームごとに行います。
    - インターフェースに加え `Factory` を用意しておくと、プラットフォーム固有コードの生成処理を隠蔽できます。

2. **KMP 側 ViewModel (`DisplayDataViewModel`) での利用**
   ```kotlin
   class DisplayDataViewModel(
       private val kmpRepository: KmpRepository,
       private val platformRepository: PlatformRepository,
   ) : ViewModel() {

       private val _displayData = MutableStateFlow<DisplayData>(DisplayData.loading())
       val displayData: StateFlow<DisplayData> = _displayData.asStateFlow()

       init {
           viewModelScope.launch {
               loadData()
           }
       }

       suspend fun loadData() {
           _displayData.emit(DisplayData.Loading)
           // 同期データ取得
           val platformSyncData = platformRepository.getDataSync()
           val kmp = kmpRepository.getKmpData()
           _displayData.emit(DisplayData.Loaded(platformSyncData, kmp))

           // 非同期データ取得
           val platformAsyncData = platformRepository.getDataAsync()
           _displayData.emit(DisplayData.Loaded(platformAsyncData, kmp))
       }
   }
   ```
    - KMP の ViewModel から `platformRepository` を通じて同期・非同期のデータを取得する実装例です。
    - `kmpRepository` はあくまで KMP 内のビジネスロジックを呼び出し、`platformRepository` ではプラットフォーム固有の処理を呼び出します。

3. **Android 側実装 (`PlatformRepositoryImpl`)**
   ```kotlin
   class PlatformRepositoryImpl: PlatformRepository {
       override fun getDataSync(): PlatformData {
           return PlatformData("[Android]The client generated sync data")
       }

       override suspend fun getDataAsync(): PlatformData {
           delay(1000L)
           return PlatformData("[Android]The client generated async data")
       }
   }
   ```
    - Android では Kotlin でそのまま実装できます。
    - 依存注入には [Koin](https://insert-koin.io/) を使用する例が多いですが、Dagger/Hilt でも同様のアプローチが可能です。

4. **iOS 側実装 (`ClientRepositoryImpl` + 拡張)**
   ```swift
   // async/await 版
   extension ClientRepositoryImpl: ClientRepository {
       func getDataSync() -> ClientData {
           return ClientData(value: "[iOS]The client generated sync data")
       }

       func getDataAsync() async throws -> ClientData {
           _ = try await Task.sleep(nanoseconds: 1000_000_000)
           return ClientData(value: "[iOS]The client generated async data")
       }
   }

   // コールバック版
   extension ClientRepositoryImpl: ClientRepository {
       func getDataSync() -> ClientData {
           return ClientData(value: "[iOS]The client generated sync data")
       }

       func getDataAsync(
           completionHandler: @escaping (PlatformData?, (any Error)?) -> Void
       ) {
           DispatchQueue.global().asyncAfter(deadline: .now() + 1.0) {
               // エラーがなければデータを返す
               let data = PlatformData(value: "[iOS]The client generated async data")
               completionHandler(data, nil)
           }
       }
   }
   ```
    - Swift 側で `ClientRepository` プロトコルに対応する実装を用意し、KMP が生成するインターフェースに適合させます。
    - 非同期処理は `async/await` あるいはコールバックの形で KMP と連携できます。

5. **ViewModel への注入**
    - iOS: SwiftUI の `@Dependency` などを活用し、ViewModel 生成時に `PlatformRepositoryFactory` を通して `PlatformRepository` を渡します。
    - Android: [Koin](https://insert-koin.io/) で `MainApp` にモジュール定義し、`PlatformRepository` や `DisplayDataViewModel` の生成を管理します。

---

## ディレクトリ構成

一例として、以下のようなディレクトリを想定しています。

```
root
├── composeApp               // Android(Compose) アプリモジュール
│   └── src/androidMain/kotlin/...
├── iosApp                   // iOS アプリプロジェクト
│   └── ...
├── shared                   // KMP 共有ロジック
│   ├── src/commonMain       // 共通 Kotlin コード
│   ├── src/androidMain      // Android 向け実装
│   ├── src/iosMain          // iOS 向け実装 (Native)
│   └── build.gradle.kts
└── build.gradle.kts         // ルートビルド設定
```

- `shared` が最も重要な部分で、KMP のビジネスロジックやインターフェースが含まれます。
- `composeApp` と `iosApp` はそれぞれプラットフォーム固有の実装を含みます。

---

## 動作確認手順

### Android 側
1. Android Studio などでこのプロジェクトを開きます。
2. Gradle Sync を行い、アプリをビルドします。
3. 実機またはエミュレータで実行し、画面に `[Android]The client generated sync/async data` が表示できるか確認してください。

### iOS 側
1. Xcode で `iosApp` プロジェクトを開きます。
2. ビルドし、シミュレータまたは実機で実行します。
3. シミュレータまたは実機で画面に `[iOS]The client generated sync/async data` が表示されるのを確認してください。

---

## 参考

- [Kotlin Multiplatform 公式ドキュメント](https://kotlinlang.org/lp/mobile/)
- [Koin (Kotlin の軽量 DI フレームワーク)](https://insert-koin.io/)
- 「KMPとPlatform間接続」スライド

---

このサンプルを通じて、**KMP からプラットフォーム固有のコードを呼び出すための全体像**と、**インターフェース + DI を活用したアプローチ**の実装イメージを掴んでいただければ幸いです。

> **Key Takeaways**
> - **KMP で共通化しつつプラットフォーム固有のコードを呼び出すには、`expect/actual` とインターフェース + DI という主な 2 つの手段がある。**
> - **既存のライブラリやネイティブコードを活用したい場合はインターフェース + DI が有効。**
> - **KMP ライフを楽しみつつ、各プラットフォームの実装を柔軟に注入しよう！**

---