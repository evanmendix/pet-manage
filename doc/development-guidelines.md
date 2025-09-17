# Development Guidelines

This document outlines the key development guidelines and conventions to be followed for this project.

## UI and Localization

*   **Primary Language**: The user-facing language of the application must be **Traditional Chinese**.
*   **Data Translation**: Any data coming from the backend (e.g., feeding types like `meal`, `snack`) must be translated to Chinese on the client-side before being displayed. Do not store translated strings in the database.
*   **Navigation**: The main navigation of the app should be implemented using a **bottom navigation bar** with icons. Text labels for navigation items should also be in Chinese.

## Timezone

*   **Display Timezone**: All timestamps displayed in the UI must be formatted to **UTC+8**.
*   **Storage Timezone**: Timestamps should be stored in a timezone-agnostic format, such as UTC milliseconds from epoch.
