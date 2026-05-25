# StudyTracker - Pomodoro Study App

## Architecture Overview

StudyTracker is an Android application implementing a Pomodoro timer system with persistent data storage, theme switching, and visual analytics. The app follows MVVM architecture with Room database, LiveData, and Foreground Services.

**Tech Stack:** Java, Room 2.6.0, Lifecycle 2.7.0, Material 1.11.0, AndroidX AppCompat 1.6.1

**Themes:** Focus (dark, low distraction) and Review (light, high contrast)

---

## File Documentation

### 1. BaseActivity.java
**Package:** `com.dandroids.studytracker.activities`

**Purpose:** Abstract base class for theme switching and notification permissions.

**Key Methods:**
- `toggleTheme()` - Switches between Focus/Review themes, saves preference, recreates activity
- `requestNotificationPermission()` - Requests POST_NOTIFICATIONS on Android 13+

**SharedPreferences:** `StudyTrackerPrefs` with key `is_focus_theme` (default: true)

---

### 2. PomodoroManager.java
**Package:** `com.dandroids.studytracker.manager`

**Purpose:** Manages Pomodoro cycle with automatic state persistence.

**Cycle Pattern:**
```
Focus (25min) → Short Break (5min) → Focus → Short Break → Focus → Short Break → Focus → Long Break (30min) → reset
```

**Constants:**
| Constant | Value |
|----------|-------|
| FOCUS_DURATION | 25 min |
| SHORT_BREAK_DURATION | 5 min |
| LONG_BREAK_DURATION | 30 min |
| CYCLES_BEFORE_LONG | 4 |

**Public Methods:**
- `advance()` - Moves to next session type, saves state
- `getCurrentDuration()` - Returns duration in ms
- `getCurrentLabel()` - Returns human-readable label
- `reset()` - Resets to cycle 1, Focus type
- `getCurrentType()` - Returns SessionType enum
- `getCurrentCycle()` - Returns current cycle (1-4)

**Persistence:** Saves to `PomodoroPrefs` SharedPreferences

---

### 3. StudyViewModel.java
**Package:** `com.dandroids.studytracker.viewmodel`

**Purpose:** AndroidViewModel providing lifecycle-aware data to UI components.

**LiveData Exposed:**
- `allSessions: LiveData<List<Session>>`
- `allSubjects: LiveData<List<Subject>>`

**Key Methods:**
- `insertSession(Session)` / `updateSession(Session)`
- `insertSubject(Subject)` / `deleteSubject(Subject)`
- `deleteAllSessions()`
- `getSessionsBySubject(long subjectId)` - Returns LiveData filtered by subject
- `getAllSessionsSync()` - Synchronous query for exports

**Threading:** Single-threaded ExecutorService for all DB operations

---

### 4. AppDatabase.java
**Package:** `com.dandroids.studytracker.db`

**Purpose:** Room Database singleton. Version 1.

**Entities:** `Session`, `Subject`

**DAOs:** `SessionDao`, `SubjectDao`

**Singleton Pattern:**
```java
AppDatabase db = AppDatabase.getInstance(context);
```

---

### 5. Session.java (Model)
**Package:** `com.dandroids.studytracker.model`

**Table Structure:**
| Column | Type | Description |
|--------|------|-------------|
| id | long | PRIMARY KEY, auto-generate |
| subjectId | long | FOREIGN KEY → Subject(id), ON DELETE CASCADE |
| startTime | long | Session start timestamp (ms) |
| endTime | long | Session end timestamp (ms) |
| durationMinutes | int | Total duration in minutes |
| completed | boolean | Whether session finished |

**Constructor:**
```java
Session(long subjectId, long startTime)
```

**Helper:**
```java
long getDurationMillis()  // Returns endTime - startTime
```

---

### 6. SessionDao.java
**Package:** `com.dandroids.studytracker.db`

**Queries:**
| Method | Return Type | Description |
|--------|-------------|-------------|
| `insert(Session)` | long | Returns row ID |
| `update(Session)` | void | Updates session |
| `getAllSessions()` | LiveData<List<Session>> | All sessions, newest first |
| `getSessionsBetween(long, long)` | LiveData<List<Session>> | Date range filter |
| `getSessionsBySubject(long)` | LiveData<List<Session>> | Filter by subject |
| `getAllSessionsSync()` | List<Session> | Synchronous version |
| `deleteAll()` | void | Clears all sessions |

---

### 7. TimerService.java
**Package:** `com.dandroids.studytracker.service`

**Purpose:** Foreground service for Pomodoro timer. Supports binding for UI updates.

**Constants:**
| Constant | Value |
|----------|-------|
| CHANNEL_ID | "study_timer_channel" |
| ACTION_PAUSE | "ACTION_PAUSE" |
| ACTION_STOP | "ACTION_STOP" |
| NOTIFICATION_ID | 1 |

