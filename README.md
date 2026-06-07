# PostlyAndroidXML

A social media Android app built with the classic Android View system (XML layouts) as part of the Postly coding challenge. This is the View-based counterpart to the Jetpack Compose version, sharing the same architecture and feature set.

## Features

- **Login** — authenticate with username/password or continue as a guest
- **Post List** — browse all posts in a `RecyclerView` with user avatars, usernames, titles, and descriptions
- **User Information** — tap any post to view user details in a `BottomSheetDialogFragment`, with email domain validation

## How to Build and Run

```bash
./gradlew assembleDebug      # build the debug APK
./gradlew installDebug       # build and install on a connected device/emulator
```

Or open the project in Android Studio and run the `app` configuration.

- **Min SDK**: 26 (Android 8.0) · **Target/Compile SDK**: 36 · **JDK**: 21

## API Endpoint

This app talks to a small, zero-dependency clone of the challenge API. The base URL is hardcoded to `http://192.168.2.56:3005/`, which is a Node server running on the author's local network — **it is not reachable from other networks**, so the app will fail to load data when cloned elsewhere. (Cleartext HTTP is allowed via `usesCleartextTraffic` for this local setup.)

To run against your own backend, change the base URL in `app/src/main/java/com/preetanshu/postlyandroidxml/services/NetworkService.kt` (`APIEndpoint.getUrl()`). Expected contract:

- `GET /login` — HTTP Basic Auth header (empty credentials = guest) → `{ "api_key": "..." }`
- `GET /posts` — header `x-access-token: <api_key>` → list of posts
- `GET /users` — header `x-access-token: <api_key>` → list of users (each with an `avatar` URL)

## Architecture

The project follows **MVVM** with `StateFlow` for state and a one-shot `SharedFlow` for events.

```
Model          → Kotlin data classes (UserModel, PostModel, LoginResponse)
ViewModel      → Business logic + StateFlow<UiState> + SharedFlow<Event>
View           → Activities / Fragments with ViewBinding, observing flows
```

### Key Design Decisions

- **Dependency Injection — Dagger 2 (KSP)** — singleton services (`NetworkService`, `ImageLoaderService`) are provided by an `AppComponent` built in `PostlyApplication`; view models are created with manual `ViewModelProvider.Factory` classes
- **UiState + Event split** — each screen exposes a `StateFlow<UiState>` for render state and a one-shot event stream (`SharedFlow`) for navigation/toasts, avoiding duplicate handling on configuration change
- **ViewBinding** — type-safe view access without `findViewById`
- **RecyclerView + ListAdapter** — efficient post list rendering with `DiffUtil`
- **Coroutines** — networking runs on `Dispatchers.IO` via `viewModelScope`
- **Zero third-party networking** — `NetworkService` is a thin wrapper over `HttpURLConnection` with `kotlinx.serialization`

## Project Structure

```
app/src/main/java/com/preetanshu/postlyandroidxml/
├── PostlyApplication.kt
├── di/
│   ├── AppComponent.kt
│   └── AppModule.kt
├── models/
│   ├── UserModel.kt
│   ├── PostModel.kt
│   └── LoginResponse.kt
├── services/
│   ├── NetworkService.kt
│   └── ImageLoaderService.kt
├── screens/
│   ├── login/
│   │   ├── LoginActivity.kt
│   │   ├── LoginViewModel.kt
│   │   ├── LoginViewModelFactory.kt
│   │   ├── LoginUiState.kt
│   │   └── LoginEvent.kt
│   ├── postlist/
│   │   ├── PostListActivity.kt
│   │   ├── PostListAdapter.kt
│   │   ├── PostListViewModel.kt
│   │   ├── PostListViewModelFactory.kt
│   │   ├── PostListUiState.kt
│   │   └── PostListEvent.kt
│   └── userinfo/
│       └── UserInfoBottomSheet.kt
└── utilities/
    └── EmailValidator.kt

app/src/main/res/layout/
├── activity_login.xml
├── activity_post_list.xml
├── item_post.xml
└── fragment_user_info.xml
```

## Assumptions

- The guest flow uses empty credentials, which the API accepts
- User avatars are loaded asynchronously with an in-memory cache to avoid redundant network calls
- Posts without a matching user are filtered out rather than displayed with missing data
- Email domain validation only checks the suffix (.com, .net, .biz) as specified, not full RFC 5322 compliance

## Future Improvements

- **Disk Caching** — persist avatar images to disk for offline support
- **Pagination** — load posts in pages with the Paging library
- **Error Retry** — allow users to retry failed network requests from the error state
- **Search and Filter** — search or filter posts by user or keyword
- **Unit Tests** — restore view model and utility tests with mock services
