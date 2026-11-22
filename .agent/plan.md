# Project Plan

Build a To-Do List Android app where the user can add tasks, check them off (mark complete/incomplete), and delete tasks. CRITICAL REQUIREMENT: The app must be large-screen friendly and follow Material 3 Adaptive guidelines (androidx.compose.material3.adaptive) to support BOTH phones and tablets/foldables. On expanded/medium-large window sizes (tablet, foldable, desktop windowing) it must use a genuine TWO-PANE layout: a navigable task list pane on the left and a task editor/detail pane on the right. Do NOT simply stretch the phone layout — use real adaptive scaffolding patterns (e.g., ListDetailPaneScaffold / NavigableListDetailPaneScaffold) so the two panes are side-by-side on wide windows and collapse gracefully into a single-pane list-then-detail navigation flow on compact phone widths. The app should be simple, clean, offline-first, with local persistence of tasks across app restarts. Use Kotlin + Jetpack Compose + Material 3, single-module Android app, Room or DataStore for persistence, ViewModel + StateFlow for state, and Gradle version catalogs. The app name should be "My Ai Todo" matching the existing project.

## Project Brief

# My Ai Todo — Project Brief (MVP)

A minimal, offline-first to-do app built with Kotlin + Jetpack Compose + Material 3, designed large-screen-first: a real two-pane list-detail experience on tablets/foldables/desktop windows that collapses into a single-pane list → detail flow on phones.

---

## Features

1. **Create & Edit Tasks** — Add a task with a title (optionally a short note); tap any task to open and edit it in the detail pane.
2. **Toggle Completion** — Check/uncheck a task from the list to mark it complete or incomplete, with instant visual feedback (strikethrough + tint).
3. **Delete Tasks** — Remove a task from the list, with an undo affordance (snackbar) so destructive actions are recoverable.
4. **Adaptive Two-Pane Layout** — Genuine list-detail adaptive scaffolding: side-by-side list + editor panes on medium/expanded widths, graceful single-pane navigation on compact phone widths. No stretched phone layout.
5. **Offline Persistence** — Tasks survive app restarts; all state is local, no network or account required.

*Out of scope for MVP: due dates/reminders, categories/tags, search & filter, task ordering/reordering, sync/backup, notifications.*

---

## High-Level Technical Stack

| Layer | Technology | Why |
|---|---|---|
| Language | **Kotlin** (2.x, JDK 17) | Modern, coroutine-first. |
| UI toolkit | **Jetpack Compose + Material 3** (`compose-material3`) | Declarative UI, dynamic color, M3 components. |
| Navigation | **Jetpack Navigation 3** (`navigation3-runtime`, `navigation3-ui`) | State-driven: a `NavBackStack` of serializable `NavKey`s rendered by `NavDisplay`; back stack is plain observable state owned by the ViewModel. |
| Adaptive layout | **Compose Material Adaptive** (`adaptive`, `adaptive-layout`, `adaptive-navigation`) | `ListDetailPaneScaffold` / `NavigableListDetailPaneScaffold` for real two-pane list-detail driven by window size class. |
| Architecture | **MVVM**: `ViewModel` + `StateFlow` | Single source of truth; immutable `UiState`; survives configuration changes. |
| Async | **Kotlin Coroutines + Flow** | DB access off the main thread, cold/`StateFlow` streams. |
| Persistence | **Room** (`room-runtime`, `room-ktx`, KSP `room-compiler`) | Explicitly required: offline local persistence of tasks; Flow-returning queries feed the UI directly. |
| Serialization | **kotlinx-serialization** | Nav3 keys must be serializable/savable for back-stack restoration. |
| Build | **Gradle version catalogs** (`gradle/libs.versions.toml`), single-module `:app` | Consistent dependency management; MVP scope. |
| Testing (light) | `kotlinx-coroutines-test`, `turbine`, Compose UI test | Verify ViewModel logic and toggle/delete flows. |