**Key Methods:**
- `startTimer(long durationMillis)` - Starts countdown
- `pauseTimer()` / `resumeTimer()` - Control timer
- `stopTimer()` - Stops completely
- `setCallback(TimerCallback)` - Register for tick/finish events
- `isRunning()` / `getRemainingMillis()` - Status methods

**TimerCallback Interface:**
```java
void onTick(long millisRemaining);
void onFinish();
```

**Features:**
- Foreground notification with Pause/Stop actions
- Vibration pattern on completion (3 pulses)
- Binder pattern for Activity binding (`TimerBinder` class)
- Sticky service

**Notification Action:** Clicking notification opens `SessionActivity`

---

### 8. BarChartView.java
**Package:** `com.dandroids.studytracker.views`

**Purpose:** Custom view displaying weekly study minutes as a bar chart (Monday-Sunday).

**Public Method:**
```java
void setSessions(List<Session> sessions)
```
Filters sessions to current week and aggregates durationMinutes per day.

**Visual:**
- Mon-Fri: Blue bars (`#5C6BC0`)
- Sat-Sun: Lighter blue (`#9FA8DA`)
- Minute labels displayed above bars
- Dynamic scaling based on max value

---

### 9. TimerProgressView.java
**Package:** `com.dandroids.studytracker.views`

**Purpose:** Custom circular progress indicator with timer text display.

**Public Methods:**
- `setProgress(long remainingMillis, long totalMillis)` - Updates progress arc and center text (MM:SS)
- `setLabelText(String label)` - Sets text below timer

**Color Coding:**
| Progress | Color |
|----------|-------|
| > 50% | Blue (#5C6BC0) |
| 20% - 50% | Orange (#FF9800) |
| < 20% | Red (#F44336) |

**Display:**
- Large center: Time remaining (MM:SS)
- Small below: Session label (e.g., "Focus")
- Circular progress ring

---

### 10. themes.xml
**Path:** `res/values/themes.xml`

| Theme | Parent | Background | Accent |
|-------|--------|------------|--------|
| Theme.StudyTracker (Focus) | DarkActionBar | #121428 | #5C6BC0 |
| Theme.StudyTracker.Review | Light.DarkActionBar | #FAFAFA | #3F51B5 |

**Switching:** `BaseActivity.toggleTheme()`

---

### 11. colors.xml
**Path:** `res/values/colors.xml`

**Focus Theme:**
- primary: #1B1F3B, background: #121428, accent: #5C6BC0, surface: #1E2240, text: #E8EAF6

**Review Theme:**
- primary: #FFFFFF, background: #FAFAFA, accent: #3F51B5, surface: #FFFFFF, text: #212121

**Shared:**
- timer_running: #4CAF50, timer_paused: #FF9800, chart_bar: #5C6BC0, chart_bar_alt: #9FA8DA

---

### 12. AndroidManifest.xml
**Path:** `/app/src/main/AndroidManifest.xml`

**Permissions:**
```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

**Activities:**
| Activity | Exported | Launch Mode |
|----------|----------|-------------|
| DashboardActivity | true (LAUNCHER) | standard |
| SessionActivity | false | singleTop |
| SessionDetailActivity | false | standard |
| SettingsActivity | false | standard |

**Service:**
```xml
<service android:name=".service.TimerService" 
         android:foregroundServiceType="dataSync" 
         android:exported="false" />
```

**FileProvider:** Configured for file sharing with authority `${applicationId}.fileprovider`

---

## Dependencies

```gradle
dependencies {
    implementation "androidx.room:room-runtime:2.6.0"
    annotationProcessor "androidx.room:room-compiler:2.6.0"
    implementation "androidx.lifecycle:lifecycle-viewmodel:2.7.0"
    implementation "androidx.lifecycle:lifecycle-livedata:2.7.0"
    implementation "com.google.android.material:material:1.11.0"
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "androidx.core:core-ktx:1.12.0"
}
```

---

## Quick Start Guide

**Initialize Database:**
```java
AppDatabase db = AppDatabase.getInstance(context);
```

**Start Timer Session:**
```java
Intent intent = new Intent(context, TimerService.class);
intent.putExtra("DURATION_MILLIS", PomodoroManager.FOCUS_DURATION);
ContextCompat.startForegroundService(context, intent);
```

**Bind to Timer Service:**
```java
bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
```

**Save Completed Session:**
```java
Session session = new Session(subjectId, startTime);
session.endTime = System.currentTimeMillis();
session.durationMinutes = (int)((session.endTime - session.startTime) / 60000);
session.completed = true;
viewModel.insertSession(session);
```

**Switch Theme:**
```java
toggleTheme();  // Activity recreates automatically
```

---

## Data Flow Diagram

```
SessionActivity → TimerService → PomodoroManager
                           ↓
                   StudyViewModel
                           ↓
                   Room Database
                           ↓
        DashboardActivity ← BarChartView
```

---

## Version History

| Version | Date | Description |
|---------|------|-------------|
| 1.0 | Current | Pomodoro timer, dual themes, Room persistence, weekly charts |
```