**UI composition strategy**
- One `AppViewModel` owns the task repository stream plus a `SnapshotStateList`/`NavBackStack<NavKey>` of keys (`TaskList`, `TaskDetail(taskId)`).
- `NavDisplay` renders the current key; on compact widths it shows list → detail as separate destinations.
- The two-pane experience uses `ListDetailPaneScaffold` (with a Navigation 3 *scene strategy*) so the same state produces side-by-side panes on medium/expanded windows and single-pane navigation on compact ones.
- New-task creation and the detail pane share a single `TaskEditorPane` composable (empty id = create, existing id = edit) to avoid duplicate screens.

---

## UI Design Image

*Omitted — no design image is available for this brief and the `generate_image` tool is not available in this session.*

## Implementation Steps

### Task_1_DependenciesAndDataLayer: Set up Gradle version catalog and dependencies (Compose BOM, Material3, Material3 Adaptive artifacts, Room + KSP, kotlinx-serialization, lifecycle-viewmodel-compose, coroutines, turbine) and implement the persistence/data layer: TaskEntity, TaskDao with Flow-returning queries, TodoDatabase (Room), and a TaskRepository exposing tasks Flow plus add/update/toggle/delete operations. Also add the Material3 theme (dynamic color support) and MyAiTodoApplication wiring.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - gradle/libs.versions.toml and app/build.gradle.kts declare all required libraries and plugins
  - Room database, DAO and repository compile and expose Flow-based tasks
  - theme with dynamic color applied in MainActivity
  - project builds successfully
- **StartTime:** 2026-11-22 16:16:41 CST

### Task_2_ViewModelAndState: Implement the MVVM layer: immutable TodoUiState, AppViewModel holding the repository task stream as StateFlow plus adaptive list-detail navigation state (selected task id / pane navigation), and task actions (create, edit, toggle completion, delete with undo restore). Include the TaskEditorPane and TaskList/TaskRow composables wired to the ViewModel so a working single-screen list is visible.
- **Status:** PENDING
- **Acceptance Criteria:**
  - AppViewModel exposes StateFlow<TodoUiState> with tasks and selection state
  - add, toggle, delete-with-undo and edit actions update UI state correctly
  - list screen renders tasks with strikethrough when complete
  - project builds successfully

### Task_3_AdaptiveTwoPaneLayout: Build the adaptive layout: calculateWindowSizeClass in MainActivity, and a ListDetailPaneScaffold / NavigableListDetailPaneScaffold host that shows list + editor side by side on Medium/Expanded widths and single-pane list-to-detail navigation on Compact with back handling. Add card-wrapped panes, empty state for the list, and a 'no task selected' placeholder for the detail pane on large screens, plus snackbar undo for deletion.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Two-pane side-by-side layout on Medium/Expanded window widths
  - Single-pane list->detail flow with back navigation on Compact widths
  - Empty state and no-selection placeholder implemented
  - snackbar undo for delete works
  - project builds successfully

### Task_4_AppIconPolish: Low-priority polish: generate/apply a simple app launcher icon (adaptive icon XML with a checklist-style vector foreground and themed background color) and refine Material 3 color scheme/typography for the app.
- **Status:** PENDING
- **Acceptance Criteria:**
  - adaptive launcher icon resources added and referenced in AndroidManifest
  - app theme colors finalized and applied consistently
  - project builds successfully

### Task_5_RunAndVerify: Run and Verify: build the debug APK, install and launch on a compact phone emulator and a tablet/expanded emulator (or resize the emulator window), and instruct critic_agent to verify application stability (no crashes on launch or during add/toggle/delete/edit), confirm alignment with user requirements (all MVP features plus adaptive two-pane behavior), and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - all MVP features (add, check-off, delete with undo, edit) work on device
  - two-pane layout verified on expanded width and single-pane on compact width
  - critic_agent report delivered

